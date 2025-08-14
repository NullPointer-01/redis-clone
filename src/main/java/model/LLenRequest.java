package model;

import repository.RepositoryManager;
import repository.Storage;
import util.RespSerializer;

public class LLenRequest extends Request {
    private final String listKey;

    public LLenRequest(String listKey) {
        super(Command.LLEN);
        this.listKey = listKey;
    }

    @Override
    public Response execute() {
        Storage<String, String> storage = RepositoryManager.getInstance();
        Integer length = storage.lLen(listKey);

        return new Response(RespSerializer.asInteger(length));
    }
}
