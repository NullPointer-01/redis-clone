package requests.slave.zsets;

import repository.RepositoryManager;
import repository.Storage;
import requests.Request;
import requests.model.Client;
import requests.model.Command;

public class ZRemSlaveRequest implements Request {
    private final String zSetKey;
    private final String member;

    public ZRemSlaveRequest(String zSetKey, String member) {
        this.zSetKey = zSetKey;
        this.member = member;
    }

    @Override
    public Command getCommand() {
        return Command.ZREM;
    }

    @Override
    public void execute(Client client) {
        Storage<String, String> storage = RepositoryManager.getInstance();
        storage.zRem(zSetKey, member);
    }
}