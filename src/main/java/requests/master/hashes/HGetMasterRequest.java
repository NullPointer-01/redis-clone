package requests.master.hashes;

import repository.RepositoryManager;
import repository.Storage;
import requests.AbstractRequest;
import requests.model.Command;
import requests.model.Response;
import util.RespSerializer;

import java.util.Optional;

import static util.RespConstants.NULL_BULK_STRING;

public class HGetMasterRequest extends AbstractRequest {
    private final String hashKey;
    private final String field;

    public HGetMasterRequest(String hashKey, String field) {
        super(Command.HGET);
        this.hashKey = hashKey;
        this.field = field;
    }

    @Override
    public Response doExecute() {
        Storage<String, String> storage = RepositoryManager.getInstance();
        Optional<String> valueOpt = storage.hGet(hashKey, field);
        if (valueOpt.isEmpty()) {
            return new Response(NULL_BULK_STRING);
        }

        return new Response(RespSerializer.asBulkString(valueOpt.get()));
    }
}
