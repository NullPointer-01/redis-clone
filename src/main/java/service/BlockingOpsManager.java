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
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static util.RespConstants.NULL_BULK_STRING;

public class BlockingOpsManager {
    private static BlockingOpsManager instance;

    private final ExecutorService executorService;
    private final ScheduledExecutorService scheduler;
    private final BlockingInfo info;

    // Private constructor to enforce singleton pattern
    private BlockingOpsManager() {
        this.info = new BlockingInfo();
        this.executorService = Executors.newSingleThreadExecutor();
        this.scheduler = Executors.newScheduledThreadPool(1);
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
                long timeout = req.getTimeout();

                if (timeout > 0) {
                    scheduler.schedule(getScheduledUnblockTask(client), timeout, TimeUnit.SECONDS);
                }

                info.block(client, keys);
                client.blockClient();
            }
        }
    }

    public void handleUnblockingRequest(Request request) {
        Runnable runnable = () -> {
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
        };

        submitTask(runnable);
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

    private void submitTask(Runnable runnable) {
        executorService.submit(runnable);
    }

    private Runnable getScheduledUnblockTask(Client client) {
        return new Task(client, this);
    }

    public void shutdown() {
        executorService.shutdown();
        scheduler.shutdown();
    }

    private static class BlockingInfo {
        private final Map<Client, Set<String>> blockedClientsVsKeys;
        private final Map<String, Set<Client>> keysVsBlockedClients;

        private final Map<Client, Long> clientsVsCounter;

        BlockingInfo() {
            this.blockedClientsVsKeys = new HashMap<>();
            this.keysVsBlockedClients = new HashMap<>();

            this.clientsVsCounter = new HashMap<>();
        }

        void block(Client client, List<String> keys) {
            blockedClientsVsKeys.computeIfAbsent(client, k -> new LinkedHashSet<>());
            blockedClientsVsKeys.get(client).addAll(keys);

            for (String key : keys) {
                keysVsBlockedClients.computeIfAbsent(key, k -> new LinkedHashSet<>());
                keysVsBlockedClients.get(key).add(client);
            }
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

                Long counter = clientsVsCounter.getOrDefault(client, 0L);
                clientsVsCounter.put(client, ++counter);
            }
        }

        boolean isKeyBlocked(String key) {
            return keysVsBlockedClients.containsKey(key);
        }

        Collection<Client> getBlockedClients(String key) {
            return keysVsBlockedClients.get(key);
        }

        long getLastExecutedCntr(Client client) {
            return clientsVsCounter.getOrDefault(client, 0L);
        }
    }

    private static class Task implements Runnable {
        Client client;
        long counter;
        BlockingOpsManager manager;

        Task(Client client, BlockingOpsManager manager) {
            this.client = client;
            this.counter = manager.info.getLastExecutedCntr(client) + 1;
            this.manager = manager;
        }

        @Override
        public void run() {
            Runnable runnable = () -> {
                long lastExecutedCounter = manager.info.getLastExecutedCntr(client);
                if (counter <= lastExecutedCounter) return;

                Response response = new Response(NULL_BULK_STRING);
                try {
                    client.write(response.getResponse());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                client.unblockClient();
                manager.info.unblock(List.of(client));
            };

            manager.submitTask(runnable);
        }
    }
}
