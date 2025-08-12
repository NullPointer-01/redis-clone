package model;

import java.util.Collections;

import static util.RespConstants.PONG_SIMPLE_STRING;

public class PingRequest extends Request {
    public PingRequest() {
        super(Command.PING, Collections.emptyList());
    }

    @Override
    public Response execute() {
        return new Response(PONG_SIMPLE_STRING);
    }
}
