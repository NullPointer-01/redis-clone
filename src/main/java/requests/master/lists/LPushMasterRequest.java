package requests.master.lists;

import repository.RepositoryManager;
import repository.Storage;
import requests.AbstractRequest;
import requests.model.Client;
import requests.model.Command;
import requests.model.Response;
import service.MasterReplicationHandler;
import util.RespSerializer;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
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

    @Override
    public void postExecute(Client ignored) {
        List<String> requestParts = new LinkedList<>();
        requestParts.add(command.getName());
        requestParts.add(listKey);
        requestParts.addAll(elements);

        byte[] request = RespSerializer.asArray(requestParts).getBytes(StandardCharsets.UTF_8);
        MasterReplicationHandler.getInstance().propagateRequests(request);
    }
}
