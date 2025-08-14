import conf.Configuration;
import conf.ConfigurationManager;
import core.ServerListenerThread;

public class App {
    private static final String PORT_ARG = "--port";

    public static void main(String[] args) {
        int port = 6379;
        if (PORT_ARG.equals(args[0])) {
            port = Integer.parseInt(args[1]);
        }

        ConfigurationManager configurationManager = ConfigurationManager.getInstance();
        configurationManager.loadConfiguration(port);
        Configuration conf = configurationManager.getConfiguration();

        // Start server in separate thread
        new ServerListenerThread(conf).start();
    }
}
