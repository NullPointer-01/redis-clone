package requests;

import constants.ErrorConstants;
import requests.model.Command;
import requests.model.Response;
import util.RespConstants;

import java.util.List;
import java.util.stream.Collectors;

public class InvalidRequest extends AbstractRequest {
    private final List<String> args;

    public InvalidRequest(List<String> args) {
        super(Command.INVALID);
        this.args = args;
    }

    @Override
    public Response doExecute() {
        String command = RespConstants.APOSTROPHE + args.get(0) + RespConstants.APOSTROPHE;

        List<String> arguments = args.subList(1, args.size());
        String argsInStr = arguments.isEmpty() ? "" : arguments.stream().map(arg -> RespConstants.APOSTROPHE + arg + RespConstants.APOSTROPHE).collect(Collectors.joining(" "));

        return new Response(String.format(ErrorConstants.ERROR_UNKNOWN_COMMAND, command, argsInStr));
    }
}
