package requests.master.zsets;

import repository.RepositoryManager;
import repository.Storage;
import requests.AbstractRequest;
import requests.model.Command;
import requests.model.Response;
import util.RespSerializer;

public class ZCardMasterRequest extends AbstractRequest {
    private final String zSetKey;

    public ZCardMasterRequest(String zSetKey) {
        super(Command.ZCARD);
        this.zSetKey = zSetKey;
    }

    @Override
    public Response doExecute() {
        Storage<String, String> storage = RepositoryManager.getInstance();
        Integer size = storage.zCard(zSetKey);

        return new Response(RespSerializer.asInteger(size));
    }
}