package gh.dso.datastructures;

import gh.dso.datastructures.list.MyIterator;
import gh.dso.datastructures.list.MyLinkedList;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class MyLinkedListTest {

    @Test
    void addFirstAndAddLast_normalCase() {
        MyLinkedList<String> list = new MyLinkedList<>();
        list.addLast("B");
        list.addFirst("A");
        list.addLast("C");

        assertEquals(3, list.size());
        assertEquals("A", list.peekFirst());
        assertEquals("C", list.peekLast());
    }

    @Test
    void insertAfter_normalCase() {
        MyLinkedList<String> list = new MyLinkedList<>();
        list.addLast("A");
        list.addLast("C");
        boolean inserted = list.insertAfter("A", "B");

        assertTrue(inserted);
        StringBuilder sb = new StringBuilder();
        MyIterator<String> it = list.iterator();
        while (it.hasNext()) sb.append(it.next());
        assertEquals("ABC", sb.toString());
    }

    @Test
    void insertAfter_targetNotFound_returnsFalse() {
        MyLinkedList<String> list = new MyLinkedList<>();
        list.addLast("A");
        assertFalse(list.insertAfter("Z", "B"));
    }

    @Test
    void remove_existingValue_normalCase() {
        MyLinkedList<Integer> list = new MyLinkedList<>();
        list.addLast(1);
        list.addLast(2);
        list.addLast(3);

        assertTrue(list.remove(2));
        assertEquals(2, list.size());
        assertEquals(1, list.peekFirst());
        assertEquals(3, list.peekLast());
    }

    @Test
    void remove_missingValue_returnsFalse() {
        MyLinkedList<Integer> list = new MyLinkedList<>();
        list.addLast(1);
        assertFalse(list.remove(99));
    }

    @Test
    void emptyList_boundaryCase() {
        MyLinkedList<Integer> list = new MyLinkedList<>();
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    void singleElement_boundaryCase() {
        MyLinkedList<Integer> list = new MyLinkedList<>();
        list.addLast(42);
        assertEquals(42, list.peekFirst());
        assertEquals(42, list.peekLast());
        assertEquals(42, list.removeFirst());
        assertTrue(list.isEmpty());
    }

    @Test
    void removeFirst_onEmptyList_invalidCase_throws() {
        MyLinkedList<Integer> list = new MyLinkedList<>();
        assertThrows(NoSuchElementException.class, list::removeFirst);
    }

    @Test
    void removeLast_onEmptyList_invalidCase_throws() {
        MyLinkedList<Integer> list = new MyLinkedList<>();
        assertThrows(NoSuchElementException.class, list::removeLast);
    }

    @Test
    void iterator_traversesInOrder() {
        MyLinkedList<Integer> list = new MyLinkedList<>();
        for (int i = 1; i <= 5; i++) list.addLast(i);

        MyIterator<Integer> it = list.iterator();
        int expected = 1;
        while (it.hasNext()) {
            assertEquals(expected++, it.next());
        }
        assertEquals(6, expected);
    }

    @Test
    void iterator_exhausted_invalidCase_throws() {
        MyLinkedList<Integer> list = new MyLinkedList<>();
        list.addLast(1);
        MyIterator<Integer> it = list.iterator();
        it.next();
        assertThrows(NoSuchElementException.class, it::next);
    }
}
