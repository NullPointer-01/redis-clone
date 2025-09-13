package requests;

import requests.model.Client;
import requests.model.Command;
import requests.model.Response;

import java.io.IOException;
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
            client.write(response.getResponse());
            return;
        }

        Response response = doExecute();
        client.write(response.getResponse());
        postExecute(client);
    }

    public abstract Response doExecute();

    public void postExecute(Client client) {}
}
