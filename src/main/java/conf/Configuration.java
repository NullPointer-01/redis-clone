package conf;

import conf.ConfigurationConstants.FSYNC_POLICY;
import conf.ConfigurationConstants.ROLE;

public abstract class Configuration {
    private ROLE role;
    private Integer port;
    private FSYNC_POLICY fsyncPolicy;

    public Configuration() {
    }

    public ROLE getRole() {
        return role;
    }

    public void setRole(ROLE role) {
        this.role = role;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public FSYNC_POLICY getFsyncPolicy() {
        return fsyncPolicy;
    }

    public void setFsyncPolicy(FSYNC_POLICY fsyncPolicy) {
        this.fsyncPolicy = fsyncPolicy;
    }

    public boolean isMaster() {
        return ROLE.MASTER.equals(role);
    }
}
