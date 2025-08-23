package requests.master;

import requests.AbstractRequest;
import requests.Request;
import requests.model.Command;
import requests.model.Response;
import util.RespSerializer;

import java.net.Socket;
import java.util.List;

import static util.RespConstants.ERROR_EXEC_WITHOUT_MULTI;

public class ExecMasterRequest extends AbstractRequest {
    private boolean inTxn;
    private List<Request> queuedRequests;
    private Socket client;

    public ExecMasterRequest() {
        super(Command.EXEC);
    }

    public void setInTxn(boolean inTxn) {
        this.inTxn = inTxn;
    }

    public void setQueuedRequests(List<Request> queuedRequests) {
        this.queuedRequests = queuedRequests;
    }

    public void setClient(Socket client) {
        this.client = client;
    }

    @Override
    public Response doExecute() {
        if (!inTxn) {
            return new Response(ERROR_EXEC_WITHOUT_MULTI);
        }

        for (Request request : queuedRequests) {
//            request.doExecute();
        }

        int count = 1;
        return new Response(RespSerializer.asInteger(count));
    }
}
