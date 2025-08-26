package repository;

import ds.Entry;
import ds.Pair;
import ds.Stream;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface Storage<K, V> {
    void set(K key, V value, Long timeToExpireInMillis);
    Optional<V> get(K key);

    Integer rPush(K listKey, List<V> elements);
    Integer lPush(K listKey, List<V> elements);

    List<V> lPop(K listKey, int count);
    List<V> lRange(K listKey, int startIdx, int endIdx);

    Integer lLen(K listKey);
    Integer delete(List<K> keys);

    String xAdd(K streamKey, String entryId, List<Pair<K, V>> keysAndValues);
    List<Entry<K, V>> xRange(K streamKey, String startEntryId, String endEntryId);

    Optional<Stream<K, V>> getStream(K key);

    Map<K, List<Entry<K, V>>> xRead(List<Pair<K, String>> streamKeysAndEntryIds);
}
