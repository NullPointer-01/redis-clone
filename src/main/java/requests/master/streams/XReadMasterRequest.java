package requests.master.streams;

import ds.Entry;
import ds.Pair;
import exceptions.InvalidEntryIdException;
import repository.RepositoryManager;
import repository.Storage;
import requests.AbstractRequest;
import requests.model.Command;
import requests.model.Response;
import util.RespSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static constants.ErrorConstants.ERROR_INVALID_STREAM_ENTRY_ID;
import static constants.ErrorConstants.ERROR_ZERO_STREAM_ENTRY_ID;

public class XReadMasterRequest extends AbstractRequest {
    private final List<Pair<String, String>> streamKeysAndEntryIds;

    public XReadMasterRequest(List<String> args) {
        super(Command.XRANGE);
        this.streamKeysAndEntryIds = getStreamKeysAndEntryIds(args);
    }

    private List<Pair<String, String>> getStreamKeysAndEntryIds(List<String> args) {
        List<Pair<String, String>> streamKeysAndEntryIds = new ArrayList<>(args.size()/2);
        int half = args.size()/2;

        for (int i = 0; i < half; i++) {
            streamKeysAndEntryIds.add(new Pair<>(args.get(i), args.get(i+half)));
        }

        return streamKeysAndEntryIds;
    }

    @Override
    public Response doExecute() {
        Storage<String, String> storage = RepositoryManager.getInstance();

        try {
            Map<String, List<Entry<String, String>>> entries = storage.xRead(streamKeysAndEntryIds);
            return new Response(RespSerializer.asXReadArray(entries));
        } catch (InvalidEntryIdException ex) {
            if (ex.isZeroEntryId()) {
                return new Response(ERROR_ZERO_STREAM_ENTRY_ID);
            }
            return new Response(ERROR_INVALID_STREAM_ENTRY_ID);
        }
    }
}
