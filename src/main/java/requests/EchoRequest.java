package requests;

import requests.model.Command;
import requests.model.Response;
import util.RespSerializer;

public class EchoRequest extends AbstractRequest {
    private final String arg;

    public EchoRequest(String arg) {
        super(Command.ECHO);
        this.arg = arg;
    }

    @Override
    public Response doExecute() {
        String response = RespSerializer.asBulkString(arg);
        return new Response(response);
    }
}
