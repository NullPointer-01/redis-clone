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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class HSetMasterRequest extends AbstractRequest {
    private final String hashKey;
    private final List<Pair<String, String>> fieldsAndValues;

    public HSetMasterRequest(String hashKey, List<String> args) {
        super(Command.HSET);
        this.hashKey = hashKey;
        this.fieldsAndValues = getFieldsAndValues(args);
    }

    private List<Pair<String, String>> getFieldsAndValues(List<String> args) {
        List<Pair<String, String>> fieldsAndValues = new ArrayList<>(args.size()/2);

        for (int i = 0; i < args.size(); i += 2) {
            fieldsAndValues.add(new Pair<>(args.get(i), args.get(i+1)));
        }

        return fieldsAndValues;
    }

    @Override
    public Response doExecute() {
        Storage<String, String> storage = RepositoryManager.getInstance();
        Integer length = storage.hSet(hashKey, fieldsAndValues);

        return new Response(RespSerializer.asInteger(length));
    }

    @Override
    public void postExecute(Client ignored) {
        List<String> requestParts = new LinkedList<>();
        requestParts.add(command.getName());
        requestParts.add(hashKey);

        List<String> args = new ArrayList<>(fieldsAndValues.size() * 2);
        for (Pair<String, String> pair : fieldsAndValues) {
            args.add(pair.getKey());
            args.add(pair.getValue());
        }
        requestParts.addAll(args);

        byte[] request = RespSerializer.asArray(requestParts).getBytes(StandardCharsets.UTF_8);
        MasterReplicationHandler.getInstance().propagateRequests(request);
        AOFPersistenceHandler.getInstance().appendRequest(String.join(" ", requestParts));
    }
}
