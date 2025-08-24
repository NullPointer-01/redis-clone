package requests.master.streams;

import ds.Pair;
import repository.RepositoryManager;
import repository.Storage;
import requests.AbstractRequest;
import requests.model.Command;
import requests.model.Response;
import util.RespSerializer;

import java.util.ArrayList;
import java.util.List;

public class XAddMasterRequest extends AbstractRequest {
    private final String streamKey;
    private final String entryId;
    private final List<Pair<String, String>> keysAndValues;

    public XAddMasterRequest(String streamKey, String entryId, List<String> args) {
        super(Command.XADD);
        this.streamKey = streamKey;
        this.entryId = entryId;
        this.keysAndValues = getKeysAndValues(args);
    }

    private List<Pair<String, String>> getKeysAndValues(List<String> args) {
        List<Pair<String, String>> keysAndValues = new ArrayList<>(args.size()/2);

        for (int i = 0; i < args.size(); i += 2) {
            keysAndValues.add(new Pair<>(args.get(i), args.get(i+1)));
        }

        return keysAndValues;
    }

    @Override
    public Response doExecute() {
        Storage<String, String> storage = RepositoryManager.getInstance();

        String persistedEntryId = storage.xAdd(streamKey, entryId, keysAndValues);
        return new Response(RespSerializer.asBulkString(persistedEntryId));
    }
}
