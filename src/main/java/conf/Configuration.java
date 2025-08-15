package conf;

import conf.ConfigurationConstants.ROLE;

public class Configuration {
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

    public int getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
