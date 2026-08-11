package gh.dso.datastructures;

import gh.dso.datastructures.setmap.MyMap;
import gh.dso.datastructures.setmap.MySet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MySetMapTest {

    @Test
    public void testMyMap() {
        MyMap<String, Integer> map = new MyMap<>(11);
        assertTrue(map.isEmpty());
        assertEquals(0, map.size());

        map.put("Accra", 1);
        map.put("Kumasi", 2);
        map.put("Tamale", 3);

        assertEquals(3, map.size());
        assertFalse(map.isEmpty());
        assertTrue(map.containsKey("Accra"));
        assertFalse(map.containsKey("Takoradi"));

        assertEquals(1, map.get("Accra"));
        assertEquals(2, map.get("Kumasi"));

        // Overwrite key
        map.put("Accra", 10);
        assertEquals(10, map.get("Accra"));
        assertEquals(3, map.size());

        // Remove key
        assertEquals(3, map.remove("Tamale"));
        assertNull(map.get("Tamale"));
        assertEquals(2, map.size());
    }

    @Test
    public void testMySet() {
        MySet<String> set = new MySet<>(11);
        assertTrue(set.isEmpty());
        assertEquals(0, set.size());

        set.add("Rider-A");
        set.add("Rider-B");
        set.add("Rider-A"); // duplicate

        assertEquals(2, set.size());
        assertFalse(set.isEmpty());
        assertTrue(set.contains("Rider-A"));
        assertTrue(set.contains("Rider-B"));
        assertFalse(set.contains("Rider-C"));

        assertTrue(set.remove("Rider-A"));
        assertFalse(set.contains("Rider-A"));
        assertEquals(1, set.size());

        assertFalse(set.remove("Rider-C")); // non-existent element
    }
}
