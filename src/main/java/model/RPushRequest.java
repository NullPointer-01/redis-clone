package model;

import repository.RepositoryManager;
import repository.Storage;
import util.RespSerializer;

import java.util.List;

public class RPushRequest extends Request {
    private final String listKey;
    private final List<String> elements;

    public RPushRequest(String listKey, List<String> elements) {
        super(Command.RPUSH);
        this.listKey = listKey;
        this.elements = elements;
    }

    @Override
    public Response execute() {
        Storage<String, String> storage = RepositoryManager.getInstance();
        Integer length = storage.rPush(listKey, elements);

        return new Response(RespSerializer.asInteger(length));
    }
}
