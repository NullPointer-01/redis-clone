package requests.master.zsets;

import repository.RepositoryManager;
import repository.Storage;
import requests.AbstractRequest;
import requests.model.Command;
import requests.model.Response;
import util.RespSerializer;

import static util.RespConstants.NULL_BULK_STRING;

public class ZRankMasterRequest extends AbstractRequest {
    private final String zSetKey;
    private final String member;

    public ZRankMasterRequest(String zSetKey, String member) {
        super(Command.ZRANK);
        this.zSetKey = zSetKey;
        this.member = member;
    }

    @Override
    public Response doExecute() {
        Storage<String, String> storage = RepositoryManager.getInstance();
        int rank = storage.zRank(zSetKey, member);

        if (rank > 0) {
            return new Response(RespSerializer.asInteger(rank-1)); // rank-1 for 0-based index
        }

        return new Response(NULL_BULK_STRING);
    }
}