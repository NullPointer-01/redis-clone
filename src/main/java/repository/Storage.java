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
    Integer delete(List<K> keys);

    Integer rPush(K listKey, List<V> elements);
    Integer lPush(K listKey, List<V> elements);
    List<V> lPop(K listKey, int count);
    List<V> lRange(K listKey, int startIdx, int endIdx);
    Integer lLen(K listKey);

    Integer hSet(K hashKey, List<Pair<K, V>> fieldsAndValues);
    Integer hSetNX(K hashKey, K field, V value);
    Optional<V> hGet(K hashKey, K field);
    List<V> hMGet(K hashKey, List<K> fields);
    List<Pair<K, V>> hGetAll(K hashKey);
    Integer hDel(K hashKey, List<K> fields);
    List<K> hKeys(K hashKey);
    List<V> hVals(K hashKey);
    Integer hLen(K hashKey);

    String xAdd(K streamKey, String entryId, List<Pair<K, V>> keysAndValues);
    List<Entry<K, V>> xRange(K streamKey, String startEntryId, String endEntryId);
    Map<K, List<Entry<K, V>>> xRead(List<Pair<K, String>> streamKeysAndEntryIds);
    Optional<Stream<K, V>> getStream(K key);

    boolean zAdd(K zSetKey, V member, double score);
    boolean zRem(K zSetKey, V member);
    Integer zRank(K zSetKey, V member);
    Integer zCard(K zSetKey);
    Double zScore(K zSetKey, V member);
    List<V> zRange(K zSetKey, int startIdx, int endIdx);
}
