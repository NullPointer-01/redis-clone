package conf;

import conf.ConfigurationConstants.ROLE;

public abstract class Configuration {
    private ROLE role;
    private Integer port;

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

    public boolean isMaster() {
        return ROLE.MASTER.equals(role);
    }
}
