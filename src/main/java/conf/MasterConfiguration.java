package conf;

import java.util.UUID;

public class MasterConfiguration extends Configuration {
    private final String masterReplId;
    private int masterReplOffset;

    public MasterConfiguration() {
        super();
        masterReplId = (
                UUID.randomUUID().toString().replace("-", "") +
                UUID.randomUUID().toString().replace("-", "")
            ).substring(0, 40);
        masterReplOffset = 0;
    }

    public String getMasterReplId() {
        return masterReplId;
    }

    public int getMasterReplOffset() {
        return masterReplOffset;
    }
}
