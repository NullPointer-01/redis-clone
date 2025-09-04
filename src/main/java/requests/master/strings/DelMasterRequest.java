package requests.master.strings;

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

    @Override
    public void postExecute(Client ignored) {
        List<String> requestParts = new LinkedList<>();
        requestParts.add(command.getName());
        requestParts.addAll(keys);

        byte[] request = RespSerializer.asArray(requestParts).getBytes(StandardCharsets.UTF_8);
        MasterReplicationHandler.getInstance().propagateRequests(request);
    }
}
