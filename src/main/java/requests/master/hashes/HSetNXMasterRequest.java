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

public class HSetNXMasterRequest extends AbstractRequest {
    private final String hashKey;
    private final String field;
    private final String value;

    public HSetNXMasterRequest(String hashKey, String field, String value) {
        super(Command.HSETNX);
        this.hashKey = hashKey;
        this.field = field;
        this.value = value;
    }

    @Override
    public Response doExecute() {
        Storage<String, String> storage = RepositoryManager.getInstance();
        Integer length = storage.hSetNX(hashKey, field, value);

        return new Response(RespSerializer.asInteger(length));
    }

    @Override
    public void postExecute(Client ignored) {
        List<String> requestParts = new LinkedList<>();
        requestParts.add(command.getName());
        requestParts.add(hashKey);
        requestParts.add(field);
        requestParts.add(value);

        byte[] request = RespSerializer.asArray(requestParts).getBytes(StandardCharsets.UTF_8);
        MasterReplicationHandler.getInstance().propagateRequests(request);
        AOFPersistenceHandler.getInstance().appendRequest(String.join(" ", requestParts));
    }
}
