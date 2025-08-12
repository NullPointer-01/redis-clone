package model;

import repository.RepositoryManager;
import repository.Storage;
import util.RespSerializer;

import java.util.Optional;

import static util.RespConstants.NULL_BULK_STRING;

public class GetRequest extends Request {
    private final String key;

    public GetRequest(String key) {
        super(Command.GET);
        this.key = key;
    }

    @Override
    public Response execute() {
        Storage<String, String> storage = RepositoryManager.getInstance();

        Optional<String> valueOpt = storage.get(key);
        if (valueOpt.isEmpty()) {
            return new Response(NULL_BULK_STRING);
        }

        return new Response(RespSerializer.asBulkString(valueOpt.get()));
    }
}
