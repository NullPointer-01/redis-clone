package requests;

import requests.model.Command;
import requests.model.Response;

import static util.RespConstants.OK_SIMPLE_STRING;

public class ReplConfRequest extends Request {
    public ReplConfRequest() {
        super(Command.REPLCONF);
    }

    @Override
    public Response execute() {
        return new Response(OK_SIMPLE_STRING);
    }
}
