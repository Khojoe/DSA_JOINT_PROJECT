package gh.dso.datastructures.map;

import gh.dso.datastructures.hash.MyHashTable;

/** Custom map facade backed by the assessed custom hash table. */
public class MyMap<K, V> {
    private final MyHashTable<K, V> table;
    public MyMap(int capacity) { table = new MyHashTable<>(capacity); }
    public void put(K key, V value) { table.put(key, value); }
    public V get(K key) { return table.get(key); }
    public V remove(K key) { return table.remove(key); }
    public boolean containsKey(K key) { return table.get(key) != null; }
    public int size() { return table.size(); }
    public double loadFactor() { return table.loadFactor(); }
}
