package model;

import conf.Configuration;
import conf.ConfigurationManager;
import util.RespSerializer;

public class InfoRequest extends Request {

    public InfoRequest() {
        super(Command.INFO);
    }

    @Override
    public Response execute() {
        ConfigurationManager configurationManager = ConfigurationManager.getInstance();
        Configuration configuration = configurationManager.getConfiguration();

        String info = "role:" + configuration.getRole().getName();
        return new Response(RespSerializer.asBulkString(info));
    }
}
