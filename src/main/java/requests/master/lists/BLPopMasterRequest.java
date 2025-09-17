package requests.master.lists;

import repository.RepositoryManager;
import repository.Storage;
import requests.Request;
import requests.model.Client;
import requests.model.Command;
import requests.model.Response;
import service.BlockingOpsManager;
import util.RespSerializer;

import java.io.IOException;
import java.util.List;

public class BLPopMasterRequest implements Request {
    private final List<String> keys;
    private final Long timeout;

    public BLPopMasterRequest(List<String> keys, Long timeout) {
        this.keys = keys;
        this.timeout = timeout;
    }

    @Override
    public Command getCommand() {
        return Command.BLPOP;
    }

    @Override
    public void execute(Client client) throws IOException {
        Storage<String, String> storage = RepositoryManager.getInstance();

        for (String listKey : keys) {
            List<String> elems = storage.lPop(listKey, 1);
            if (!elems.isEmpty()) {
                Response response = new Response(RespSerializer.asArray(List.of(listKey, elems.get(0))));
                client.write(response.getResponse());
                return;
            }
        }

        BlockingOpsManager.getInstance().handleBlockingRequest(this, client);
    }

    public List<String> getKeys() {
        return keys;
    }

    public Long getTimeout() {
        return timeout;
    }
}
