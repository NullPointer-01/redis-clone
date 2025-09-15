package requests.master.hashes;

import ds.Pair;
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
import java.util.Optional;

import static constants.ErrorConstants.ERROR_HASH_NOT_AN_INTEGER;

public class HIncrByMasterRequest extends AbstractRequest {
    private final String hashKey;
    private final String field;
    private final int increment;
    private boolean isSuccess;

    public HIncrByMasterRequest(String hashKey, String field, Integer increment) {
        super(Command.HINCRBY);
        this.hashKey = hashKey;
        this.field = field;
        this.increment = increment;
    }

    @Override
    public Response doExecute() {
        Storage<String, String> storage = RepositoryManager.getInstance();
        Optional<String> valueOpt = storage.hGet(hashKey, field);

        if (valueOpt.isEmpty()) {
            Pair<String, String> pair = new Pair<>(field, String.valueOf(increment));
            storage.hSet(hashKey, List.of(pair));
            isSuccess = true;

            return new Response(RespSerializer.asInteger(increment));
        }

        try {
            Integer count = Integer.parseInt(valueOpt.get());
            count += increment;
            Pair<String, String> pair = new Pair<>(field, String.valueOf(count));

            storage.hSet(hashKey, List.of(pair));
            isSuccess = true;

            return new Response(RespSerializer.asInteger(count));
        } catch (NumberFormatException e) {
            return new Response(ERROR_HASH_NOT_AN_INTEGER);
        }
    }

    @Override
    public void postExecute(Client ignored) {
        if (!isSuccess) return;

        List<String> requestParts = new LinkedList<>();
        requestParts.add(command.getName());
        requestParts.add(hashKey);
        requestParts.add(field);
        requestParts.add(String.valueOf(increment));

        byte[] request = RespSerializer.asArray(requestParts).getBytes(StandardCharsets.UTF_8);
        MasterReplicationHandler.getInstance().propagateRequests(request);
        AOFPersistenceHandler.getInstance().appendRequest(String.join(" ", requestParts));
    }
}
