package requests.master.hashes;

import repository.RepositoryManager;
import repository.Storage;
import requests.AbstractRequest;
import requests.model.Command;
import requests.model.Response;
import util.RespSerializer;

import java.util.List;

public class HKeysMasterRequest extends AbstractRequest {
    private final String hashKey;

    public HKeysMasterRequest(String hashKey) {
        super(Command.HKEYS);
        this.hashKey = hashKey;
    }

    @Override
    public Response doExecute() {
        Storage<String, String> storage = RepositoryManager.getInstance();
        List<String> keys = storage.hKeys(hashKey);

        return new Response(RespSerializer.asArray(keys));
    }
}
