package requests.slave.hashes;

import repository.RepositoryManager;
import repository.Storage;
import requests.Request;
import requests.model.Client;
import requests.model.Command;

import java.io.IOException;
import java.util.List;

public class HDelSlaveRequest implements Request {
    private final String hashKey;
    private final List<String> fields;

    public HDelSlaveRequest(String hashKey, List<String> fields) {
        this.hashKey = hashKey;
        this.fields = fields;
    }

    @Override
    public Command getCommand() {
        return Command.HDEL;
    }

    @Override
    public void execute(Client client) throws IOException {
        Storage<String, String> storage = RepositoryManager.getInstance();
        storage.hDel(hashKey, fields);
    }
}