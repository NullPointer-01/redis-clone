package requests.master;

import ds.Stream;
import repository.RepositoryManager;
import repository.Storage;
import requests.AbstractRequest;
import requests.model.Command;
import requests.model.Response;

import java.util.Optional;

import static util.RespConstants.*;

public class TypeMasterCommand extends AbstractRequest {
    private final String key;

    public TypeMasterCommand(String key) {
        super(Command.GET);
        this.key = key;
    }

    @Override
    public Response doExecute() {
        Storage<String, String> storage = RepositoryManager.getInstance();

        Optional<String> valueOpt = storage.get(key);
        if (valueOpt.isPresent()) {
            return new Response(TYPE_STRING_SIMPLE_STRING);
        }

        Optional<Stream<String, String>> streamOpt = storage.getStream(key);
        if (streamOpt.isPresent()) {
            return new Response(TYPE_STREAM_SIMPLE_STRING);
        }

        return new Response(TYPE_NONE_SIMPLE_STRING);
    }
}
