package requests;

import requests.model.Command;
import requests.model.Response;

public abstract class Request {
    protected final Command command;

    protected Request(Command command) {
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }

    public abstract Response execute();
}
