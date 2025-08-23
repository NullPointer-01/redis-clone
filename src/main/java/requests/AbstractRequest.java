package requests;

import requests.model.Command;
import requests.model.Response;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Logger;

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
    public void execute(Socket client) throws IOException {
        Response response = doExecute();
        writeOutputToSocket(client, response);
        postExecute(client);
    }

    private static void writeOutputToSocket(Socket client, Response response) throws IOException {
        OutputStream outputStream = client.getOutputStream();
        outputStream.write(response.getResponse());
        outputStream.flush();
    }

    protected abstract Response doExecute();
}
