package gh.dso.datastructures;

import gh.dso.datastructures.queue.CircularQueue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CircularQueueTest {

    @Test
    void enqueueAndDequeue_normalCase_FIFOOrder() {
        CircularQueue<String> queue = new CircularQueue<>(4);
        queue.enqueue("Amina");
        queue.enqueue("Kojo");
        queue.enqueue("Efua");

        assertEquals("Amina", queue.dequeue());
        assertEquals(2, queue.size());
    }

    @Test
    void wrapAround_trace_matchesExpectedOrder() {
        CircularQueue<String> queue = new CircularQueue<>(4);
        queue.enqueue("Amina");
        queue.enqueue("Kojo");
        queue.enqueue("Efua");
        queue.dequeue(); // removes Amina, front moves forward
        queue.enqueue("Yaw");
        queue.enqueue("Adjoa"); // wraps rear around to index 0

        assertEquals(java.util.List.of("Kojo", "Efua", "Yaw", "Adjoa"), queue.snapshotInQueueOrder());
    }

    @Test
    void emptyQueue_boundaryCase() {
        CircularQueue<String> queue = new CircularQueue<>(3);
        assertTrue(queue.isEmpty());
        assertNull(queue.dequeue());
    }

    @Test
    void fullQueue_boundaryCase_rejectsEnqueue() {
        CircularQueue<String> queue = new CircularQueue<>(2);
        queue.enqueue("A");
        queue.enqueue("B");
        assertTrue(queue.isFull());
        assertFalse(queue.enqueue("C")); // invalid case: queue full
    }

    @Test
    void invalidCapacity_invalidCase_throws() {
        assertThrows(IllegalArgumentException.class, () -> new CircularQueue<String>(0));
        assertThrows(IllegalArgumentException.class, () -> new CircularQueue<String>(-5));
    }
}
