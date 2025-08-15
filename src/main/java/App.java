import conf.Configuration;
import conf.ConfigurationManager;
import conf.ConfigurationUtil;
import core.Server;

public class App {

    public static void main(String[] args) {
        Configuration conf = ConfigurationUtil.getConfigurationFromArgs(args);

        ConfigurationManager configurationManager = ConfigurationManager.getInstance();
        configurationManager.loadConfiguration(conf);

        // Start server in separate thread
        Server server = ConfigurationUtil.getServerByConf(conf);
        server.start();
    }
}
