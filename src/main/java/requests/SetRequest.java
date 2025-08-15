package requests;

import repository.RepositoryManager;
import repository.Storage;
import requests.model.Command;
import requests.model.Response;

import static util.RespConstants.OK_SIMPLE_STRING;

public class SetRequest extends Request {
    private final String key;
    private final String value;
    private final Long timeToExpireInMillis;

    public SetRequest(String key, String value, Long timeToExpireInMillis) {
        super(Command.SET);
        this.key = key;
        this.value = value;
        this.timeToExpireInMillis = timeToExpireInMillis;
    }

    @Override
    public Response execute() {
        Storage<String, String> storage = RepositoryManager.getInstance();
        storage.set(key, value, timeToExpireInMillis);

        return new Response(OK_SIMPLE_STRING);
    }
}
