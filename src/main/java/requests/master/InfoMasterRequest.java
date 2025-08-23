package requests.master;

import conf.Configuration;
import conf.ConfigurationManager;
import conf.MasterConfiguration;
import requests.AbstractRequest;
import requests.model.Command;
import requests.model.Response;
import util.RespSerializer;

import static util.RespConstants.CRLF;

public class InfoMasterRequest extends AbstractRequest {

    public InfoMasterRequest() {
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

        MasterConfiguration masterConfiguration = (MasterConfiguration) configuration;

        String masterReplId = "master_replid:" + masterConfiguration.getMasterReplId();
        info.append(masterReplId);
        info.append(CRLF);

        String masterOffset = "master_repl_offset:" + masterConfiguration.getMasterReplOffset();
        info.append(masterOffset);
        info.append(CRLF);

        return new Response(RespSerializer.asBulkString(info.toString()));
    }
}
