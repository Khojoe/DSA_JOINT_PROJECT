package gh.dso.datastructures;

import gh.dso.datastructures.deque.MyDeque;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MyDequeTest {

    @Test
    void urgentInsertion_normalCase_priorityJumpsQueue() {
        MyDeque<String> deque = new MyDeque<>();
        deque.addRear("Kojo");
        deque.addRear("Ama");
        deque.addFront("Nurse Efua"); // urgent passenger

        assertEquals(java.util.List.of("Nurse Efua", "Kojo", "Ama"), deque.snapshot());
    }

    @Test
    void removeFromBothEnds_normalCase() {
        MyDeque<String> deque = new MyDeque<>();
        deque.addRear("Kojo");
        deque.addRear("Ama");
        deque.addFront("Nurse Efua");

        assertEquals("Nurse Efua", deque.removeFront());
        assertEquals("Ama", deque.removeRear());
        assertEquals(1, deque.size());
    }

    @Test
    void emptyDeque_boundaryCase() {
        MyDeque<String> deque = new MyDeque<>();
        assertTrue(deque.isEmpty());
        assertNull(deque.removeFront());
        assertNull(deque.removeRear());
    }

    @Test
    void singleElement_boundaryCase() {
        MyDeque<String> deque = new MyDeque<>();
        deque.addFront("only");
        assertEquals("only", deque.removeRear());
        assertTrue(deque.isEmpty());
    }
}
