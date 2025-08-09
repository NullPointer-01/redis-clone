import conf.Configuration;
import conf.ConfigurationManager;
import core.ServerListenerThread;

import java.util.logging.Logger;

public class App {

    private static final Logger LOGGER = Logger.getLogger(App.class.getName());
    private static final String CONFIGURATION_FILE_PATH = "src/main/resources/conf/config.properties";

    public static void main(String[] args) {
        ConfigurationManager configurationManager = ConfigurationManager.getInstance();
        configurationManager.loadConfiguration(CONFIGURATION_FILE_PATH);
        Configuration conf = configurationManager.getConfiguration();

        // Start server in separate thread
        new ServerListenerThread(conf).start();
    }
}
