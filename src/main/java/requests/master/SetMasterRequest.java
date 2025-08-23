package requests.master;

import conf.ConfigurationManager;
import conf.MasterConfiguration;
import repository.RepositoryManager;
import repository.Storage;
import requests.AbstractRequest;
import requests.model.Command;
import requests.model.Response;
import service.PropagationHandler;
import util.RespSerializer;

import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static util.RespConstants.OK_SIMPLE_STRING;

public class SetMasterRequest extends AbstractRequest {
    private final String key;
    private final String value;
    private final Long timeToExpireInMillis;

    public SetMasterRequest(String key, String value, Long timeToExpireInMillis) {
        super(Command.SET);
        this.key = key;
        this.value = value;
        this.timeToExpireInMillis = timeToExpireInMillis;
    }

    @Override
    public Response doExecute() {
        Storage<String, String> storage = RepositoryManager.getInstance();
        storage.set(key, value, timeToExpireInMillis);

        return new Response(OK_SIMPLE_STRING);
    }

    @Override
    public void postExecute(Socket ignored) {
        MasterConfiguration masterConfiguration = (MasterConfiguration) ConfigurationManager.getInstance().getConfiguration();

        List<String> requestParts;
        if (timeToExpireInMillis == null) {
            requestParts = List.of(command.getName(), key, value);
        } else {
            requestParts = List.of(command.getName(), key, value, timeToExpireInMillis.toString());
        }

        byte[] request = RespSerializer.asArray(requestParts).getBytes(StandardCharsets.UTF_8);
        new PropagationHandler(request, masterConfiguration.getReplicas()).propagateRequests();
    }
}
