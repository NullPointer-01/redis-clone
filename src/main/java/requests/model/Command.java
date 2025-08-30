package requests.model;

public enum Command {
    PING("PING"),
    ECHO("ECHO"),
    INFO("INFO"),
    TYPE("TYPE"),

    SET("SET"),
    GET("GET"),
    DEL("DEL"),
    INCR("INCR"),

    RPUSH("RPUSH"),
    LPUSH("LPUSH"),
    LLEN("LLEN"),
    LPOP("LPOP"),
    LRANGE("LRANGE"),

    REPLCONF("REPLCONF"),
    PSYNC("PSYNC"),

    MULTI("MULTI"),
    EXEC("EXEC"),
    DISCARD("DISCARD"),

    XADD("XADD"),
    XRANGE("XRANGE"),
    XREAD("XREAD"),

    ZADD("ZADD"),
    ZREM("ZREM"),
    ZRANK("ZRANK"),
    ZCARD("ZCARD"),
    ZSCORE("ZSCORE"),
    ZRANGE("ZRANGE"),

    SUBSCRIBE("SUBSCRIBE"),

    COMMAND("COMMAND"),
    INVALID("");

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

        return INVALID;
    }
}
