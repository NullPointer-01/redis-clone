package model;

import repository.RepositoryManager;
import repository.Storage;

import java.util.List;

import static util.RespConstants.OK_SIMPLE_STRING;

public class SetRequest extends Request {
    public SetRequest(String key, String value) {
        super(Command.SET, List.of(key, value));
    }

    @Override
    public Response execute() {
        Storage<String, String> storage = RepositoryManager.getInstance();
        String key = args.get(0).toString(), value = args.get(1).toString();

        storage.set(key, value);
        return new Response(OK_SIMPLE_STRING);
    }
}
