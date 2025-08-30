package requests.master.zsets;


import repository.RepositoryManager;
import repository.Storage;
import requests.AbstractRequest;
import requests.model.Command;
import requests.model.Response;
import util.RespSerializer;

public class ZRemMasterRequest extends AbstractRequest {
    private final String zSetKey;
    private final String member;

    public ZRemMasterRequest(String zSetKey, String member) {
        super(Command.ZREM);
        this.zSetKey = zSetKey;
        this.member = member;
    }

    @Override
    public Response doExecute() {
        Storage<String, String> storage = RepositoryManager.getInstance();
        int count = 0;

        if (storage.zRem(zSetKey, member)) {
            count = 1;
        }

        return new Response(RespSerializer.asInteger(count));
    }
}
