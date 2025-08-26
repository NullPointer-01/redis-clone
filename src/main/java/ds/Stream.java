package ds;

import java.util.LinkedList;
import java.util.List;

import static constants.Constants.ZERO_STREAM_ENTRY_ID;

public class Stream<K, V> {
    private final Trie trie;
    private final List<Entry<K, V>> entries;

    public Stream() {
        this.trie = new Trie();
        this.entries = new LinkedList<>();
    }

    public void addEntry(String entryId, List<Pair<K, V>> keysAndValues) {
        trie.insert(entryId, entries.size());
        entries.add(new Entry<>(entryId, keysAndValues));
    }

    public long getLastMillis() {
        return entries.isEmpty() ? 0 : entries.get(entries.size()-1).getMillis();
    }

    public long getLastSequenceNumber() {
        return entries.isEmpty() ? 0 : entries.get(entries.size()-1).getSequenceNumber();
    }

    public String getLastEntryId() {
        if (entries.isEmpty()) return ZERO_STREAM_ENTRY_ID;

        Entry<K, V> lastEntry = entries.get(entries.size()-1);
        return String.valueOf(lastEntry.getMillis()) + '-' + lastEntry.getSequenceNumber();
    }

    public int findIndex(String entryId) {
        Integer value = trie.search(entryId);
        return value == null ? -1 : value;
    }

    public int findCeil(String entryId) {
        Integer value = trie.searchCeil(entryId);
        return value == null ? entries.size() : value;
    }

    public int size() {
        return entries.size();
    }

    public List<Entry<K, V>> getRange(int startIdx, int endIdx) {
        return entries.subList(startIdx, endIdx+1); // End index is exclusive
    }
}
