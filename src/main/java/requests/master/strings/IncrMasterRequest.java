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
import java.util.Optional;

import static constants.ErrorConstants.ERROR_NOT_AN_INTEGER;

public class IncrMasterRequest extends AbstractRequest {
    private final String key;

    public IncrMasterRequest(String key) {
        super(Command.INCR);
        this.key = key;
    }

    @Override
    public Response doExecute() {
        Storage<String, String> storage = RepositoryManager.getInstance();

        Optional<String> valueOpt = storage.get(key);
        if (valueOpt.isEmpty()) {
            storage.set(key, "1", null);
            return new Response(RespSerializer.asInteger(1));
        }

        try {
            Integer count = Integer.parseInt(valueOpt.get());
            count++;

            storage.set(key, String.valueOf(count), null);
            return new Response(RespSerializer.asInteger(count));
        } catch (NumberFormatException e) {
            return new Response(ERROR_NOT_AN_INTEGER);
        }
    }

    @Override
    public void postExecute(Client ignored) {
        List<String> requestParts = List.of(command.getName(), key);

        byte[] request = RespSerializer.asArray(requestParts).getBytes(StandardCharsets.UTF_8);
        MasterReplicationHandler.getInstance().propagateRequests(request);
        AOFPersistenceHandler.getInstance().appendRequest(String.join(" ", requestParts));
    }
}
