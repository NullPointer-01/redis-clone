package repository;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStorage<K, V> implements Storage<K, V> {
    private final Map<K, Map.Entry<Long,V>> map;

    InMemoryStorage() {
        map = new ConcurrentHashMap<>();
    }

    @Override
    public void set(K key, V value, Long timeToExpireInMillis) {
        Long expiryTimeInMillis = timeToExpireInMillis == null ? Long.MAX_VALUE : System.currentTimeMillis() + timeToExpireInMillis;
        map.put(key, new AbstractMap.SimpleEntry<>(expiryTimeInMillis, value));
    }

    @Override
    public Optional<V> get(K key) {
        if (map.containsKey(key)) {
            Map.Entry<Long, V> entry = map.get(key);
            Long expiryTimeInMillis = entry.getKey();

            if (expiryTimeInMillis <= System.currentTimeMillis()) {
                return Optional.empty();
            }

            return Optional.of(entry.getValue());
        }

        return Optional.empty();
    }
}
