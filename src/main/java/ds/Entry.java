package ds;

import java.util.List;

public class Entry<K, V> {
    private final int millis;
    private final int sequenceNumber;

    private final List<Pair<K, V>> keysAndValues;

    public Entry(int millis, int sequenceNumber, List<Pair<K, V>> keysAndValues) {
        this.millis = millis;
        this.sequenceNumber = sequenceNumber;
        this.keysAndValues = keysAndValues;
    }

    public int getMillis() {
        return millis;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public List<Pair<K, V>> getKeysAndValues() {
        return keysAndValues;
    }
}
