package requests.master.hashes;

import repository.RepositoryManager;
import repository.Storage;
import requests.AbstractRequest;
import requests.model.Client;
import requests.model.Command;
import requests.model.Response;
import service.AOFPersistenceHandler;
import service.MasterReplicationHandler;
import util.RespSerializer;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

public class HDelMasterRequest extends AbstractRequest {
    private final String hashKey;
    private final List<String> fields;

    public HDelMasterRequest(String hashKey, List<String> fields) {
        super(Command.HDEL);
        this.hashKey = hashKey;
        this.fields = fields;
    }

    @Override
    public Response doExecute() {
        Storage<String, String> storage = RepositoryManager.getInstance();
        Integer length = storage.hDel(hashKey, fields);

        return new Response(RespSerializer.asInteger(length));
    }

    @Override
    public void postExecute(Client ignored) {
        List<String> requestParts = new LinkedList<>();
        requestParts.add(command.getName());
        requestParts.add(hashKey);
        requestParts.addAll(fields);

        byte[] request = RespSerializer.asArray(requestParts).getBytes(StandardCharsets.UTF_8);
        MasterReplicationHandler.getInstance().propagateRequests(request);
        AOFPersistenceHandler.getInstance().appendRequest(String.join(" ", requestParts));
    }
}
