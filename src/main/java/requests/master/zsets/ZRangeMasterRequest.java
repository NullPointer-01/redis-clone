package requests.master.zsets;


import repository.RepositoryManager;
import repository.Storage;
import requests.AbstractRequest;
import requests.model.Command;
import requests.model.Response;
import util.RespSerializer;

import java.util.List;

import static util.RespConstants.EMPTY_RESP_ARRAY;

public class ZRangeMasterRequest extends AbstractRequest {
    private final String zSetKey;
    private final int startIdx;
    private final int endIdx;

    public ZRangeMasterRequest(String zSetKey, int startIdx, int endIdx) {
        super(Command.ZRANGE);
        this.zSetKey = zSetKey;
        this.startIdx = startIdx;
        this.endIdx = endIdx;
    }

    @Override
    public Response doExecute() {
        Storage<String, String> storage = RepositoryManager.getInstance();
        List<String> elements = storage.zRange(zSetKey, startIdx, endIdx);

        if (elements.isEmpty()) return new Response(EMPTY_RESP_ARRAY);

        return new Response(RespSerializer.asArray(elements));
    }
}
