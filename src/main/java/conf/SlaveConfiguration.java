package conf;

public class SlaveConfiguration extends Configuration {
    private String masterHost;
    private Integer masterPort;

    public SlaveConfiguration() {
        super();
    }

    public String getMasterHost() {
        return masterHost;
    }

    public void setMasterHost(String masterHost) {
        this.masterHost = masterHost;
    }

    public int getMasterPort() {
        return masterPort;
    }

    public void setMasterPort(Integer masterPort) {
        this.masterPort = masterPort;
    }
}
