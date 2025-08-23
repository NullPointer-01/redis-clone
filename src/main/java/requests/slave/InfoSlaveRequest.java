package requests.slave;

import conf.Configuration;
import conf.ConfigurationManager;
import requests.AbstractRequest;
import requests.model.Command;
import requests.model.Response;
import util.RespSerializer;

import static util.RespConstants.CRLF;

public class InfoSlaveRequest extends AbstractRequest {

    public InfoSlaveRequest() {
        super(Command.INFO);
    }

    @Override
    public Response doExecute() {
        ConfigurationManager configurationManager = ConfigurationManager.getInstance();
        Configuration configuration = configurationManager.getConfiguration();

        StringBuilder info = new StringBuilder();

        String role = "role:" + configuration.getRole().getName();
        info.append(role);
        info.append(CRLF);

        return new Response(RespSerializer.asBulkString(info.toString()));
    }
}
