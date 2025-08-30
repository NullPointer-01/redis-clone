package requests;

import requests.model.Client;
import requests.model.Command;
import requests.model.Response;
import util.RespSerializer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import static constants.Constants.PONG;
import static util.RespConstants.PONG_SIMPLE_STRING;

public class PingRequest implements Request {
    public PingRequest() {
    }

    @Override
    public Command getCommand() {
        return Command.PING;
    }

    @Override
    public void execute(Client client) throws IOException {
        Response response;
        if (client.inSubscribedMode()) {
            response = new Response(RespSerializer.asArray(List.of(PONG, "")));
        } else {
            response = new Response(PONG_SIMPLE_STRING);
        }

        OutputStream outputStream = client.getSocket().getOutputStream();
        outputStream.write(response.getResponse());

        outputStream.flush();
    }
}
