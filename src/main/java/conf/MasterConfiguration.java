package conf;

import core.Replica;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MasterConfiguration extends Configuration {
    private final String masterReplId;
    private int masterReplOffset;

    private final Set<Replica> replicas;

    public MasterConfiguration() {
        super();
        masterReplId = (
                UUID.randomUUID().toString().replace("-", "") +
                UUID.randomUUID().toString().replace("-", "")
            ).substring(0, 40);
        masterReplOffset = 0;
        replicas = new HashSet<>();
    }

    public String getMasterReplId() {
        return masterReplId;
    }

    public void incrMasterReplOffsetBy(int offset) {
        masterReplOffset += offset;
    }

    public int getMasterReplOffset() {
        return masterReplOffset;
    }

    public void addReplica(Replica replica) {
        replicas.add(replica);
    }

    public void removeReplica(Replica replica) {
        replicas.remove(replica);
    }

    public Set<Replica> getReplicas() {
        return replicas;
    }
}
