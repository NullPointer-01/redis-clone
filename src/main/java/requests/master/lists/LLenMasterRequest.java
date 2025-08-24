package requests.master.lists;

import repository.RepositoryManager;
import repository.Storage;
import requests.AbstractRequest;
import requests.model.Command;
import requests.model.Response;
import util.RespSerializer;

public class LLenMasterRequest extends AbstractRequest {
    private final String listKey;

    public LLenMasterRequest(String listKey) {
        super(Command.LLEN);
        this.listKey = listKey;
    }

    @Override
    public Response doExecute() {
        Storage<String, String> storage = RepositoryManager.getInstance();
        Integer length = storage.lLen(listKey);

        return new Response(RespSerializer.asInteger(length));
    }
}
