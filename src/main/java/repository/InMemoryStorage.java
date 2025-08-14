package repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStorage<K, V> implements Storage<K, V> {
    private final Map<K, Map.Entry<Long,V>> valuesMap;
    private final Map<K, List<V>> listsMap;

    InMemoryStorage() {
        valuesMap = new ConcurrentHashMap<>();
        listsMap = new ConcurrentHashMap<>();
    }

    @Override
    public void set(K key, V value, Long timeToExpireInMillis) {
        Long expiryTimeInMillis = timeToExpireInMillis == null ? Long.MAX_VALUE : System.currentTimeMillis() + timeToExpireInMillis;
        valuesMap.put(key, new AbstractMap.SimpleEntry<>(expiryTimeInMillis, value));
    }

    @Override
    public Optional<V> get(K key) {
        if (valuesMap.containsKey(key)) {
            Map.Entry<Long, V> entry = valuesMap.get(key);
            Long expiryTimeInMillis = entry.getKey();

            if (expiryTimeInMillis <= System.currentTimeMillis()) {
                return Optional.empty();
            }

            return Optional.of(entry.getValue());
        }

        return Optional.empty();
    }

    @Override
    public Integer rPush(K listKey, List<V> elements) {
        listsMap.computeIfAbsent(listKey, k -> new LinkedList<>());

        List<V> list = listsMap.get(listKey);
        list.addAll(elements);
        return list.size();
    }

    @Override
    public Integer lPush(K listKey, List<V> elements) {
        listsMap.computeIfAbsent(listKey, k -> new LinkedList<>());

        List<V> list = listsMap.get(listKey);
        for (V ele : elements) {
            list.add(0, ele);
        }
        return list.size();
    }

    @Override
    public List<V> lRange(K listKey, int startIdx, int endIdx) {
        List<V> list = listsMap.get(listKey);
        if (list == null || list.isEmpty()) return Collections.emptyList();

        int size = list.size();
        if (startIdx < 0) {
            startIdx = Math.max(0, startIdx+size);
        }
        if (endIdx < 0) {
            endIdx = Math.max(0, endIdx+size);
        }

        if (startIdx > endIdx || startIdx >= size) return Collections.emptyList();

        endIdx = Math.min(endIdx, size-1);
        return list.subList(startIdx, endIdx+1);
    }
}
