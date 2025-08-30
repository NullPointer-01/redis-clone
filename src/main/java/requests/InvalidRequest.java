package requests;

import constants.ErrorConstants;
import requests.model.Client;
import requests.model.Command;
import requests.model.Response;
import util.RespConstants;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

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
        Response response;
        if (client.inSubscribedMode()) {
            response = getSubscribedModeErrorResponse();
        } else {
            response = getErrorResponse();
        }

        OutputStream outputStream = client.getSocket().getOutputStream();
        outputStream.write(response.getResponse());

        outputStream.flush();
    }

    private Response getSubscribedModeErrorResponse() {
        return new Response(String.format(ErrorConstants.ERROR_INVALID_COMMAND_SUBSCRIBED_MODE, args.get(0)));
    }

    private Response getErrorResponse() {
        List<String> arguments = args.subList(1, args.size());
        String argsInStr = arguments.isEmpty() ? "" : arguments.stream().map(arg -> RespConstants.APOSTROPHE + arg + RespConstants.APOSTROPHE).collect(Collectors.joining(" "));

        return new Response(String.format(ErrorConstants.ERROR_UNKNOWN_COMMAND, args.get(0), argsInStr));
    }
}
