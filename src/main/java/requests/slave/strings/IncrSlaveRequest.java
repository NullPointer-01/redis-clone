package requests.slave.strings;

import repository.RepositoryManager;
import repository.Storage;
import requests.Request;
import requests.model.Client;
import requests.model.Command;

import java.util.Optional;

public class IncrSlaveRequest implements Request {
    private final String key;

    public IncrSlaveRequest(String key) {
        this.key = key;
    }

    @Override
    public Command getCommand() {
        return Command.INCR;
    }

    @Override
    public void execute(Client client) {
        Storage<String, String> storage = RepositoryManager.getInstance();

        Optional<String> valueOpt = storage.get(key);
        if (valueOpt.isEmpty()) {
            storage.set(key, "1", null);
        } else {
            Integer count = Integer.parseInt(valueOpt.get());
            count++;

            storage.set(key, String.valueOf(count), null);
        }
    }
}
