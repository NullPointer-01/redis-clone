package conf;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigurationManager {
    private static final Logger LOGGER = Logger.getLogger(ConfigurationManager.class.getName());

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

    public void loadConfiguration(String filePath) {
        Properties properties = new Properties();

        try (InputStream inputStream = new BufferedInputStream(new FileInputStream(filePath))) {
            properties.load(inputStream);

            String port = properties.getProperty("port");
            conf = new Configuration(Integer.parseInt(port));
        } catch (IOException e) {
            LOGGER.log(Level.CONFIG, "Exception loading conf properties");
        }
    }

    public Configuration getConfiguration() {
        return conf;
    }
}
