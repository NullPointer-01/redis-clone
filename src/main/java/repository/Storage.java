package repository;

import java.util.List;
import java.util.Optional;

public interface Storage<K, V> {
    void set(K key, V value, Long timeToExpireInMillis);
    Optional<V> get(K key);

    Integer rPush(K listKey, List<V> elements);
    Integer lPush(K listKey, List<V> elements);

    List<V> lRange(K listKey, int startIdx, int endIdx);
}
