package gh.dso.datastructures.setmap;

import gh.dso.datastructures.hash.MyHashTable;

/**
 * Custom Map implementation built on top of our custom MyHashTable.
 * Satisfies the custom set/map requirement in Section 6.
 */
public class MyMap<K, V> {
    private final MyHashTable<K, V> table;

    public MyMap(int tableSize) {
        this.table = new MyHashTable<>(tableSize);
    }

    public void put(K key, V value) {
        table.put(key, value);
    }

    public V get(K key) {
        return table.get(key);
    }

    public V remove(K key) {
        return table.remove(key);
    }

    public boolean containsKey(K key) {
        return table.containsKey(key);
    }

    public int size() {
        return table.size();
    }

    public boolean isEmpty() {
        return table.isEmpty();
    }

    public double loadFactor() {
        return table.loadFactor();
    }
}
