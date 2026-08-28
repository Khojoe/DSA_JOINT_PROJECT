package gh.dso;

import gh.dso.algorithms.search.SearchAlgorithms;
import gh.dso.algorithms.sort.SortAlgorithms;
import gh.dso.datastructures.deque.MyDeque;
import gh.dso.datastructures.hash.MyHashTable;
import gh.dso.datastructures.heap.MyPriorityQueue;
import gh.dso.datastructures.list.MyIterator;
import gh.dso.datastructures.list.MyLinkedList;
import gh.dso.datastructures.tree.BST;
import gh.dso.db.DataLoader;
import gh.dso.db.DatabaseConnection;
import gh.dso.graph.Dijkstra;
import gh.dso.graph.Graph;
import gh.dso.model.Location;
import gh.dso.model.Resource;
import gh.dso.model.Road;
import gh.dso.model.ServiceRequest;
import gh.dso.optimization.GreedyDispatcher;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * Real courier operations backed by the project's custom data structures and
 * algorithms.  This is intentionally separate from the DSA evidence lab:
 * the lab demonstrates individual structures, while this class uses them to
 * answer actual courier questions using database records.
 */
public final class CourierOperations {
    private final DataLoader loader = new DataLoader();

    public void run(Scanner scanner) {
        while (true) {
            ConsoleUI.clearHeader();
            ConsoleUI.section("COURIER OPERATIONS - LIVE DATABASE DATA");
            ConsoleUI.menuItem("1", "Operational dashboard / load data into custom structures");
            ConsoleUI.menuItem("2", "Search service request by ID (Linear Search)");
            ConsoleUI.menuItem("3", "Search service request by ID (Binary Search)");
            ConsoleUI.menuItem("4", "Search requests by source or destination");
            ConsoleUI.menuItem("5", "Hash-table request lookup");
            ConsoleUI.menuItem("6", "Location search using BST");
            ConsoleUI.menuItem("7", "Sort and prioritise service requests");
            ConsoleUI.menuItem("8", "Dispatch queue - FIFO / urgent deque / priority heap");
            ConsoleUI.menuItem("9", "Find delivery route for a request (Dijkstra)");
            ConsoleUI.menuItem("10", "Suggest nearest available courier");
            ConsoleUI.menuItem("0", "Back to main menu");

            String choice = ConsoleUI.readChoice(scanner, "Select an operation");
            try {
                switch (choice) {
                    case "1" -> dashboard();
                    case "2" -> searchByIdLinear(scanner);
                    case "3" -> searchByIdBinary(scanner);
                    case "4" -> searchByLocation(scanner);
                    case "5" -> hashLookup(scanner);
                    case "6" -> locationBstSearch(scanner);
                    case "7" -> sortRequests(scanner);
                    case "8" -> dispatchStructures();
                    case "9" -> routeRequest(scanner);
                    case "10" -> suggestCourier(scanner);
                    case "0" -> { return; }
                    default -> ConsoleUI.warning("Invalid option.");
                }
            } catch (Exception e) {
                ConsoleUI.error("Operation failed: " + e.getMessage());
            }
            if (!"0".equals(choice)) ConsoleUI.pause(scanner);
        }
    }

    private void dashboard() throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            MyLinkedList<Location> locations = loader.loadLocations(conn);
            MyLinkedList<Road> roads = loader.loadRoads(conn);
            MyLinkedList<ServiceRequest> requests = loader.loadServiceRequests(conn);
            MyLinkedList<Resource> resources = loader.loadResources(conn);

            // Operational indexes: the records are first loaded into our custom list,
            // then indexed in our custom hash table and BST for real system operations.
            MyHashTable<String, ServiceRequest> requestIndex = new MyHashTable<>(ProjectParameters.HASH_TABLE_SIZE);
            BST<String, Location> locationIndex = new BST<>();
            MyPriorityQueue<ServiceRequest> urgentHeap = new MyPriorityQueue<>(requestComparator());
            MyDeque<ServiceRequest> fifoDeque = new MyDeque<>();
            int availableCount = countAvailable(resources);
            gh.dso.datastructures.queue.CircularQueue<Resource> riderHubQueue =
                    new gh.dso.datastructures.queue.CircularQueue<>(Math.max(1, Math.max(resources.size(), availableCount)));

            forEach(requests, r -> {
                requestIndex.put(r.getRequestId(), r);
                if ("NEW".equalsIgnoreCase(r.getStatus())) {
                    urgentHeap.insert(r);
                    fifoDeque.addRear(r);
                }
            });
            forEach(locations, l -> locationIndex.insert(l.getName(), l));
            forEach(resources, r -> { if ("AVAILABLE".equalsIgnoreCase(r.getAvailabilityStatus())) riderHubQueue.enqueue(r); });

            System.out.println("DATABASE -> CUSTOM DSA PIPELINE");
            System.out.println("  Locations loaded into MyLinkedList : " + locations.size());
            System.out.println("  Roads loaded into graph source      : " + roads.size());
            System.out.println("  Requests loaded into MyLinkedList   : " + requests.size());
            System.out.println("  Resources loaded                    : " + resources.size());
            System.out.println();
            System.out.println("CUSTOM INDEXES");
            System.out.println("  Request Hash Table entries : " + requestIndex.size());
            System.out.printf("  Hash load factor           : %.3f%n", requestIndex.loadFactor());
            System.out.println("  Hash collisions            : " + requestIndex.collisionCount());
            System.out.println("  Location BST entries       : " + locationIndex.size());
            System.out.println("  Pending FIFO entries       : " + fifoDeque.size());
            System.out.println("  Pending priority heap      : " + urgentHeap.size());
            System.out.println("  Available rider-hub queue  : " + riderHubQueue.size());
            ConsoleUI.success("Database records are now feeding the courier DSA pipeline.");
        }
    }

    private void searchByIdLinear(Scanner scanner) throws Exception {
        List<ServiceRequest> requests = pendingAndAllRequests();
        String id = scannerValue(scanner, "Enter request ID (e.g. REQ001)");
        List<String> ids = requestIds(requests);
        SearchAlgorithms.SearchResult result = SearchAlgorithms.linearSearch(
                ids, id.trim(), Comparator.naturalOrder());
        ConsoleUI.section("LINEAR SEARCH - REQUEST LOOKUP");
        System.out.println("Dataset          : service_requests from MySQL");
        System.out.println("Target           : " + id);
        System.out.println("Result index     : " + result.index());
        System.out.println("Comparisons      : " + result.comparisons());
        if (result.index() >= 0) printRequest(requests.get(result.index()));
        else ConsoleUI.warning("Request not found.");
        System.out.println("Complexity       : O(n) worst case; works on unsorted data.");
    }

    private void searchByIdBinary(Scanner scanner) throws Exception {
        List<ServiceRequest> requests = pendingAndAllRequests();
        SortAlgorithms.quickSort(requests, Comparator.comparing(ServiceRequest::getRequestId));
        List<String> ids = requestIds(requests);
        String id = scannerValue(scanner, "Enter request ID (e.g. REQ001)");
        SearchAlgorithms.SearchResult result = SearchAlgorithms.binarySearch(
                ids, id.trim(), Comparator.naturalOrder());
        ConsoleUI.section("BINARY SEARCH - SORTED REQUEST INDEX");
        System.out.println("Precondition      : request IDs sorted ascending with QuickSort");
        System.out.println("Target            : " + id);
        System.out.println("Result index      : " + result.index());
        System.out.println("Comparisons       : " + result.comparisons());
        if (result.index() >= 0) printRequest(requests.get(result.index()));
        else ConsoleUI.warning("Request not found.");
        System.out.println("Complexity        : O(log n) search after O(n log n) sorting.");
    }

    private void searchByLocation(Scanner scanner) throws Exception {
        List<ServiceRequest> requests = pendingAndAllRequests();
        String location = scannerValue(scanner, "Enter source/destination location ID").trim();
        List<ServiceRequest> matches = new ArrayList<>();
        for (ServiceRequest r : requests) {
            if (r.getSourceLocationId().equalsIgnoreCase(location) ||
                    r.getDestinationLocationId().equalsIgnoreCase(location)) {
                matches.add(r);
            }
        }
        ConsoleUI.section("LOCATION-BASED REQUEST SEARCH");
        System.out.println("Location ID : " + location);
        System.out.println("Matches     : " + matches.size());
        printRequests(matches, 12);
        System.out.println("Method      : linear scan over the database-backed custom-list dataset.");
    }

    private void hashLookup(Scanner scanner) throws Exception {
        String id = scannerValue(scanner, "Enter request ID").trim();
        MyHashTable<String, ServiceRequest> index = new MyHashTable<>(ProjectParameters.HASH_TABLE_SIZE);
        for (ServiceRequest r : pendingAndAllRequests()) index.put(r.getRequestId(), r);
        ServiceRequest request = index.get(id);

        ConsoleUI.section("CUSTOM HASH TABLE - REQUEST INDEX");
        System.out.println("Key          : " + id);
        System.out.println("Table size   : " + index.tableSize());
        System.out.printf("Load factor  : %.3f%n", index.loadFactor());
        System.out.println("Collisions   : " + index.collisionCount());
        if (request != null) printRequest(request);
        else ConsoleUI.warning("Request not found in custom hash index.");
    }

    private void locationBstSearch(Scanner scanner) throws Exception {
        String name = scannerValue(scanner, "Enter location name (e.g. Osu)").trim();
        BST<String, Location> index = new BST<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            forEach(loader.loadLocations(conn), l -> index.insert(l.getName(), l));
        }
        Location location = index.search(name);
        ConsoleUI.section("CUSTOM BST - LOCATION INDEX");
        System.out.println("Search key       : " + name);
        System.out.println("Search path size : " + index.searchPathLength(name));
        System.out.println("Tree height      : " + index.height());
        if (location != null) {
            System.out.println("FOUND            : " + location);
            System.out.println("Location ID      : " + location.getLocationId());
        } else ConsoleUI.warning("Location not found.");
    }

    private void sortRequests(Scanner scanner) throws Exception {
        List<ServiceRequest> requests = pendingRequests();
        if (requests.isEmpty()) {
            ConsoleUI.warning("There are no NEW requests to prioritise.");
            return;
        }
        ConsoleUI.section("SORT & PRIORITISE LIVE COURIER REQUESTS");
        System.out.println("Pending NEW requests: " + requests.size());
        ConsoleUI.menuItem("1", "Selection Sort - urgency");
        ConsoleUI.menuItem("2", "Insertion Sort - deadline");
        ConsoleUI.menuItem("3", "Merge Sort - submission time");
        ConsoleUI.menuItem("4", "Quick Sort - urgency, then deadline");
        ConsoleUI.menuItem("5", "Compare all four on the same request batch");
        String algorithm = scannerValue(scanner, "Choose sorting option");

        if ("5".equals(algorithm)) {
            compareSorts(requests);
            return;
        }

        Comparator<ServiceRequest> comparator;
        String criterion;
        switch (algorithm) {
            case "1" -> { comparator = urgencyComparator(); criterion = "urgency (5 -> 1)"; }
            case "2" -> { comparator = Comparator.comparing(ServiceRequest::getDeadline); criterion = "deadline (earliest first)"; }
            case "3" -> { comparator = Comparator.comparing(ServiceRequest::getTimeSubmitted); criterion = "submission time (earliest first)"; }
            case "4" -> { comparator = urgencyComparator(); criterion = "urgency (5 -> 1), then deadline"; }
            default -> { ConsoleUI.warning("Invalid sorting option."); return; }
        }

        SortAlgorithms.SortStats stats;
        if ("1".equals(algorithm)) stats = SortAlgorithms.selectionSort(requests, comparator);
        else if ("2".equals(algorithm)) stats = SortAlgorithms.insertionSort(requests, comparator);
        else if ("3".equals(algorithm)) stats = SortAlgorithms.mergeSort(requests, comparator);
        else stats = SortAlgorithms.quickSort(requests, comparator);

        System.out.println("Criterion : " + criterion);
        System.out.println("Comparisons: " + stats.comparisons());
        System.out.println("Swaps/writes: " + stats.swaps());
        System.out.println("\nTop pending requests after sorting:");
        printRequests(requests, 15);
        ConsoleUI.success("Sorted operational data is ready for dispatch prioritisation.");
    }

    private void compareSorts(List<ServiceRequest> original) {
        Comparator<ServiceRequest> comparator = urgencyComparator();
        ConsoleUI.section("SORTING ALGORITHM COMPARISON - SAME COURIER DATA");
        System.out.printf("%-18s %-14s %-14s%n", "Algorithm", "Comparisons", "Swaps/Writes");
        System.out.println("------------------------------------------------");
        List<ServiceRequest> a = new ArrayList<>(original);
        var s = SortAlgorithms.selectionSort(a, comparator);
        System.out.printf("%-18s %-14d %-14d%n", "Selection Sort", s.comparisons(), s.swaps());
        a = new ArrayList<>(original);
        s = SortAlgorithms.insertionSort(a, comparator);
        System.out.printf("%-18s %-14d %-14d%n", "Insertion Sort", s.comparisons(), s.swaps());
        a = new ArrayList<>(original);
        s = SortAlgorithms.mergeSort(a, comparator);
        System.out.printf("%-18s %-14d %-14d%n", "Merge Sort", s.comparisons(), s.swaps());
        a = new ArrayList<>(original);
        s = SortAlgorithms.quickSort(a, comparator);
        System.out.printf("%-18s %-14d %-14d%n", "Quick Sort", s.comparisons(), s.swaps());
        System.out.println("\nBusiness meaning: urgency ordering determines which pending jobs are presented first to dispatch.");
    }

    private void dispatchStructures() throws Exception {
        List<ServiceRequest> pending = pendingRequests();
        if (pending.isEmpty()) {
            ConsoleUI.warning("There are no NEW requests waiting for dispatch.");
            return;
        }
        ConsoleUI.section("DISPATCH ENGINE - CUSTOM STRUCTURES IN ACTION");
        System.out.println("Requests entering dispatch pipeline: " + pending.size());

        MyDeque<ServiceRequest> fifo = new MyDeque<>();
        for (ServiceRequest r : pending) fifo.addRear(r);
        System.out.println("\n1) FIFO / MyDeque - first submitted order");
        printDequePreview(fifo, 10);

        MyDeque<ServiceRequest> urgentDeque = new MyDeque<>();
        for (ServiceRequest r : pending) {
            if (r.getUrgency() >= 5) urgentDeque.addFront(r);
            else urgentDeque.addRear(r);
        }
        System.out.println("\n2) Urgent insertion / MyDeque - critical jobs move to the front");
        printDequePreview(urgentDeque, 10);

        MyPriorityQueue<ServiceRequest> heap = new MyPriorityQueue<>(requestComparator());
        for (ServiceRequest r : pending) heap.insert(r);
        System.out.println("\n3) Priority Queue / Binary Heap - highest urgency first");
        for (int i = 1; i <= 10 && !heap.isEmpty(); i++) {
            ServiceRequest r = heap.extract();
            System.out.printf("  %2d. %-10s urgency=%d deadline=%s%n", i, r.getRequestId(), r.getUrgency(), r.getDeadline());
        }
        ConsoleUI.success("Queue, deque and heap now represent real pending courier work.");
    }

    private void routeRequest(Scanner scanner) throws Exception {
        String id = scannerValue(scanner, "Enter request ID").trim();
        ServiceRequest request = findRequest(id);
        if (request == null) { ConsoleUI.warning("Request not found."); return; }

        Graph graph = buildGraph();
        Dijkstra.PathResult result = Dijkstra.shortestPaths(graph, request.getSourceLocationId());
        Double weight = result.distances().get(request.getDestinationLocationId());
        ConsoleUI.section("DELIVERY ROUTE - DIJKSTRA");
        printRequest(request);
        System.out.println("Route metric : travel time adjusted by road condition");
        if (weight == null) {
            ConsoleUI.warning("Destination is unreachable from the request source.");
            return;
        }
        System.out.printf("Shortest effective route weight : %.2f%n", weight);
        System.out.println("Path                         : " + result.pathTo(request.getDestinationLocationId()));
        ConsoleUI.success("Dijkstra supplied the route used by the courier planning stage.");
    }

    private void suggestCourier(Scanner scanner) throws Exception {
        String id = scannerValue(scanner, "Enter request ID").trim();
        ServiceRequest request = findRequest(id);
        if (request == null) { ConsoleUI.warning("Request not found."); return; }
        try (Connection conn = DatabaseConnection.getConnection()) {
            List<Resource> available = filterAvailable(loader.loadResources(conn));
            Graph graph = buildGraph();
            GreedyDispatcher.DispatchResult result = GreedyDispatcher.assign(List.of(request), available, graph);
            ConsoleUI.section("COURIER ASSIGNMENT SUGGESTION");
            printRequest(request);
            if (result.assignments().isEmpty()) {
                ConsoleUI.warning("No reachable AVAILABLE courier was found.");
                return;
            }
            GreedyDispatcher.Assignment best = result.assignments().get(0);
            System.out.println("Recommended courier : " + best.resource().getResourceId());
            System.out.println("Type                 : " + best.resource().getResourceType());
            System.out.println("Home location        : " + best.resource().getHomeLocationId());
            System.out.printf("Distance to pickup   : %.2f%n", best.distance());
            ConsoleUI.success("Greedy nearest-courier selection completed. No database write was made.");
        }
    }

    private List<ServiceRequest> pendingAndAllRequests() throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return toList(loader.loadServiceRequests(conn));
        }
    }

    private List<ServiceRequest> pendingRequests() throws Exception {
        List<ServiceRequest> all = pendingAndAllRequests();
        all.removeIf(r -> !"NEW".equalsIgnoreCase(r.getStatus()));
        return all;
    }

    private ServiceRequest findRequest(String id) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            MyLinkedList<ServiceRequest> data = loader.loadServiceRequests(conn);
            MyHashTable<String, ServiceRequest> index = new MyHashTable<>(ProjectParameters.HASH_TABLE_SIZE);
            forEach(data, r -> index.put(r.getRequestId(), r));
            return index.get(id);
        }
    }

    private Graph buildGraph() throws Exception {
        Graph graph = new Graph();
        try (Connection conn = DatabaseConnection.getConnection()) {
            MyLinkedList<Location> locations = loader.loadLocations(conn);
            forEach(locations, l -> graph.addLocation(l.getLocationId()));
            forEach(loader.loadRoads(conn), graph::addRoad);
        }
        return graph;
    }

    private static int countAvailable(MyLinkedList<Resource> resources) {
        final int[] count = {0};
        forEach(resources, r -> { if ("AVAILABLE".equalsIgnoreCase(r.getAvailabilityStatus())) count[0]++; });
        return count[0];
    }

    private static List<Resource> filterAvailable(MyLinkedList<Resource> resources) {
        List<Resource> result = new ArrayList<>();
        forEach(resources, r -> { if ("AVAILABLE".equalsIgnoreCase(r.getAvailabilityStatus())) result.add(r); });
        return result;
    }

    private static Comparator<ServiceRequest> urgencyComparator() {
        return Comparator.<ServiceRequest>comparingInt(ServiceRequest::getUrgency).reversed()
                .thenComparing(ServiceRequest::getDeadline)
                .thenComparing(ServiceRequest::getTimeSubmitted);
    }

    private static Comparator<ServiceRequest> requestComparator() { return urgencyComparator(); }

    private static String scannerValue(Scanner scanner, String prompt) {
        System.out.print("\n" + prompt + ": ");
        return scanner.nextLine();
    }

    private static List<String> requestIds(List<ServiceRequest> requests) {
        List<String> ids = new ArrayList<>();
        for (ServiceRequest r : requests) ids.add(r.getRequestId());
        return ids;
    }

    private static void printRequest(ServiceRequest r) {
        System.out.println("\nREQUEST DETAILS");
        System.out.println("  ID          : " + r.getRequestId());
        System.out.println("  Route       : " + r.getSourceLocationId() + " -> " + r.getDestinationLocationId());
        System.out.println("  Category    : " + r.getCategory());
        System.out.println("  Urgency     : " + r.getUrgency() + "/5");
        System.out.println("  Submitted   : " + r.getTimeSubmitted());
        System.out.println("  Deadline    : " + r.getDeadline());
        System.out.println("  Status      : " + r.getStatus());
    }

    private static void printRequests(List<ServiceRequest> requests, int limit) {
        int n = Math.min(limit, requests.size());
        for (int i = 0; i < n; i++) {
            ServiceRequest r = requests.get(i);
            System.out.printf("  %-10s %-12s urgency=%d deadline=%s status=%s%n",
                    r.getRequestId(), r.getCategory(), r.getUrgency(), r.getDeadline(), r.getStatus());
        }
        if (requests.size() > n) System.out.println("  ... " + (requests.size() - n) + " more requests");
    }

    private static void printDequePreview(MyDeque<ServiceRequest> deque, int limit) {
        List<ServiceRequest> snapshot = deque.snapshot();
        for (int i = 0; i < Math.min(limit, snapshot.size()); i++) {
            ServiceRequest r = snapshot.get(i);
            System.out.printf("  %2d. %-10s urgency=%d submitted=%s%n", i + 1, r.getRequestId(), r.getUrgency(), r.getTimeSubmitted());
        }
        if (snapshot.size() > limit) System.out.println("  ... " + (snapshot.size() - limit) + " more");
    }

    private static <T> List<T> toList(MyLinkedList<T> source) {
        List<T> result = new ArrayList<>();
        forEach(source, result::add);
        return result;
    }

    private static <T> void forEach(MyLinkedList<T> source, java.util.function.Consumer<T> action) {
        MyIterator<T> it = source.iterator();
        while (it.hasNext()) action.accept(it.next());
    }
}
