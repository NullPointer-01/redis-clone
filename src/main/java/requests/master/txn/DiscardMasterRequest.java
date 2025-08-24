package requests.master.txn;

import requests.Request;
import requests.model.Client;
import requests.model.Command;
import requests.model.Response;

import java.io.IOException;
import java.io.OutputStream;

import static util.RespConstants.ERROR_DISCARD_WITHOUT_MULTI;
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

            OutputStream outputStream = client.getSocket().getOutputStream();
            outputStream.write(response.getResponse());
            outputStream.flush();
            return;
        }

        Response response = new Response(OK_SIMPLE_STRING);
        OutputStream outputStream = client.getSocket().getOutputStream();
        outputStream.write(response.getResponse());
        outputStream.flush();

        client.endTransaction();
    }
}
