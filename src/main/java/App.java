import conf.Configuration;
import conf.ConfigurationManager;
import conf.ConfigurationUtil;
import core.ServerListenerThread;

public class App {

    public static void main(String[] args) {
        Configuration conf = ConfigurationUtil.getConfigurationFromArgs(args);

        ConfigurationManager configurationManager = ConfigurationManager.getInstance();
        configurationManager.loadConfiguration(conf);

        // Start server in separate thread
        new ServerListenerThread(conf).start();
    }
}
