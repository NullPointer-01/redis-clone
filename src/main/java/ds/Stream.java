package ds;

import java.util.LinkedList;
import java.util.List;

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

    public long getLastestMillis() {
        return entries.isEmpty() ? 0 : entries.get(entries.size()-1).getMillis();
    }

    public int getLastestSequenceNumber() {
        return entries.isEmpty() ? 0 : entries.get(entries.size()-1).getSequenceNumber();
    }

    public String getLastestEntryId() {
        if (entries.isEmpty()) return "0-0";

        Entry<K, V> lastEntry = entries.get(entries.size()-1);
        return String.valueOf(lastEntry.getMillis()) + '-' + lastEntry.getSequenceNumber();
    }
}
