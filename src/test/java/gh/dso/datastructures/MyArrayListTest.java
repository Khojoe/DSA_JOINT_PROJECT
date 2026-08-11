package gh.dso.datastructures;

import gh.dso.datastructures.list.MyArrayList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MyArrayListTest {

    @Test
    public void testBasicOperations() {
        MyArrayList<Integer> list = new MyArrayList<>(2);
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
        assertEquals(2, list.capacity());

        list.add(10);
        list.add(20);
        assertEquals(2, list.size());
        assertFalse(list.isEmpty());
        assertEquals(10, list.get(0));
        assertEquals(20, list.get(1));

        list.set(1, 25);
        assertEquals(25, list.get(1));
    }

    @Test
    public void testResizing() {
        MyArrayList<Integer> list = new MyArrayList<>(2);
        list.add(1);
        list.add(2);
        assertEquals(2, list.capacity());

        // This addition should trigger a resize
        list.add(3);
        assertEquals(3, list.size());
        assertTrue(list.capacity() > 2);
        assertEquals(3, list.get(2));
    }

    @Test
    public void testInsertionAndRemoval() {
        MyArrayList<String> list = new MyArrayList<>();
        list.add("A");
        list.add("C");
        list.insert(1, "B");

        assertEquals(3, list.size());
        assertEquals("A", list.get(0));
        assertEquals("B", list.get(1));
        assertEquals("C", list.get(2));

        String removed = list.remove(1);
        assertEquals("B", removed);
        assertEquals(2, list.size());
        assertEquals("C", list.get(1));

        assertTrue(list.removeElement("A"));
        assertFalse(list.removeElement("NonExistent"));
        assertEquals(1, list.size());
    }

    @Test
    public void testBoundaryAndExceptions() {
        MyArrayList<Integer> list = new MyArrayList<>(5);
        
        assertThrows(IllegalArgumentException.class, () -> new MyArrayList<>(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.set(0, 10));
        assertThrows(IndexOutOfBoundsException.class, () -> list.insert(1, 10));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(0));
    }
}
