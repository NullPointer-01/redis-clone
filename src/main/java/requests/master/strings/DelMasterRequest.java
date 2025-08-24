package requests.master.strings;

import repository.RepositoryManager;
import repository.Storage;
import requests.AbstractRequest;
import requests.model.Command;
import requests.model.Response;
import util.RespSerializer;

import java.util.List;

public class DelMasterRequest extends AbstractRequest {
    private final List<String> keys;

    public DelMasterRequest(List<String> keys) {
        super(Command.DEL);
        this.keys = keys;
    }

    @Override
    public Response doExecute() {
        Storage<String, String> storage = RepositoryManager.getInstance();

        Integer count = storage.delete(keys);
        return new Response(RespSerializer.asInteger(count));
    }
}
