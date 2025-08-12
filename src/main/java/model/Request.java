package model;

public abstract class Request {
   protected final Command command;

    protected Request(Command command) {
        this.command = command;
    }

    public abstract Response execute();
}
