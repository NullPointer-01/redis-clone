package repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStorage<K, V> implements Storage<K, V> {
    private final Map<K, V> map;

    InMemoryStorage() {
        map = new ConcurrentHashMap<>();
    }

    @Override
    public void set(K key, V value) {
        map.put(key, value);
    }

    @Override
    public Optional<V> get(K key) {
        if (map.containsKey(key)) {
            return Optional.of(map.get(key));
        }

        return Optional.empty();
    }
}
