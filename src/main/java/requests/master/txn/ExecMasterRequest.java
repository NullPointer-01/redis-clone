package requests.master.txn;

import requests.AbstractRequest;
import requests.Request;
import requests.model.Client;
import requests.model.Command;
import requests.model.Response;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static util.RespConstants.*;

public class ExecMasterRequest implements Request {
    public ExecMasterRequest() {
    }

    @Override
    public Command getCommand() {
        return Command.EXEC;
    }

    @Override
    public void execute(Client client) throws IOException {
        if (!client.inTransaction()) {
            Response response = new Response(ERROR_EXEC_WITHOUT_MULTI);

            OutputStream outputStream = client.getSocket().getOutputStream();
            outputStream.write(response.getResponse());
            outputStream.flush();
            return;
        }

        List<Request> queuedRequests = client.getQueuedRequests();
        StringBuilder execResponse = new StringBuilder().append(ASTERISK).append(queuedRequests.size()).append(CRLF);

        for (Request queuedRequest : queuedRequests) {
            Response response = ((AbstractRequest) queuedRequest).doExecute();
            execResponse.append(response.getResponseAsStr());
        }

        OutputStream outputStream = client.getSocket().getOutputStream();
        outputStream.write(execResponse.toString().getBytes(StandardCharsets.UTF_8));
        outputStream.flush();

        client.endTransaction();
    }
}
