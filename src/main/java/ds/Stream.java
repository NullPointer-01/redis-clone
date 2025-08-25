package ds;

import java.util.LinkedList;
import java.util.List;

import static util.RespConstants.ZERO_STREAM_ENTRY_ID;

public class Stream<K, V> {
    private final Trie trie;
    private final List<Entry<K, V>> entries;

    public Stream() {
        this.trie = new Trie();
        this.entries = new LinkedList<>();
    }

    public void addEntry(String entryId, List<Pair<K, V>> keysAndValues) {
        String[] parts = entryId.split("-");

        trie.insert(entryId, entries.size());
        entries.add(new Entry<>(Long.parseLong(parts[0]), Integer.parseInt(parts[1]), keysAndValues));
    }

    public long getLastMillis() {
        return entries.isEmpty() ? 0 : entries.get(entries.size()-1).getMillis();
    }

    public int getLastSequenceNumber() {
        return entries.isEmpty() ? 0 : entries.get(entries.size()-1).getSequenceNumber();
    }

    public String getLastEntryId() {
        if (entries.isEmpty()) return ZERO_STREAM_ENTRY_ID;

        Entry<K, V> lastEntry = entries.get(entries.size()-1);
        return String.valueOf(lastEntry.getMillis()) + '-' + lastEntry.getSequenceNumber();
    }
}
