package requests.master.hashes;

import repository.RepositoryManager;
import repository.Storage;
import requests.AbstractRequest;
import requests.model.Command;
import requests.model.Response;
import util.RespSerializer;

import java.util.Optional;

public class HExistsMasterRequest extends AbstractRequest {
    private final String hashKey;
    private final String field;

    public HExistsMasterRequest(String hashKey, String field) {
        super(Command.HEXISTS);
        this.hashKey = hashKey;
        this.field = field;
    }

    @Override
    public Response doExecute() {
        Storage<String, String> storage = RepositoryManager.getInstance();
        Optional<String> valueOpt = storage.hGet(hashKey, field);
        if (valueOpt.isEmpty()) {
            return new Response(RespSerializer.asInteger(0));
        }

        return new Response(RespSerializer.asInteger(1));
    }
}
