package gh.dso.datastructures.setmap;

import gh.dso.datastructures.hash.MyHashTable;

/**
 * Custom Set implementation built on top of our custom MyHashTable.
 * Satisfies the custom set/map requirement in Section 6.
 */
public class MySet<E> {
    private final MyHashTable<E, Boolean> table;

    public MySet(int tableSize) {
        this.table = new MyHashTable<>(tableSize);
    }

    public void add(E element) {
        table.put(element, Boolean.TRUE);
    }

    public boolean contains(E element) {
        return table.containsKey(element);
    }

    public boolean remove(E element) {
        return table.remove(element) != null;
    }

    public int size() {
        return table.size();
    }

    public boolean isEmpty() {
        return table.isEmpty();
    }
}
