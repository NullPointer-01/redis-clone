package model;

import conf.Configuration;
import conf.ConfigurationManager;
import conf.MasterConfiguration;
import util.RespSerializer;

import static util.RespConstants.CRLF;

public class InfoRequest extends Request {

    public InfoRequest() {
        super(Command.INFO);
    }

    @Override
    public Response execute() {
        ConfigurationManager configurationManager = ConfigurationManager.getInstance();
        Configuration configuration = configurationManager.getConfiguration();

        StringBuilder info = new StringBuilder();

        String role = "role:" + configuration.getRole().getName();
        info.append(role);
        info.append(CRLF);

        if (configuration.isMaster()) {
            MasterConfiguration masterConfiguration = (MasterConfiguration) configuration;

            String masterReplId = "master_replid:" + masterConfiguration.getMasterReplId();
            info.append(masterReplId);
            info.append(CRLF);

            String masterOffset = "master_repl_offset:" + masterConfiguration.getMasterReplOffset();
            info.append(masterOffset);
            info.append(CRLF);
        }

        return new Response(RespSerializer.asBulkString(info.toString()));
    }
}
