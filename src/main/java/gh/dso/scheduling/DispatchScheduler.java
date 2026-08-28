package gh.dso.scheduling;

import gh.dso.datastructures.deque.MyDeque;
import gh.dso.datastructures.heap.MyPriorityQueue;
import gh.dso.algorithms.sort.SortAlgorithms;
import gh.dso.model.ServiceRequest;

import java.util.Comparator;
import java.util.List;

/**
 * Turns a batch of ServiceRequests into a dispatch order, using the
 * Phase 1 custom structures. Two strategies are provided:
 *
 *  - FIFO: first submitted, first dispatched (fair, but ignores urgency).
 *  - Urgency-priority: highest urgency first, earliest submission breaks ties
 *    (matches how a real dispatcher would triage a pharmacy/medical order
 *    ahead of a routine grocery run).
 */
public class DispatchScheduler {

    /** Builds a FIFO dispatch queue ordered strictly by submission time. */
    public MyDeque<ServiceRequest> buildFifoQueue(List<ServiceRequest> requests) {
        List<ServiceRequest> sortedBySubmission = new java.util.ArrayList<>(requests);
        SortAlgorithms.insertionSort(sortedBySubmission,
                Comparator.comparing(ServiceRequest::getTimeSubmitted));

        MyDeque<ServiceRequest> queue = new MyDeque<>();
        for (ServiceRequest r : sortedBySubmission) {
            queue.addRear(r);
        }
        return queue;
    }

    /** Builds an urgency-first priority queue (5 = most urgent dispatched first). */
    public MyPriorityQueue<ServiceRequest> buildUrgencyQueue(List<ServiceRequest> requests) {
        MyPriorityQueue<ServiceRequest> pq = new MyPriorityQueue<>(
                Comparator.<ServiceRequest>comparingInt(r -> -r.getUrgency())
                        .thenComparing(ServiceRequest::getTimeSubmitted));
        for (ServiceRequest r : requests) {
            pq.insert(r);
        }
        return pq;
    }

    /** Drains an urgency queue into dispatch order (does not mutate the input list). */
    public List<ServiceRequest> dispatchInUrgencyOrder(List<ServiceRequest> requests) {
        MyPriorityQueue<ServiceRequest> pq = buildUrgencyQueue(requests);
        List<ServiceRequest> order = new java.util.ArrayList<>();
        while (!pq.isEmpty()) {
            order.add(pq.extract());
        }
        return order;
    }
}
