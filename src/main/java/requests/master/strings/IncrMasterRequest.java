package requests.master.strings;

import repository.RepositoryManager;
import repository.Storage;
import requests.AbstractRequest;
import requests.model.Command;
import requests.model.Response;
import util.RespSerializer;

import java.util.Optional;

import static util.RespConstants.ERROR_NOT_AN_INTEGER;

public class IncrMasterRequest extends AbstractRequest {
    private final String key;

    public IncrMasterRequest(String key) {
        super(Command.INCR);
        this.key = key;
    }

    @Override
    public Response doExecute() {
        Storage<String, String> storage = RepositoryManager.getInstance();

        Optional<String> valueOpt = storage.get(key);
        if (valueOpt.isEmpty()) {
            storage.set(key, "1", null);
            return new Response(RespSerializer.asInteger(1));
        }

        try {
            Integer count = Integer.parseInt(valueOpt.get());
            count++;

            storage.set(key, String.valueOf(count), null);
            return new Response(RespSerializer.asInteger(count));
        } catch (NumberFormatException e) {
            return new Response(ERROR_NOT_AN_INTEGER);
        }
    }
}
