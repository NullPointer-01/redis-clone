package requests.master.txn;

import requests.Request;
import requests.model.Client;
import requests.model.Command;
import requests.model.Response;

import java.io.IOException;
import java.io.OutputStream;

import static util.RespConstants.OK_SIMPLE_STRING;

public class MultiMasterRequest implements Request {

    public MultiMasterRequest() {
    }

    @Override
    public Command getCommand() {
        return Command.MULTI;
    }

    @Override
    public void execute(Client client) throws IOException {
        client.startTransaction();

        Response response = new Response(OK_SIMPLE_STRING);
        OutputStream outputStream = client.getSocket().getOutputStream();
        outputStream.write(response.getResponse());

        outputStream.flush();
    }
}
