package model;

import repository.RepositoryManager;
import repository.Storage;

import static util.RespConstants.OK_SIMPLE_STRING;

public class SetRequest extends Request {
    private final String key;
    private final String value;

    public SetRequest(String key, String value) {
        super(Command.SET);
        this.key = key;
        this.value = value;
    }

    @Override
    public Response execute() {
        Storage<String, String> storage = RepositoryManager.getInstance();
        storage.set(key, value);

        return new Response(OK_SIMPLE_STRING);
    }
}
