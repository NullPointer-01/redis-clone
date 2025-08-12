package model;

import static util.RespConstants.PONG_SIMPLE_STRING;

public class PingRequest extends Request {
    public PingRequest() {
        super(Command.PING);
    }

    @Override
    public Response execute() {
        return new Response(PONG_SIMPLE_STRING);
    }
}
