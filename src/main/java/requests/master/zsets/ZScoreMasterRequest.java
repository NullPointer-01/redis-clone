package requests.master.zsets;

import repository.RepositoryManager;
import repository.Storage;
import requests.AbstractRequest;
import requests.model.Command;
import requests.model.Response;
import util.RespSerializer;

import static util.RespConstants.NULL_BULK_STRING;

public class ZScoreMasterRequest extends AbstractRequest {
    private final String zSetKey;
    private final String member;

    public ZScoreMasterRequest(String zSetKey, String member) {
        super(Command.ZSCORE);
        this.zSetKey = zSetKey;
        this.member = member;
    }

    @Override
    public Response doExecute() {
        Storage<String, String> storage = RepositoryManager.getInstance();
        Double score = storage.zScore(zSetKey, member);

        if (score == null) {
            return new Response(NULL_BULK_STRING);

        }

        return new Response(RespSerializer.asBulkString(score.toString()));
    }
}