package requests;

import constants.ErrorConstants;
import requests.model.Client;
import requests.model.Command;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class InvalidRequest implements Request {
    private final List<String> args;

    public InvalidRequest(List<String> args) {
        this.args = args;
    }

    @Override
    public Command getCommand() {
        return Command.INVALID;
    }

    @Override
    public void execute(Client client) throws IOException {
        client.write(String.format(ErrorConstants.ERROR_MISSING_ARGUMENT, args.get(0)).getBytes(StandardCharsets.UTF_8));
    }
}
