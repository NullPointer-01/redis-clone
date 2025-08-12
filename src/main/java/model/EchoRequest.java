package model;

import util.RespSerializer;

import java.util.List;

public class EchoRequest extends Request {
    public EchoRequest(String arg) {
        super(Command.ECHO, List.of(arg));
    }

    @Override
    public Response execute() {
        String response = RespSerializer.asBulkString(args.get(0).toString());
        return new Response(response);
    }
}
