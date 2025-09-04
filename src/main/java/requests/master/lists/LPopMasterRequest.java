package requests.master.lists;

import repository.RepositoryManager;
import repository.Storage;
import requests.AbstractRequest;
import requests.model.Client;
import requests.model.Command;
import requests.model.Response;
import service.MasterReplicationHandler;
import util.RespSerializer;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static util.RespConstants.EMPTY_RESP_ARRAY;
import static util.RespConstants.NULL_BULK_STRING;

public class LPopMasterRequest extends AbstractRequest {
    private final String listKey;
    private final Integer count;

    public LPopMasterRequest(String listKey, Integer count) {
        super(Command.LPOP);
        this.listKey = listKey;
        this.count = count;
    }

    @Override
    public Response doExecute() {
        Storage<String, String> storage = RepositoryManager.getInstance();
        if (count == null) {
            List<String> elements = storage.lPop(listKey, 1);
            if (elements.isEmpty()) return new Response(NULL_BULK_STRING);

            return new Response(RespSerializer.asBulkString(elements.get(0)));
        }

        List<String> elements = storage.lPop(listKey, count);
        if (elements.isEmpty()) return new Response(EMPTY_RESP_ARRAY);

        return new Response(RespSerializer.asArray(elements));
    }

    @Override
    public void postExecute(Client ignored) {
        List<String> requestParts = List.of(command.getName(), listKey, String.valueOf(count));

        byte[] request = RespSerializer.asArray(requestParts).getBytes(StandardCharsets.UTF_8);
        MasterReplicationHandler.getInstance().propagateRequests(request);
    }
}
