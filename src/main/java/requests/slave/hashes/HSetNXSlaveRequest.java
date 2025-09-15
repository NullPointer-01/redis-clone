package requests.slave.hashes;

import repository.RepositoryManager;
import repository.Storage;
import requests.Request;
import requests.model.Client;
import requests.model.Command;

import java.io.IOException;

public class HSetNXSlaveRequest implements Request {
    private final String hashKey;
    private final String field;
    private final String value;

    public HSetNXSlaveRequest(String hashKey, String field, String value) {
        this.hashKey = hashKey;
        this.field = field;
        this.value = value;
    }

    @Override
    public Command getCommand() {
        return Command.HSETNX;
    }

    @Override
    public void execute(Client client) throws IOException {
        Storage<String, String> storage = RepositoryManager.getInstance();
        storage.hSetNX(hashKey, field, value);
    }
}