package requests.master.hashes;

import ds.Pair;
import repository.RepositoryManager;
import repository.Storage;
import requests.AbstractRequest;
import requests.model.Command;
import requests.model.Response;
import util.RespSerializer;

import java.util.ArrayList;
import java.util.List;

public class HGetAllMasterRequest extends AbstractRequest {
    private final String hashKey;

    public HGetAllMasterRequest(String hashKey) {
        super(Command.HGETALL);
        this.hashKey = hashKey;
    }

    @Override
    public Response doExecute() {
        Storage<String, String> storage = RepositoryManager.getInstance();
        List<Pair<String, String>> fieldsAndValues = storage.hGetAll(hashKey);

        List<String> elements = new ArrayList<>(fieldsAndValues.size() * 2);
        for (Pair<String, String> pair : fieldsAndValues) {
            elements.add(pair.getKey());
            elements.add(pair.getValue());
        }

        return new Response(RespSerializer.asArray(elements));
    }
}
