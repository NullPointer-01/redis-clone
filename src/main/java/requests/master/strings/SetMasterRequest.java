package requests.master.strings;

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
import java.util.List;

import static util.RespConstants.OK_SIMPLE_STRING;

public class SetMasterRequest extends AbstractRequest {
    private final String key;
    private final String value;
    private final Long timeToExpireInMillis;

    public SetMasterRequest(String key, String value, Long timeToExpireInMillis) {
        super(Command.SET);
        this.key = key;
        this.value = value;
        this.timeToExpireInMillis = timeToExpireInMillis;
    }

    @Override
    public Response doExecute() {
        Storage<String, String> storage = RepositoryManager.getInstance();
        storage.set(key, value, timeToExpireInMillis);

        return new Response(OK_SIMPLE_STRING);
    }

    @Override
    public void postExecute(Client ignored) {
        List<String> requestParts;
        if (timeToExpireInMillis == null) {
            requestParts = List.of(command.getName(), key, value);
        } else {
            requestParts = List.of(command.getName(), key, value, timeToExpireInMillis.toString());
        }

        byte[] request = RespSerializer.asArray(requestParts).getBytes(StandardCharsets.UTF_8);
        MasterReplicationHandler.getInstance().propagateRequests(request);
        AOFPersistenceHandler.getInstance().appendRequest(String.join(" ", requestParts));
    }
}
