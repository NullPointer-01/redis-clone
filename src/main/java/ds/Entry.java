package ds;

import java.util.List;

import static constants.Constants.HYPHEN;

public class Entry<K, V> {
    private final long millis;
    private final long sequenceNumber;

    private final List<Pair<K, V>> keysAndValues;

    public Entry(String entryId, List<Pair<K, V>> keysAndValues) {
        this.keysAndValues = keysAndValues;

        this.millis = Long.parseLong(entryId.substring(0, 16));
        this.sequenceNumber = Long.parseLong(entryId.substring(16, 32));
    }

    public long getMillis() {
        return millis;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public String getEntryId() {
        return millis + HYPHEN + sequenceNumber;
    }

    public List<Pair<K, V>> getKeysAndValues() {
        return keysAndValues;
    }
}
