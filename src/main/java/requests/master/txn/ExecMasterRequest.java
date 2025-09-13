package requests.master.txn;

import constants.ErrorConstants;
import requests.AbstractRequest;
import requests.Request;
import requests.model.Client;
import requests.model.Command;
import requests.model.Response;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static util.RespConstants.ASTERISK;
import static util.RespConstants.CRLF;

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
            Response response = new Response(ErrorConstants.ERROR_EXEC_WITHOUT_MULTI);
            client.write(response.getResponse());
            return;
        }

        List<Request> queuedRequests = client.getQueuedRequests();
        StringBuilder execResponse = new StringBuilder().append(ASTERISK).append(queuedRequests.size()).append(CRLF);

        for (Request queuedRequest : queuedRequests) {
            Response response = ((AbstractRequest) queuedRequest).doExecute();
            execResponse.append(response.getResponseAsStr());
        }

        client.write(execResponse.toString().getBytes(StandardCharsets.UTF_8));
        client.endTransaction();
    }
}
