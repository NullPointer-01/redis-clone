package model;

import java.util.List;

public abstract class Request {
   protected final Command command;
   protected final List<Object> args;

    protected Request(Command command, List<Object> args) {
        this.command = command;
        this.args = args;
    }

    public abstract Response execute();
}
