package requests.master.hashes;

import repository.RepositoryManager;
import repository.Storage;
import requests.AbstractRequest;
import requests.model.Command;
import requests.model.Response;
import util.RespSerializer;

public class HLenMasterRequest extends AbstractRequest {
    private final String hashKey;

    public HLenMasterRequest(String hashKey) {
        super(Command.HLEN);
        this.hashKey = hashKey;
    }

    @Override
    public Response doExecute() {
        Storage<String, String> storage = RepositoryManager.getInstance();
        Integer length = storage.hLen(hashKey);

        return new Response(RespSerializer.asInteger(length));
    }
}
