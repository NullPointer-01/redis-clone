package requests.slave.lists;

import repository.RepositoryManager;
import repository.Storage;
import requests.Request;
import requests.model.Client;
import requests.model.Command;

import java.util.List;

public class LPushSlaveRequest implements Request {
    private final String listKey;
    private final List<String> elements;

    public LPushSlaveRequest(String listKey, List<String> elements) {
        this.listKey = listKey;
        this.elements = elements;
    }

    @Override
    public Command getCommand() {
        return Command.LPUSH;
    }

    @Override
    public void execute(Client client) {
        Storage<String, String> storage = RepositoryManager.getInstance();
        storage.lPush(listKey, elements);
    }
}