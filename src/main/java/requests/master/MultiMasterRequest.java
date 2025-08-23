package requests.master;

import requests.AbstractRequest;
import requests.model.Command;
import requests.model.Response;

import static util.RespConstants.OK_SIMPLE_STRING;

public class MultiMasterRequest extends AbstractRequest {

    public MultiMasterRequest() {
        super(Command.MULTI);
    }

    @Override
    public Response doExecute() {
        return new Response(OK_SIMPLE_STRING);
    }
}
