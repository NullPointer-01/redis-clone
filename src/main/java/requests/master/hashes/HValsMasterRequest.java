package requests.master.hashes;

import repository.RepositoryManager;
import repository.Storage;
import requests.AbstractRequest;
import requests.model.Command;
import requests.model.Response;
import util.RespSerializer;

import java.util.List;

public class HValsMasterRequest extends AbstractRequest {
    private final String hashKey;

    public HValsMasterRequest(String hashKey) {
        super(Command.HVALS);
        this.hashKey = hashKey;
    }

    @Override
    public Response doExecute() {
        Storage<String, String> storage = RepositoryManager.getInstance();
        List<String> values = storage.hVals(hashKey);

        return new Response(RespSerializer.asArray(values));
    }
}
