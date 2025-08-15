package conf;

public class ConfigurationManager {
    private static ConfigurationManager configurationManager;
    private static Configuration conf;

    // Private constructor to enforce singleton
    private ConfigurationManager() {}

    public static ConfigurationManager getInstance() {
        if (configurationManager == null) {
            configurationManager = new ConfigurationManager();
        }

        return configurationManager;
    }

    public void loadConfiguration(Configuration configuration) {
        conf = configuration;
    }

    public Configuration getConfiguration() {
        return conf;
    }
}
