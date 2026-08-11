package gh.dso.datastructures.hash;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom hash table with separate chaining (no java.util.HashMap used).
 * Table size is a required "index-number-derived parameter" per the brief —
 * pass a size derived from a team member's index number at construction time.
 */
public class MyHashTable<K, V> {

    private static class Entry<K, V> {
        final K key;
        V value;
        Entry(K key, V value) { this.key = key; this.value = value; }
    }

    private List<Entry<K, V>>[] buckets;
    private int size;
    private int collisionCount; // running total, useful for the load-factor experiment

    @SuppressWarnings("unchecked")
    public MyHashTable(int tableSize) {
        if (tableSize <= 0) throw new IllegalArgumentException("tableSize must be positive");
        buckets = new List[tableSize];
    }

    private int bucketIndex(K key) {
        int h = key.hashCode();
        h ^= (h >>> 16); // spread bits, same idea as java.util.HashMap
        return Math.abs(h) % buckets.length;
    }

    public void put(K key, V value) {
        int idx = bucketIndex(key);
        if (buckets[idx] == null) {
            buckets[idx] = new ArrayList<>();
        } else if (!buckets[idx].isEmpty()) {
            collisionCount++; // a bucket already occupied counts as a collision
        }
        for (Entry<K, V> entry : buckets[idx]) {
            if (entry.key.equals(key)) {
                entry.value = value; // update
                return;
            }
        }
        buckets[idx].add(new Entry<>(key, value));
        size++;
    }

    public V get(K key) {
        int idx = bucketIndex(key);
        if (buckets[idx] == null) return null;
        for (Entry<K, V> entry : buckets[idx]) {
            if (entry.key.equals(key)) return entry.value;
        }
        return null;
    }

    public boolean containsKey(K key) {
        return get(key) != null;
    }

    public V remove(K key) {
        int idx = bucketIndex(key);
        if (buckets[idx] == null) return null;
        List<Entry<K, V>> chain = buckets[idx];
        for (int i = 0; i < chain.size(); i++) {
            if (chain.get(i).key.equals(key)) {
                V removedValue = chain.get(i).value;
                chain.remove(i);
                size--;
                return removedValue;
            }
        }
        return null;
    }

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }
    public int tableSize() { return buckets.length; }
    public int collisionCount() { return collisionCount; }
    public double loadFactor() { return (double) size / buckets.length; }
}
