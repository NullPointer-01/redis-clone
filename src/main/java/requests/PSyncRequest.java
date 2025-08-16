package requests;

import conf.Configuration;
import conf.ConfigurationManager;
import conf.MasterConfiguration;
import requests.model.Command;
import requests.model.Response;

import static util.RespConstants.*;

public class PSyncRequest extends Request {
    public PSyncRequest() {
        super(Command.PSYNC);
    }

    @Override
    public Response execute() {
        ConfigurationManager configurationManager = ConfigurationManager.getInstance();
        Configuration configuration = configurationManager.getConfiguration();

        if (configuration.isMaster()) {
            MasterConfiguration masterConfiguration = (MasterConfiguration) configuration;

            String response = FULL_RESYNC + " " + masterConfiguration.getMasterReplId() + " 0" + CRLF;
            return new Response(response);
        }

        throw new UnsupportedOperationException("Operation not supported in slave");
    }
}
