package repository;

import java.util.Optional;

public interface Storage<K, V> {
    void set(K key, V value);
    Optional<V> get(K key);
}
