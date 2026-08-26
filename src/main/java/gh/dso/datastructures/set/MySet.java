package gh.dso.datastructures.set;

import gh.dso.datastructures.hash.MyHashTable;

/** Custom set backed by the assessed custom hash table. */
public class MySet<T> {
    private static final Object PRESENT = new Object();
    private final MyHashTable<T, Object> table;
    public MySet(int capacity) { table = new MyHashTable<>(capacity); }
    public boolean add(T value) { if (contains(value)) return false; table.put(value, PRESENT); return true; }
    public boolean contains(T value) { return table.get(value) != null; }
    public boolean remove(T value) { if (!contains(value)) return false; table.remove(value); return true; }
    public int size() { return table.size(); }
}
