package gh.dso.datastructures;

import gh.dso.datastructures.heap.MyPriorityQueue;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class MyPriorityQueueTest {

    private record Ticket(String id, int urgency, int arrival) { }

    private MyPriorityQueue<Ticket> urgencyQueue() {
        return new MyPriorityQueue<>(
                Comparator.<Ticket>comparingInt(t -> -t.urgency())
                        .thenComparingInt(Ticket::arrival));
    }

    @Test
    void extractOrder_normalCase_highestUrgencyFirst() {
        MyPriorityQueue<Ticket> pq = urgencyQueue();
        pq.insert(new Ticket("T1", 5, 1));
        pq.insert(new Ticket("T2", 2, 2));
        pq.insert(new Ticket("T3", 4, 3));
        pq.insert(new Ticket("T4", 4, 4));

        assertEquals("T1", pq.extract().id());
        assertEquals("T3", pq.extract().id()); // same urgency as T4 but earlier arrival
        assertEquals("T4", pq.extract().id());
        assertEquals("T2", pq.extract().id());
    }

    @Test
    void peek_doesNotRemove() {
        MyPriorityQueue<Ticket> pq = urgencyQueue();
        pq.insert(new Ticket("T1", 5, 1));
        assertEquals("T1", pq.peek().id());
        assertEquals(1, pq.size());
    }

    @Test
    void emptyQueue_boundaryCase() {
        MyPriorityQueue<Ticket> pq = urgencyQueue();
        assertTrue(pq.isEmpty());
    }

    @Test
    void singleElement_boundaryCase() {
        MyPriorityQueue<Ticket> pq = urgencyQueue();
        pq.insert(new Ticket("Solo", 1, 1));
        assertEquals("Solo", pq.extract().id());
        assertTrue(pq.isEmpty());
    }

    @Test
    void extract_onEmptyQueue_invalidCase_throws() {
        MyPriorityQueue<Ticket> pq = urgencyQueue();
        assertThrows(NoSuchElementException.class, pq::extract);
    }

    @Test
    void peek_onEmptyQueue_invalidCase_throws() {
        MyPriorityQueue<Ticket> pq = urgencyQueue();
        assertThrows(NoSuchElementException.class, pq::peek);
    }
}
