package requests.slave.repl;

import requests.AbstractRequest;
import requests.model.Command;
import requests.model.Response;
import util.RespSerializer;

import java.util.List;

public class ReplConfSlaveRequest extends AbstractRequest {

    public ReplConfSlaveRequest() {
        super(Command.REPLCONF);
    }

    @Override
    public Response doExecute() {
        String response = RespSerializer.asArray(List.of("REPLCONF", "ACK", "0"));
        return new Response(response);
    }
}
