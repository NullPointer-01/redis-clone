package model;

public enum Command {
    PING("PING"),
    ECHO("ECHO"),
    SET("SET"),
    GET("GET"),
    RPUSH("RPUSH"),
    LRANGE("LRANGE");

    private final String name;

    Command(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Command getCommandByName(String name) {
        for (Command command : Command.values()) {
            if (command.getName().equals(name)) {
                return command;
            }
        }

        throw new IllegalArgumentException("Invalid command " + name);
    }
}
