package conf;

public class ConfigurationConstants {
    public static final ROLE DEFAULT_ROLE = ROLE.MASTER;
    public static final Integer DEFAULT_PORT = 6379;

    public static final FSYNC_POLICY DEFAULT_FSYNC_POLICY = FSYNC_POLICY.EVERY_SEC;
    public static final String DATA_DIR = System.getenv("DATA_DIR");

    public static final String AOF_FILE_PATH = DATA_DIR + "/aof.txt";

    public enum ARGUMENT {
        PORT("--port"),
        REPLICA_OF("--replicaof");

        private final String value;

        ARGUMENT(String value) {
            this.value = value;
        }

        public static ARGUMENT getArgumentByValue(String value) {
            for (ARGUMENT arg : ARGUMENT.values()) {
                if (arg.getValue().equals(value)) {
                    return arg;
                }
            }

            throw new IllegalArgumentException("Invalid name");
        }

        public String getValue() {
            return value;
        }
    }

    public enum ROLE {
        MASTER("master"),
        SLAVE("slave");

        private final String name;

        ROLE(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static ROLE getRoleByName(String name) {
            for (ROLE role : ROLE.values()) {
                if (role.getName().equals(name)) {
                    return role;
                }
            }

            throw new IllegalArgumentException("Invalid name");
        }
    }

    public enum FSYNC_POLICY {
        ALWAYS, EVERY_SEC, NO
    }
}
