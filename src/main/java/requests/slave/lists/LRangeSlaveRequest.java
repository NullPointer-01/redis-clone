package requests.slave.lists;

import repository.RepositoryManager;
import repository.Storage;
import requests.AbstractRequest;
import requests.model.Command;
import requests.model.Response;
import util.RespSerializer;

import java.util.List;

import static util.RespConstants.EMPTY_RESP_ARRAY;

public class LRangeSlaveRequest extends AbstractRequest {
    private final String listKey;
    private final int startIdx;
    private final int endIdx;

    public LRangeSlaveRequest(String listKey, int startIdx, int endIdx) {
        super(Command.LRANGE);
        this.listKey = listKey;
        this.startIdx = startIdx;
        this.endIdx = endIdx;
    }

    @Override
    public Response doExecute() {
        Storage<String, String> storage = RepositoryManager.getInstance();
        List<String> elements = storage.lRange(listKey, startIdx, endIdx);

        if (elements.isEmpty()) return new Response(EMPTY_RESP_ARRAY);

        return new Response(RespSerializer.asArray(elements));
    }
}
