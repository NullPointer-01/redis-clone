package requests;

import requests.model.Client;
import requests.model.Command;
import requests.model.Response;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Logger;

import static util.RespConstants.QUEUED_SIMPLE_STRING;

public abstract class AbstractRequest implements Request {
    protected static final Logger LOGGER = Logger.getLogger(AbstractRequest.class.getName());
    protected final Command command;

    protected AbstractRequest(Command command) {
        this.command = command;
    }

    @Override
    public Command getCommand() {
        return command;
    }

    @Override
    public void execute(Client client) throws IOException {
        if (client.inTransaction()) {
            Response response = new Response(QUEUED_SIMPLE_STRING);
            client.queueRequest(this);
            writeOutputToSocket(client.getSocket(), response);
            return;
        }

        Response response = doExecute();
        writeOutputToSocket(client.getSocket(), response);
        postExecute(client);
    }

    private static void writeOutputToSocket(Socket socket, Response response) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(response.getResponse());
        outputStream.flush();
    }

    public abstract Response doExecute();
}
