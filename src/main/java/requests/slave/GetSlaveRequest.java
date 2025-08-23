package requests.slave;

import repository.RepositoryManager;
import repository.Storage;
import requests.AbstractRequest;
import requests.model.Command;
import requests.model.Response;
import util.RespSerializer;

import java.util.Optional;

import static util.RespConstants.NULL_BULK_STRING;

public class GetSlaveRequest extends AbstractRequest {
    private final String key;

    public GetSlaveRequest(String key) {
        super(Command.GET);
        this.key = key;
    }

    @Override
    public Response doExecute() {
        Storage<String, String> storage = RepositoryManager.getInstance();

        Optional<String> valueOpt = storage.get(key);
        if (valueOpt.isEmpty()) {
            return new Response(NULL_BULK_STRING);
        }

        return new Response(RespSerializer.asBulkString(valueOpt.get()));
    }
}
