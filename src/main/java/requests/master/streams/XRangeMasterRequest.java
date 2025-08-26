package requests.master.streams;

import ds.Entry;
import exceptions.InvalidEntryIdException;
import repository.RepositoryManager;
import repository.Storage;
import requests.AbstractRequest;
import requests.model.Command;
import requests.model.Response;
import util.RespSerializer;

import java.util.List;

import static constants.ErrorConstants.ERROR_INVALID_STREAM_ENTRY_ID;
import static constants.ErrorConstants.ERROR_ZERO_STREAM_ENTRY_ID;

public class XRangeMasterRequest extends AbstractRequest  {
    private final String streamKey;
    private final String startEntryId;
    private final String endEntryId;

    public XRangeMasterRequest(String streamKey, String startEntryId, String endEntryId) {
        super(Command.XRANGE);
        this.streamKey = streamKey;
        this.startEntryId = startEntryId;
        this.endEntryId = endEntryId;
    }

    @Override
    public Response doExecute() {
        Storage<String, String> storage = RepositoryManager.getInstance();

        try {
            List<Entry<String, String>> entries = storage.xRange(streamKey, startEntryId, endEntryId);
            return new Response(RespSerializer.asXRangeArray(entries));
        } catch (InvalidEntryIdException ex) {
            if (ex.isZeroEntryId()) {
                return new Response(ERROR_ZERO_STREAM_ENTRY_ID);
            }
            return new Response(ERROR_INVALID_STREAM_ENTRY_ID);
        }
    }
}
