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
    BLPOP("BLPOP"),

    REPLCONF("REPLCONF"),
    PSYNC("PSYNC"),

    MULTI("MULTI"),
    EXEC("EXEC"),
    DISCARD("DISCARD"),

    HSET("HSET"),
    HSETNX("HSETNX"),
    HGET("HGET"),
    HMGET("HMGET"),
    HGETALL("HGETALL"),
    HKEYS("HKEYS"),
    HVALS("HVALS"),
    HEXISTS("HEXISTS"),
    HLEN("HLEN"),
    HDEL("HDEL"),
    HINCRBY("HINCRBY"),

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
    UNSUBSCRIBE("UNSUBSCRIBE"),
    PSUBSCRIBE("PSUBSCRIBE"),
    PUNSUBSCRIBE("PUNSUBSCRIBE"),
    PUBLISH("PUBLISH"),
    QUIT("QUIT"),

    GEOADD("GEOADD"),
    GEOPOS("GEOPOS"),
    GEODIST("GEODIST"),

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
