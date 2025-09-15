package requests.master.hashes;

import repository.RepositoryManager;
import repository.Storage;
import requests.AbstractRequest;
import requests.model.Command;
import requests.model.Response;
import util.RespSerializer;

import java.util.List;

public class HMGetMasterRequest extends AbstractRequest {
    private final String hashKey;
    private final List<String> fields;

    public HMGetMasterRequest(String hashKey, List<String> fields) {
        super(Command.HMGET);
        this.hashKey = hashKey;
        this.fields = fields;
    }

    @Override
    public Response doExecute() {
        Storage<String, String> storage = RepositoryManager.getInstance();
        List<String> values = storage.hMGet(hashKey, fields);

        return new Response(RespSerializer.asArray(values));
    }
}
