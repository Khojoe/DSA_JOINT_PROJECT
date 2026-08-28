package gh.dso.operations;

import gh.dso.ProjectParameters;
import gh.dso.algorithms.search.SearchAlgorithms;
import gh.dso.algorithms.sort.SortAlgorithms;
import gh.dso.datastructures.deque.MyDeque;
import gh.dso.datastructures.hash.MyHashTable;
import gh.dso.datastructures.heap.MyPriorityQueue;
import gh.dso.datastructures.list.MyIterator;
import gh.dso.datastructures.list.MyLinkedList;
import gh.dso.datastructures.queue.CircularQueue;
import gh.dso.datastructures.tree.BST;
import gh.dso.db.DataLoader;
import gh.dso.model.Location;
import gh.dso.model.ServiceRequest;

import java.sql.Connection;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/** Application-level DSA operations using the real courier data. */
public class CourierOperations {
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public MyLinkedList<ServiceRequest> loadRequests(Connection conn) throws Exception {
        return new DataLoader().loadServiceRequests(conn);
    }

    public MyLinkedList<Location> loadLocations(Connection conn) throws Exception {
        return new DataLoader().loadLocations(conn);
    }

    public List<ServiceRequest> pendingRequests(Connection conn) throws Exception {
        List<ServiceRequest> all = toList(loadRequests(conn));
        List<ServiceRequest> pending = new ArrayList<>();
        for (ServiceRequest request : all) {
            if ("NEW".equalsIgnoreCase(request.getStatus())) pending.add(request);
        }
        return pending;
    }

    public SearchAlgorithms.SearchResult linearSearchRequest(List<ServiceRequest> requests, String requestId) {
        Comparator<ServiceRequest> byId = Comparator.comparing(ServiceRequest::getRequestId);
        return SearchAlgorithms.linearSearch(requests, requestWithId(requestId), byId);
    }

    public SearchAlgorithms.SearchResult binarySearchRequest(List<ServiceRequest> requests, String requestId) {
        Comparator<ServiceRequest> byId = Comparator.comparing(ServiceRequest::getRequestId);
        SortAlgorithms.quickSort(requests, byId);
        return SearchAlgorithms.binarySearch(requests, requestWithId(requestId), byId);
    }

    public List<ServiceRequest> searchBySource(List<ServiceRequest> requests, String sourceId) {
        List<ServiceRequest> result = new ArrayList<>();
        for (ServiceRequest r : requests) if (r.getSourceLocationId().equalsIgnoreCase(sourceId)) result.add(r);
        return result;
    }

    public List<ServiceRequest> searchByDestination(List<ServiceRequest> requests, String destinationId) {
        List<ServiceRequest> result = new ArrayList<>();
        for (ServiceRequest r : requests) if (r.getDestinationLocationId().equalsIgnoreCase(destinationId)) result.add(r);
        return result;
    }

    public MyHashTable<String, ServiceRequest> buildRequestIndex(List<ServiceRequest> requests) {
        MyHashTable<String, ServiceRequest> index = new MyHashTable<>(ProjectParameters.HASH_TABLE_SIZE);
        for (ServiceRequest request : requests) index.put(request.getRequestId(), request);
        return index;
    }

    public BST<String, Location> buildLocationIndex(List<Location> locations) {
        BST<String, Location> index = new BST<>();
        for (Location location : locations) index.insert(location.getName().toLowerCase(Locale.ROOT), location);
        return index;
    }

    public SortAlgorithms.SortStats sortRequests(List<ServiceRequest> requests, int algorithm,
                                                   Comparator<ServiceRequest> comparator) {
        return switch (algorithm) {
            case 1 -> SortAlgorithms.selectionSort(requests, comparator);
            case 2 -> SortAlgorithms.insertionSort(requests, comparator);
            case 3 -> SortAlgorithms.mergeSort(requests, comparator);
            case 4 -> SortAlgorithms.quickSort(requests, comparator);
            default -> throw new IllegalArgumentException("Unknown sorting algorithm: " + algorithm);
        };
    }

    public Comparator<ServiceRequest> comparatorFor(String criterion) {
        return switch (criterion) {
            case "urgency" -> Comparator.comparingInt(ServiceRequest::getUrgency).reversed()
                    .thenComparing(ServiceRequest::getTimeSubmitted);
            case "deadline" -> Comparator.comparing(ServiceRequest::getDeadline)
                    .thenComparing(Comparator.comparingInt(ServiceRequest::getUrgency).reversed());
            case "submitted" -> Comparator.comparing(ServiceRequest::getTimeSubmitted);
            case "id" -> Comparator.comparing(ServiceRequest::getRequestId);
            default -> throw new IllegalArgumentException("Unknown criterion: " + criterion);
        };
    }

    public MyDeque<ServiceRequest> buildFifoQueue(List<ServiceRequest> requests) {
        List<ServiceRequest> ordered = new ArrayList<>(requests);
        SortAlgorithms.insertionSort(ordered, Comparator.comparing(ServiceRequest::getTimeSubmitted));
        MyDeque<ServiceRequest> queue = new MyDeque<>();
        for (ServiceRequest request : ordered) queue.addRear(request);
        return queue;
    }

    public MyPriorityQueue<ServiceRequest> buildUrgencyQueue(List<ServiceRequest> requests) {
        MyPriorityQueue<ServiceRequest> queue = new MyPriorityQueue<>(
                Comparator.<ServiceRequest>comparingInt(r -> -r.getUrgency())
                        .thenComparing(ServiceRequest::getTimeSubmitted));
        for (ServiceRequest request : requests) queue.insert(request);
        return queue;
    }

    public CircularQueue<ServiceRequest> buildCircularQueue(List<ServiceRequest> requests, int capacity) {
        CircularQueue<ServiceRequest> queue = new CircularQueue<>(capacity);
        for (ServiceRequest request : requests) {
            if (queue.isFull()) break;
            queue.enqueue(request);
        }
        return queue;
    }

    public MyDeque<ServiceRequest> buildUrgentDeque(List<ServiceRequest> requests) {
        MyDeque<ServiceRequest> deque = new MyDeque<>();
        List<ServiceRequest> ordered = new ArrayList<>(requests);
        SortAlgorithms.insertionSort(ordered, Comparator.comparing(ServiceRequest::getTimeSubmitted));
        for (ServiceRequest request : ordered) {
            if (request.getUrgency() >= 4) deque.addFront(request);
            else deque.addRear(request);
        }
        return deque;
    }

    public static String format(ServiceRequest r) {
        return String.format("%-10s %-8s -> %-8s urgency=%d deadline=%s status=%s",
                r.getRequestId(), r.getSourceLocationId(), r.getDestinationLocationId(),
                r.getUrgency(), r.getDeadline().format(TIME), r.getStatus());
    }

    public static String formatShort(ServiceRequest r) {
        return r.getRequestId() + " | " + r.getSourceLocationId() + " -> " +
                r.getDestinationLocationId() + " | urgency=" + r.getUrgency();
    }

    private ServiceRequest requestWithId(String requestId) {
        return new ServiceRequest(requestId, "", "", "Parcel", 0,
                java.time.LocalDateTime.MIN, java.time.LocalDateTime.MIN, "NEW");
    }

    private static <T> List<T> toList(MyLinkedList<T> source) {
        List<T> result = new ArrayList<>();
        MyIterator<T> it = source.iterator();
        while (it.hasNext()) result.add(it.next());
        return result;
    }
}
