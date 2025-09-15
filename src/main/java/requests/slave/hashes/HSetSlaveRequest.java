package requests.slave.hashes;

import ds.Pair;
import repository.RepositoryManager;
import repository.Storage;
import requests.Request;
import requests.model.Client;
import requests.model.Command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HSetSlaveRequest implements Request {
    private final String hashKey;
    private final List<Pair<String, String>> fieldsAndValues;

    public HSetSlaveRequest(String hashKey, List<String> args) {
        super();
        this.hashKey = hashKey;
        this.fieldsAndValues = getFieldsAndValues(args);
    }

    private List<Pair<String, String>> getFieldsAndValues(List<String> args) {
        List<Pair<String, String>> fieldsAndValues = new ArrayList<>(args.size() / 2);

        for (int i = 0; i < args.size(); i += 2) {
            fieldsAndValues.add(new Pair<>(args.get(i), args.get(i + 1)));
        }

        return fieldsAndValues;
    }

    @Override
    public Command getCommand() {
        return Command.HSET;
    }

    @Override
    public void execute(Client client) throws IOException {
        Storage<String, String> storage = RepositoryManager.getInstance();
        storage.hSet(hashKey, fieldsAndValues);
    }
}