package gh.dso.datastructures;

import gh.dso.datastructures.hash.MyHashTable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MyHashTableTest {

    @Test
    void putAndGet_normalCase() {
        MyHashTable<Integer, String> table = new MyHashTable<>(16);
        table.put(101, "FOOD to Osu");
        table.put(102, "PARCEL to Madina");

        assertEquals("FOOD to Osu", table.get(101));
        assertEquals("PARCEL to Madina", table.get(102));
        assertEquals(2, table.size());
    }

    @Test
    void put_updatesExistingKey_noDuplicateEntry() {
        MyHashTable<Integer, String> table = new MyHashTable<>(16);
        table.put(1, "first");
        table.put(1, "updated");

        assertEquals("updated", table.get(1));
        assertEquals(1, table.size());
    }

    @Test
    void remove_normalCase() {
        MyHashTable<Integer, String> table = new MyHashTable<>(16);
        table.put(1, "a");
        table.put(2, "b");

        assertEquals("a", table.remove(1));
        assertNull(table.get(1));
        assertEquals(1, table.size());
    }

    @Test
    void collisionHandling_sameBucket_bothRetrievable() {
        // Table size 1 forces every key into the same bucket.
        MyHashTable<Integer, String> table = new MyHashTable<>(1);
        table.put(1, "one");
        table.put(2, "two");
        table.put(3, "three");

        assertEquals("one", table.get(1));
        assertEquals("two", table.get(2));
        assertEquals("three", table.get(3));
        assertTrue(table.collisionCount() >= 2);
    }

    @Test
    void emptyTable_boundaryCase() {
        MyHashTable<Integer, String> table = new MyHashTable<>(8);
        assertTrue(table.isEmpty());
        assertNull(table.get(1));
        assertNull(table.remove(1));
    }

    @Test
    void invalidTableSize_invalidCase_throws() {
        assertThrows(IllegalArgumentException.class, () -> new MyHashTable<Integer, String>(0));
        assertThrows(IllegalArgumentException.class, () -> new MyHashTable<Integer, String>(-3));
    }

    @Test
    void loadFactor_reflectsSizeVsCapacity() {
        MyHashTable<Integer, String> table = new MyHashTable<>(10);
        for (int i = 0; i < 5; i++) table.put(i, "v" + i);
        assertEquals(0.5, table.loadFactor(), 0.0001);
    }
}
