package conf;

import conf.ConfigurationConstants.FSYNC_POLICY;
import conf.ConfigurationConstants.ROLE;
import core.MasterServer;
import core.Server;
import core.SlaveServer;

public class ConfigurationUtil {
    public static Configuration getConfigurationFromArgs(String[] args) {
        Integer port = ConfigurationConstants.DEFAULT_PORT;
        ROLE role = ConfigurationConstants.DEFAULT_ROLE;
        FSYNC_POLICY fsyncPolicy = ConfigurationConstants.DEFAULT_FSYNC_POLICY;

        String masterHost = null;
        Integer masterPort = null;

        for (int i = 0; i < args.length; i+=2) {
            ConfigurationConstants.ARGUMENT arg = ConfigurationConstants.ARGUMENT.getArgumentByValue(args[i]);
            switch (arg) {
                case PORT -> port = Integer.parseInt(args[i+1]);
                case REPLICA_OF -> {
                    role = ROLE.SLAVE;
                    String[] parts = args[i+1].split(" ");
                    masterHost = parts[0];
                    masterPort = Integer.parseInt(parts[1]);
                }
            }
        }

        if (role.equals(ROLE.SLAVE)) {
            SlaveConfiguration conf = new SlaveConfiguration();
            conf.setPort(port);
            conf.setRole(role);

            conf.setMasterHost(masterHost);
            conf.setMasterPort(masterPort);

            return conf;
        }

        Configuration conf = new MasterConfiguration();
        conf.setPort(port);
        conf.setRole(role);
        conf.setFsyncPolicy(fsyncPolicy);

        return conf;
    }

    public static Server getServerByConf(Configuration conf) {
        return conf.isMaster() ? new MasterServer(conf) : new SlaveServer(conf);
    }
}
