package requests.master.txn;

import requests.Request;
import requests.model.Client;
import requests.model.Command;
import requests.model.Response;

import java.io.IOException;

import static constants.ErrorConstants.ERROR_DISCARD_WITHOUT_MULTI;
import static util.RespConstants.OK_SIMPLE_STRING;

public class DiscardMasterRequest implements Request {
    public DiscardMasterRequest() {
    }

    @Override
    public Command getCommand() {
        return Command.EXEC;
    }

    @Override
    public void execute(Client client) throws IOException {
        if (!client.inTransaction()) {
            Response response = new Response(ERROR_DISCARD_WITHOUT_MULTI);
            client.write(response.getResponse());
            return;
        }

        Response response = new Response(OK_SIMPLE_STRING);
        client.write(response.getResponse());

        client.endTransaction();
    }
}
