package requests.master.zsets;


import repository.RepositoryManager;
import repository.Storage;
import requests.AbstractRequest;
import requests.model.Command;
import requests.model.Response;
import util.RespSerializer;

public class ZAddMasterRequest extends AbstractRequest {
    private final String zSetKey;
    private final String member;
    private final double score;

    public ZAddMasterRequest(String zSetKey, double score, String member) {
        super(Command.ZADD);
        this.zSetKey = zSetKey;
        this.score = score;
        this.member = member;
    }

    @Override
    public Response doExecute() {
        Storage<String, String> storage = RepositoryManager.getInstance();
        int count = 0;

        if (storage.zAdd(zSetKey, member, score)) {
            count = 1;
        }

        return new Response(RespSerializer.asInteger(count));
    }
}
