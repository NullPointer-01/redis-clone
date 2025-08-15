package requests;

import repository.RepositoryManager;
import repository.Storage;
import requests.model.Command;
import requests.model.Response;
import util.RespSerializer;

import java.util.List;

public class DelRequest extends Request {
    private final List<String> keys;

    public DelRequest(List<String> keys) {
        super(Command.DEL);
        this.keys = keys;
    }

    @Override
    public Response execute() {
        Storage<String, String> storage = RepositoryManager.getInstance();

        Integer count = storage.delete(keys);
        return new Response(RespSerializer.asInteger(count));
    }
}
