package requests.master;

import repository.RepositoryManager;
import repository.Storage;
import requests.AbstractRequest;
import requests.model.Command;
import requests.model.Response;
import util.RespSerializer;

import java.util.List;

public class LPushMasterRequest extends AbstractRequest {
    private final String listKey;
    private final List<String> elements;

    public LPushMasterRequest(String listKey, List<String> elements) {
        super(Command.LPUSH);
        this.listKey = listKey;
        this.elements = elements;
    }

    @Override
    public Response doExecute() {
        Storage<String, String> storage = RepositoryManager.getInstance();
        Integer length = storage.lPush(listKey, elements);

        return new Response(RespSerializer.asInteger(length));
    }
}
