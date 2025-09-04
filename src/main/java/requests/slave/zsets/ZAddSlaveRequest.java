package requests.slave.zsets;

import repository.RepositoryManager;
import repository.Storage;
import requests.Request;
import requests.model.Client;
import requests.model.Command;

public class ZAddSlaveRequest  implements Request {
    private final String zSetKey;
    private final String member;
    private final double score;

    public ZAddSlaveRequest(String zSetKey, double score, String member) {
        this.zSetKey = zSetKey;
        this.score = score;
        this.member = member;
    }

    @Override
    public Command getCommand() {
        return Command.ZADD;
    }

    @Override
    public void execute(Client client) {
        Storage<String, String> storage = RepositoryManager.getInstance();
        storage.zAdd(zSetKey, member, score);

    }
}