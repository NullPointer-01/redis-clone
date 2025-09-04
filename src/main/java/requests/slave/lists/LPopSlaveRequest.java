package requests.slave.lists;

import repository.RepositoryManager;
import repository.Storage;
import requests.Request;
import requests.model.Client;
import requests.model.Command;

import java.util.Objects;

public class LPopSlaveRequest implements Request {
    private final String listKey;
    private final Integer count;

    public LPopSlaveRequest(String listKey, Integer count) {
        this.listKey = listKey;
        this.count = count;
    }

    @Override
    public Command getCommand() {
        return Command.LPOP;
    }

    @Override
    public void execute(Client client) {
        Storage<String, String> storage = RepositoryManager.getInstance();
        storage.lPop(listKey, Objects.requireNonNullElse(count, 1));
    }
}