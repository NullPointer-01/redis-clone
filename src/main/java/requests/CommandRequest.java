package requests;

import requests.model.Command;
import requests.model.Response;

import static util.RespConstants.OK_SIMPLE_STRING;

public class CommandRequest extends AbstractRequest {
    public CommandRequest() {
        super(Command.COMMAND);
    }

    @Override
    public Response doExecute() {
        return new Response(OK_SIMPLE_STRING);
    }
}
