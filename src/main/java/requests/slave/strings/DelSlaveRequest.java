package requests.slave.strings;

import repository.RepositoryManager;
import repository.Storage;
import requests.Request;
import requests.model.Client;
import requests.model.Command;

import java.util.List;

public class DelSlaveRequest implements Request {
    private final List<String> keys;

    public DelSlaveRequest(List<String> keys) {
        this.keys = keys;
    }

    @Override
    public Command getCommand() {
        return Command.DEL;
    }

    @Override
    public void execute(Client client) {
        Storage<String, String> storage = RepositoryManager.getInstance();
        storage.delete(keys);
    }
}
