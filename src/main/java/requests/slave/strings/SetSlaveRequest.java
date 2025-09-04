package requests.slave.strings;

import repository.RepositoryManager;
import repository.Storage;
import requests.Request;
import requests.model.Client;
import requests.model.Command;

public class SetSlaveRequest implements Request {
    private final String key;
    private final String value;
    private final Long timeToExpireInMillis;

    public SetSlaveRequest(String key, String value, Long timeToExpireInMillis) {
        this.key = key;
        this.value = value;
        this.timeToExpireInMillis = timeToExpireInMillis;
    }

    @Override
    public Command getCommand() {
        return Command.SET;
    }

    @Override
    public void execute(Client client) {
        Storage<String, String> storage = RepositoryManager.getInstance();
        storage.set(key, value, timeToExpireInMillis);
    }
}
