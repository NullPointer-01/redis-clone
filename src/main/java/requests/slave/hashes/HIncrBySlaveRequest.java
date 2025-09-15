package requests.slave.hashes;

import ds.Pair;
import repository.RepositoryManager;
import repository.Storage;
import requests.Request;
import requests.model.Client;
import requests.model.Command;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class HIncrBySlaveRequest implements Request {
    private final String hashKey;
    private final String field;
    private final int increment;

    public HIncrBySlaveRequest(String hashKey, String field, Integer increment) {
        this.hashKey = hashKey;
        this.field = field;
        this.increment = increment;
    }

    @Override
    public Command getCommand() {
        return Command.HINCRBY;
    }

    @Override
    public void execute(Client client) throws IOException {
        Storage<String, String> storage = RepositoryManager.getInstance();
        Optional<String> valueOpt = storage.hGet(hashKey, field);

        if (valueOpt.isEmpty()) {
            Pair<String, String> pair = new Pair<>(field, String.valueOf(increment));
            storage.hSet(hashKey, List.of(pair));

            return;
        }

        Integer count = Integer.parseInt(valueOpt.get());
        count += increment;
        Pair<String, String> pair = new Pair<>(field, String.valueOf(count));

        storage.hSet(hashKey, List.of(pair));
    }
}