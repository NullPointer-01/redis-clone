package service;

import repository.RepositoryManager;
import repository.Storage;
import requests.Request;
import requests.master.lists.BLPopMasterRequest;
import requests.master.lists.LPopMasterRequest;
import requests.master.lists.LPushMasterRequest;
import requests.master.lists.RPushMasterRequest;
import requests.model.Client;
import requests.model.Command;
import requests.model.Response;
import util.RespSerializer;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BlockingOpsManager {
    private static BlockingOpsManager instance;

    private final ExecutorService executorService;
    private final BlockingInfo info;

    // Private constructor to enforce singleton pattern
    private BlockingOpsManager() {
        this.info = new BlockingInfo();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public static BlockingOpsManager getInstance() {
        if (instance == null) {
            instance = new BlockingOpsManager();
        }

        return instance;
    }

    public void handleBlockingRequest(Request request, Client client) {
        Command command = request.getCommand();

        switch (command) {
            case BLPOP -> {
                BLPopMasterRequest req = (BLPopMasterRequest) request;
                List<String> keys = req.getKeys();

                info.block(client, keys);
                client.blockClient();
            }
        }
    }

    public void handleUnblockingRequest(Request request) {
        executorService.submit(() -> {
            Command command = request.getCommand();

            try {
                switch (command) {
                    case LPUSH:
                        handleBLPopRequest(((LPushMasterRequest) request).getListKey());
                        break;
                    case RPUSH:
                        handleBLPopRequest(((RPushMasterRequest) request).getListKey());
                        break;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void handleBLPopRequest(String listKey) throws IOException {
        if (!info.isKeyBlocked(listKey)) return;

        Storage<String, String> storage = RepositoryManager.getInstance();
        List<Client> unblockedClients = new LinkedList<>();

        Integer lLen = storage.lLen(listKey);
        if (lLen == 0) {
            return;
        }

        Collection<Client> blockedClients = info.getBlockedClients(listKey);
        for (Client client : blockedClients) {
            if (storage.lLen(listKey) == 0) {
                break;
            }

            List<String> elems = storage.lPop(listKey, 1);

            Response response = new Response(RespSerializer.asArray(List.of(listKey, elems.get(0))));
            client.write(response.getResponse());
            client.unblockClient();

            unblockedClients.add(client);
        }

        info.unblock(unblockedClients);

        LPopMasterRequest lPopRequest = new LPopMasterRequest(listKey, unblockedClients.size());
        Client dummy = new Client(null);

        lPopRequest.postExecute(dummy);
    }

    private static class BlockingInfo {
        private final Map<Client, Set<String>> blockedClientsVsKeys;
        private final Map<String, Set<Client>> keysVsBlockedClients;

        BlockingInfo() {
            this.blockedClientsVsKeys = new HashMap<>();
            this.keysVsBlockedClients = new HashMap<>();
        }

        void block(Client client, List<String> keys) {
            blockedClientsVsKeys.computeIfAbsent(client, k -> new LinkedHashSet<>());
            blockedClientsVsKeys.get(client).addAll(keys);

            for (String key : keys) {
                keysVsBlockedClients.computeIfAbsent(key, k -> new LinkedHashSet<>());
                keysVsBlockedClients.get(key).add(client);
            }
        }

        boolean isKeyBlocked(String key) {
            return keysVsBlockedClients.containsKey(key);
        }

        Collection<Client> getBlockedClients(String key) {
            return keysVsBlockedClients.get(key);
        }

        void unblock(Collection<Client> clients) {
            for (Client client : clients) {
                Set<String> blockedKeys = blockedClientsVsKeys.get(client);

                for (String key : blockedKeys) {
                    keysVsBlockedClients.get(key).remove(client);

                    if (keysVsBlockedClients.get(key).isEmpty()) {
                        keysVsBlockedClients.remove(key);
                    }
                }

                blockedClientsVsKeys.remove(client);
            }
        }
    }
}
