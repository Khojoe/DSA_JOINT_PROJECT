package gh.dso;

import gh.dso.algorithms.search.SearchAlgorithms;
import gh.dso.algorithms.sort.SortAlgorithms;
import gh.dso.datastructures.array.MyDynamicArray;
import gh.dso.datastructures.deque.MyDeque;
import gh.dso.datastructures.hash.MyHashTable;
import gh.dso.datastructures.map.MyMap;
import gh.dso.datastructures.set.MySet;
import gh.dso.datastructures.heap.MyPriorityQueue;
import gh.dso.datastructures.list.MyIterator;
import gh.dso.datastructures.list.MyLinkedList;
import gh.dso.datastructures.queue.CircularQueue;
import gh.dso.datastructures.stack.MyStack;
import gh.dso.datastructures.tree.BST;
import gh.dso.datastructures.tree.BTree;
import gh.dso.datastructures.tree.RedBlackTree;
import gh.dso.db.DataLoader;
import gh.dso.db.DatabaseConnection;
import gh.dso.graph.Dijkstra;
import gh.dso.graph.Graph;
import gh.dso.graph.GraphMatrix;
import gh.dso.graph.GraphTraversal;
import gh.dso.graph.Kruskal;
import gh.dso.graph.Prim;
import gh.dso.model.Location;
import gh.dso.model.Road;
import gh.dso.model.ServiceRequest;
import gh.dso.optimization.IntegratedDispatchService;
import gh.dso.optimization.KnapsackOptimizer;
import gh.dso.performance.PerformanceLab;
import gh.dso.scheduling.DispatchScheduler;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Examiner-friendly console entry point for the Ghana Courier DSO project.
 * The algorithms and data structures remain unchanged; this class organizes
 * the demonstrations into clear, evidence-oriented menus.
 */
public class
Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ConsoleUI.startup();
        waitBrieflyForStatus(scanner);

        boolean running = true;
        while (running) {
            showMainMenu();
            String choice = ConsoleUI.readChoice(scanner, "Select an option");

            switch (choice) {
                case "1" -> dataLoadingMenu(scanner);
                case "2" -> dataStructuresMenu(scanner);
                case "3" -> searchSortMenu(scanner);
                case "4" -> graphMenu(scanner);
                case "5" -> schedulingMenu(scanner);
                case "6" -> optimizationMenu(scanner);
                case "7" -> performanceMenu(scanner);
                case "8" -> dispatchMenu(scanner);
                case "0" -> running = false;
                default -> ConsoleUI.warning("Invalid option. Please select one of the displayed choices.");
            }
        }

        ConsoleUI.section("SESSION COMPLETE");
        System.out.println("Thank you for using Ghana Courier Service Optimizer.");
        scanner.close();
    }

    private static void showMainMenu() {
        ConsoleUI.clearHeader();
        String[] status = databaseStatus();
        ConsoleUI.status(status[0], status[1]);

        ConsoleUI.section("MAIN MENU");
        ConsoleUI.menuItem("1", "Data Loading & Database");
        ConsoleUI.menuItem("2", "Data Structures");
        ConsoleUI.menuItem("3", "Searching & Sorting");
        ConsoleUI.menuItem("4", "Graph Algorithms");
        ConsoleUI.menuItem("5", "Dispatch Scheduling");
        ConsoleUI.menuItem("6", "Optimization (Greedy vs DP)");
        ConsoleUI.menuItem("7", "Performance Lab");
        ConsoleUI.menuItem("8", "Integrated Dispatch");
        ConsoleUI.menuItem("0", "Exit");
    }

    private static void dataLoadingMenu(Scanner scanner) {
        while (true) {
            ConsoleUI.clearHeader();
            ConsoleUI.section("DATA LOADING & DATABASE");
            ConsoleUI.menuItem("1", "Load locations from database");
            ConsoleUI.menuItem("2", "Load service requests from database");
            ConsoleUI.menuItem("3", "Show current database status");
            ConsoleUI.menuItem("0", "Back");

            String choice = ConsoleUI.readChoice(scanner, "Select an option");
            switch (choice) {
                case "1" -> demoLoadLocations();
                case "2" -> demoLoadServiceRequests();
                case "3" -> showDatabaseStatus();
                case "0" -> { return; }
                default -> ConsoleUI.warning("Invalid option.");
            }
            if (!"0".equals(choice)) ConsoleUI.pause(scanner);
        }
    }

    private static void dataStructuresMenu(Scanner scanner) {
        while (true) {
            ConsoleUI.clearHeader();
            ConsoleUI.section("CUSTOM DATA STRUCTURES");
            ConsoleUI.menuItem("1", "Linked List + Iterator");
            ConsoleUI.menuItem("2", "Stack");
            ConsoleUI.menuItem("3", "Queue / Circular Queue");
            ConsoleUI.menuItem("4", "Deque");
            ConsoleUI.menuItem("5", "Priority Queue / Heap");
            ConsoleUI.menuItem("6", "BST");
            ConsoleUI.menuItem("7", "Hash Table");
            ConsoleUI.menuItem("8", "Dynamic Array");
            ConsoleUI.menuItem("9", "Red-Black Tree");
            ConsoleUI.menuItem("10", "B-Tree");
            ConsoleUI.menuItem("11", "Custom Set / Map");
            ConsoleUI.menuItem("12", "Disjoint Set");
            ConsoleUI.menuItem("13", "Adjacency Matrix");
            ConsoleUI.menuItem("0", "Back");

            String choice = ConsoleUI.readChoice(scanner, "Select an option");
            switch (choice) {
                case "1" -> demoLinkedList();
                case "2" -> demoStack();
                case "3" -> demoCircularQueue();
                case "4" -> demoDeque();
                case "5" -> demoPriorityQueue();
                case "6" -> demoBST();
                case "7" -> demoHashTable();
                case "8" -> demoDynamicArray();
                case "9" -> demoRedBlackTree();
                case "10" -> demoBTree();
                case "11" -> demoSetMap();
                case "12" -> demoDisjointSet();
                case "13" -> demoMatrix();
                case "0" -> { return; }
                default -> ConsoleUI.warning("Invalid option.");
            }
            if (!"0".equals(choice)) ConsoleUI.pause(scanner);
        }
    }

    private static void searchSortMenu(Scanner scanner) {
        ConsoleUI.clearHeader();
        ConsoleUI.section("SEARCHING & SORTING");
        ConsoleUI.menuItem("1", "Run search and sorting demonstration");
        ConsoleUI.menuItem("0", "Back");
        String choice = ConsoleUI.readChoice(scanner, "Select an option");
        if ("1".equals(choice)) {
            demoSearchAndSort();
            ConsoleUI.pause(scanner);
        }
    }

    private static void graphMenu(Scanner scanner) {
        while (true) {
            ConsoleUI.clearHeader();
            ConsoleUI.section("GRAPH ALGORITHMS");
            ConsoleUI.menuItem("1", "BFS & DFS traversal");
            ConsoleUI.menuItem("2", "Dijkstra shortest path");
            ConsoleUI.menuItem("3", "Kruskal vs Prim minimum spanning tree");
            ConsoleUI.menuItem("4", "Adjacency Matrix demonstration");
            ConsoleUI.menuItem("0", "Back");
            String choice = ConsoleUI.readChoice(scanner, "Select an option");
            switch (choice) {
                case "1" -> demoGraphTraversal();
                case "2" -> demoDijkstra();
                case "3" -> demoMst();
                case "4" -> demoMatrix();
                case "0" -> { return; }
                default -> ConsoleUI.warning("Invalid option.");
            }
            if (!"0".equals(choice)) ConsoleUI.pause(scanner);
        }
    }

    private static void schedulingMenu(Scanner scanner) {
        ConsoleUI.clearHeader();
        ConsoleUI.section("DISPATCH SCHEDULING");
        ConsoleUI.menuItem("1", "Compare FIFO and urgency-first scheduling");
        ConsoleUI.menuItem("0", "Back");
        String choice = ConsoleUI.readChoice(scanner, "Select an option");
        if ("1".equals(choice)) {
            demoScheduler();
            ConsoleUI.pause(scanner);
        }
    }

    private static void optimizationMenu(Scanner scanner) {
        ConsoleUI.clearHeader();
        ConsoleUI.section("OPTIMIZATION ENGINE");
        ConsoleUI.menuItem("1", "Greedy vs Dynamic Programming");
        ConsoleUI.menuItem("0", "Back");
        String choice = ConsoleUI.readChoice(scanner, "Select an option");
        if ("1".equals(choice)) {
            demoKnapsack();
            ConsoleUI.pause(scanner);
        }
    }

    private static void performanceMenu(Scanner scanner) {
        ConsoleUI.clearHeader();
        ConsoleUI.section("EMPIRICAL PERFORMANCE LAB");
        System.out.println("Each benchmark uses 3 measured runs per input size.");
        System.out.println("Results are exported to CSV and saved to the database when available.");
        ConsoleUI.menuItem("1", "Run performance lab");
        ConsoleUI.menuItem("0", "Back");
        String choice = ConsoleUI.readChoice(scanner, "Select an option");
        if ("1".equals(choice)) {
            runPerformanceLab();
            ConsoleUI.pause(scanner);
        }
    }

    private static void dispatchMenu(Scanner scanner) {
        while (true) {
            ConsoleUI.clearHeader();
            ConsoleUI.section("INTEGRATED DISPATCH");
            ConsoleUI.warning("Options 1 and 2 write operational changes to the database.");
            ConsoleUI.menuItem("1", "AUTO dispatch - assign pending requests");
            ConsoleUI.menuItem("2", "INTERACTIVE dispatch - confirm one at a time");
            ConsoleUI.menuItem("0", "Back");
            String choice = ConsoleUI.readChoice(scanner, "Select an option");
            switch (choice) {
                case "1" -> runAutoDispatch();
                case "2" -> runInteractiveDispatch(scanner);
                case "0" -> { return; }
                default -> ConsoleUI.warning("Invalid option.");
            }
            if (!"0".equals(choice)) ConsoleUI.pause(scanner);
        }
    }

    private static void waitBrieflyForStatus(Scanner scanner) {
        String[] status = databaseStatus();
        System.out.println("Database status: " + status[0]);
        if (!status[1].isBlank()) System.out.println("Records: " + status[1]);
        ConsoleUI.pause(scanner);
    }

    private static void showDatabaseStatus() {
        String[] status = databaseStatus();
        ConsoleUI.section("DATABASE STATUS");
        System.out.println("Connection : " + status[0]);
        System.out.println("Records    : " + (status[1].isBlank() ? "Unavailable" : status[1]));
    }

    private static String[] databaseStatus() {
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement()) {
            String counts = "locations=" + count(stmt, "locations")
                    + ", roads=" + count(stmt, "roads")
                    + ", requests=" + count(stmt, "service_requests")
                    + ", resources=" + count(stmt, "resources");
            return new String[]{"CONNECTED", counts};
        } catch (Exception e) {
            return new String[]{"NOT CONNECTED", ""};
        }
    }

    private static int count(Statement stmt, String table) throws Exception {
        try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + table)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    // =========================================================
    // PHASE 1 - DATA LOADING
    // =========================================================

    private static void demoLoadLocations() {
        ConsoleUI.section("LOAD LOCATIONS");
        try (Connection conn = DatabaseConnection.getConnection()) {
            MyLinkedList<Location> locations = new DataLoader().loadLocations(conn);
            System.out.println("Loaded " + locations.size() + " locations from MySQL.");
            System.out.println("Sample records:");
            MyIterator<Location> it = locations.iterator();
            int shown = 0;
            while (it.hasNext() && shown < 5) {
                System.out.println("  - " + it.next());
                shown++;
            }
            if (locations.size() > shown) System.out.println("  ... and " + (locations.size() - shown) + " more");
            ConsoleUI.success("Location data loaded into the custom linked list.");
        } catch (Exception e) {
            ConsoleUI.error("Database error: " + e.getMessage());
            System.out.println("Hint: run schema.sql and import the seed CSV files first.");
        }
    }

    private static void demoLoadServiceRequests() {
        ConsoleUI.section("LOAD SERVICE REQUESTS");
        try (Connection conn = DatabaseConnection.getConnection()) {
            MyLinkedList<ServiceRequest> requests = new DataLoader().loadServiceRequests(conn);
            System.out.println("Loaded " + requests.size() + " service requests.");
            ConsoleUI.success("Request records loaded successfully.");
        } catch (Exception e) {
            ConsoleUI.error("Database error: " + e.getMessage());
        }
    }

    private static void demoLinkedList() {
        ConsoleUI.section("LINKED LIST + ITERATOR");
        MyLinkedList<String> list = new MyLinkedList<>();
        list.addFirst("Osu Hub");
        list.addLast("Madina Hub");
        list.insertAfter("Osu Hub", "Circle Hub");

        System.out.println("After addFirst/addLast/insertAfter:");
        MyIterator<String> it = list.iterator();
        while (it.hasNext()) System.out.println("  -> " + it.next());
        System.out.println("Size: " + list.size());
        System.out.println("Remove: " + list.remove("Circle Hub"));
        System.out.println("First: " + list.peekFirst() + ", Last: " + list.peekLast());
    }

    private static void demoStack() {
        ConsoleUI.section("STACK - BRACKET BALANCE");
        MyStack<Character> stack = new MyStack<>();
        String command = "PRINT[RIDER(29)]";
        boolean balanced = true;
        for (char ch : command.toCharArray()) {
            if (ch == '(' || ch == '[' || ch == '{') stack.push(ch);
            else if (ch == ')' || ch == ']' || ch == '}') {
                if (stack.isEmpty()) { balanced = false; break; }
                stack.pop();
            }
        }
        balanced = balanced && stack.isEmpty();
        System.out.println("Input command : " + command);
        System.out.println("Balanced      : " + balanced);
        System.out.println("Stack state   : " + (stack.isEmpty() ? "empty" : "not empty"));
    }

    private static void demoCircularQueue() {
        ConsoleUI.section("CIRCULAR QUEUE - RIDER WAITING LINE");
        CircularQueue<String> queue = new CircularQueue<>(3);
        queue.enqueue("Rider-A");
        queue.enqueue("Rider-B");
        queue.enqueue("Rider-C");
        System.out.println("Initial queue : " + queue.snapshotInQueueOrder());
        System.out.println("Queue full?   : " + queue.isFull());
        System.out.println("Dequeue       : " + queue.dequeue());
        queue.enqueue("Rider-D");
        System.out.println("After wrap    : " + queue.snapshotInQueueOrder());
        ConsoleUI.success("Front/rear wrap-around behaviour demonstrated.");
    }

    private static void demoDeque() {
        ConsoleUI.section("DEQUE - URGENT REQUEST INSERTION");
        MyDeque<String> deque = new MyDeque<>();
        deque.addRear("Order-101");
        deque.addRear("Order-102");
        deque.addFront("URGENT-PHARMACY");
        System.out.println("Dispatch order: " + deque.snapshot());
        System.out.println("Front removed: " + deque.removeFront());
        System.out.println("Rear removed : " + deque.removeRear());
    }

    private static void demoPriorityQueue() {
        ConsoleUI.section("PRIORITY QUEUE / HEAP - URGENCY DISPATCH");
        record DemoTicket(String id, int urgency, int arrival) { }
        MyPriorityQueue<DemoTicket> pq = new MyPriorityQueue<>(
                Comparator.<DemoTicket>comparingInt(t -> -t.urgency())
                        .thenComparingInt(DemoTicket::arrival));
        pq.insert(new DemoTicket("R1", 2, 1));
        pq.insert(new DemoTicket("R2", 5, 2));
        pq.insert(new DemoTicket("R3", 4, 3));
        int rank = 1;
        while (!pq.isEmpty()) {
            DemoTicket t = pq.extract();
            System.out.println("  " + rank++ + ". " + t.id() + " | urgency=" + t.urgency());
        }
    }

    private static void demoBST() {
        ConsoleUI.section("BINARY SEARCH TREE - LOCATION INDEX");
        BST<String, Integer> index = new BST<>();
        index.insert("Osu", 1);
        index.insert("Madina", 2);
        index.insert("East Legon", 3);
        index.insert("Achimota", 4);
        System.out.println("In-order keys : " + index.inorderKeys());
        System.out.println("Search Madina : " + index.search("Madina"));
        System.out.println("Tree height   : " + index.height());
    }

    private static void demoHashTable() {
        ConsoleUI.section("HASH TABLE - REQUEST LOOKUP");
        MyHashTable<Integer, String> table = new MyHashTable<>(ProjectParameters.HASH_TABLE_SIZE);
        table.put(101, "FOOD delivery to Osu");
        table.put(102, "PARCEL to Madina");
        table.put(118, "GROCERY to East Legon");
        System.out.println("Lookup 101       : " + table.get(101));
        System.out.printf("Load factor      : %.3f%n", table.loadFactor());
        System.out.println("Collisions so far: " + table.collisionCount());
    }

    private static void demoDynamicArray() {
        ConsoleUI.section("DYNAMIC ARRAY - RESIZE TRACE");
        MyDynamicArray<String> array = new MyDynamicArray<>(2);
        System.out.println("Initial          : size=" + array.size() + ", capacity=" + array.capacity());
        array.add("Osu");
        array.add("Madina");
        System.out.println("After 2 inserts  : size=" + array.size() + ", capacity=" + array.capacity());
        array.add("East Legon");
        System.out.println("After resize     : " + array.resizeTrace());
        System.out.println("Values           : [" + array.get(0) + ", " + array.get(1) + ", " + array.get(2) + "]");
    }

    private static void demoRedBlackTree() {
        ConsoleUI.section("RED-BLACK TREE - BALANCING");
        RedBlackTree<Integer, String> rbt = new RedBlackTree<>();
        int[] keys = {30, 20, 10, 25, 40, 50};
        System.out.println("Insertion sequence: " + java.util.Arrays.toString(keys));
        for (int id : keys) rbt.insert(id, "Request-" + id);
        System.out.println("In-order traversal: " + rbt.inorderKeys());
        System.out.println("Root/children     : " + rbt.structureSummary());
        System.out.println("Rotations         : " + rbt.rotationCount());
        System.out.println("Height             : " + rbt.height());
        ConsoleUI.success("Balancing through recolouring/rotations preserved tree order.");
    }

    private static void demoBTree() {
        ConsoleUI.section("B-TREE - NODE SPLIT");
        BTree<Integer, String> btree = new BTree<>(2);
        int[] keys = {10, 20, 5, 6, 12, 30, 7, 17};
        System.out.println("Insertion sequence: " + java.util.Arrays.toString(keys));
        for (int id : keys) btree.insert(id, "Request-" + id);
        System.out.println("In-order traversal: " + btree.inorderKeys());
        System.out.println("Tree structure     : " + btree.structureSummary());
        System.out.println("Node splits        : " + btree.splitCount());
    }

    private static void demoSetMap() {
        ConsoleUI.section("CUSTOM SET / MAP");
        MySet<String> zones = new MySet<>(ProjectParameters.HASH_TABLE_SIZE);
        zones.add("Osu");
        zones.add("Madina");
        zones.add("Osu");
        MyMap<Integer, String> requestIndex = new MyMap<>(ProjectParameters.HASH_TABLE_SIZE);
        requestIndex.put(101, "Osu -> Airport");
        requestIndex.put(102, "Madina -> Circle");
        System.out.println("Set size         : " + zones.size());
        System.out.println("Contains Osu     : " + zones.contains("Osu"));
        System.out.println("Map lookup 101   : " + requestIndex.get(101));
        System.out.println("Map lookup 102   : " + requestIndex.get(102));
    }

    private static void demoDisjointSet() {
        ConsoleUI.section("DISJOINT SET / UNION-FIND");
        gh.dso.graph.DisjointSet ds = new gh.dso.graph.DisjointSet();
        for (String id : List.of("Osu", "Circle", "Airport", "Madina")) ds.makeSet(id);
        System.out.println("union(Osu, Circle)   -> " + ds.union("Osu", "Circle"));
        System.out.println("union(Circle, Airport)-> " + ds.union("Circle", "Airport"));
        System.out.println("union(Osu, Airport)  -> " + ds.union("Osu", "Airport") + " (cycle avoided)");
        System.out.println("connected(Osu, Airport): " + ds.connected("Osu", "Airport"));
    }

    private static void demoMatrix() {
        ConsoleUI.section("ADJACENCY MATRIX - WEIGHTED ROAD NETWORK");
        GraphMatrix matrix = new GraphMatrix();
        matrix.addRoad(new Road("R1", "Osu", "Circle", 2.0, 5.0, 1.0));
        matrix.addRoad(new Road("R2", "Circle", "Airport", 7.0, 15.0, 1.2));
        System.out.println("Neighbors of Circle: " + matrix.neighborsOf("Circle"));
        System.out.println();
        System.out.print(matrix.matrixTrace());
    }

    // =========================================================
    // PHASE 2 - SEARCH / SORT / GRAPH
    // =========================================================

    private static void demoSearchAndSort() {
        ConsoleUI.section("SEARCH & SORT ALGORITHMS");
        List<Integer> unsorted = new ArrayList<>(List.of(29, 4, 71, 15, 8, 42, 3, 56, 19, 1));
        System.out.println("Input           : " + unsorted);

        List<Integer> forQuickSort = new ArrayList<>(unsorted);
        var quickStats = SortAlgorithms.quickSort(forQuickSort, Comparator.naturalOrder());
        System.out.println("QuickSort       : " + forQuickSort);
        System.out.println("  comparisons=" + quickStats.comparisons() + ", swaps=" + quickStats.swaps());

        List<Integer> forMergeSort = new ArrayList<>(unsorted);
        var mergeStats = SortAlgorithms.mergeSort(forMergeSort, Comparator.naturalOrder());
        System.out.println("MergeSort       : " + forMergeSort);
        System.out.println("  comparisons=" + mergeStats.comparisons());

        var searchResult = SearchAlgorithms.binarySearch(forQuickSort, 42, Comparator.naturalOrder());
        System.out.println("BinarySearch 42 : index=" + searchResult.index()
                + ", comparisons=" + searchResult.comparisons());
        ConsoleUI.success("Binary search is demonstrated on the sorted output.");
    }

    private static void demoGraphTraversal() {
        ConsoleUI.section("BFS & DFS TRAVERSAL");
        Graph graph = loadGraphFromDb();
        if (graph == null) return;

        String start = graph.allLocationIds().get(0);
        System.out.println("Start location: " + start);
        System.out.println("BFS (first 10): " + firstN(GraphTraversal.bfs(graph, start), 10));
        System.out.println("DFS (first 10): " + firstN(GraphTraversal.dfs(graph, start), 10));
        System.out.println("Connected?     : " + GraphTraversal.isConnected(graph, start));
    }

    private static void demoDijkstra() {
        ConsoleUI.section("DIJKSTRA SHORTEST PATH");
        Graph graph = loadGraphFromDb();
        if (graph == null) return;

        List<String> ids = graph.allLocationIds();
        String source = ids.get(0);
        String target = ids.get(ids.size() / 2);

        Dijkstra.PathResult result = Dijkstra.shortestPaths(graph, source);
        Double distance = result.distances().get(target);
        System.out.println("Source      : " + source);
        System.out.println("Destination : " + target);
        System.out.println("Distance    : " + (distance == null ? "unreachable" : String.format("%.2f", distance)));
        System.out.println("Path        : " + result.pathTo(target));
    }

    private static void demoMst() {
        ConsoleUI.section("MINIMUM SPANNING TREE - KRUSKAL VS PRIM");
        Graph graph = loadGraphFromDb();
        if (graph == null) return;

        List<Road> roads;
        try (Connection conn = DatabaseConnection.getConnection()) {
            roads = toList(new DataLoader().loadRoads(conn));
        } catch (Exception e) {
            ConsoleUI.error("Database error: " + e.getMessage());
            return;
        }

        Kruskal.MstResult kruskalResult = Kruskal.buildMst(graph.allLocationIds(), roads);
        Prim.MstResult primResult = Prim.buildMst(graph, graph.allLocationIds().get(0));

        System.out.printf("Kruskal: edges=%d, total weight=%.2f%n", kruskalResult.edges().size(), kruskalResult.totalWeight());
        System.out.printf("Prim   : edges=%d, total weight=%.2f%n", primResult.edges().size(), primResult.totalWeight());
    }

    private static void demoScheduler() {
        ConsoleUI.section("DISPATCH SCHEDULING");
        try (Connection conn = DatabaseConnection.getConnection()) {
            List<ServiceRequest> requests = toList(new DataLoader().loadServiceRequests(conn));
            DispatchScheduler scheduler = new DispatchScheduler();

            List<ServiceRequest> urgencyOrder = scheduler.dispatchInUrgencyOrder(requests);
            System.out.println("Urgency-first (first 5):");
            urgencyOrder.stream().limit(5).forEach(r -> System.out.println("  " + r));

            MyDeque<ServiceRequest> fifo = scheduler.buildFifoQueue(requests);
            System.out.println("FIFO (first 5):");
            for (int i = 0; i < 5 && !fifo.isEmpty(); i++) System.out.println("  " + fifo.removeFront());
        } catch (Exception e) {
            ConsoleUI.error("Database error: " + e.getMessage());
        }
    }

    // =========================================================
    // PHASE 3 - OPTIMIZATION / DISPATCH / PERFORMANCE
    // =========================================================

    private static void runAutoDispatch() {
        ConsoleUI.section("AUTO DISPATCH");
        ConsoleUI.warning("This operation updates the database.");
        try (Connection conn = DatabaseConnection.getConnection()) {
            IntegratedDispatchService.DispatchSummary summary = new IntegratedDispatchService().runAuto(conn);
            System.out.printf("Assigned       : %d%n", summary.assignedCount());
            System.out.printf("Unassigned     : %d%n", summary.unassignedCount());
            System.out.printf("Total distance : %.2f%n", summary.totalDistance());
            ConsoleUI.success("Dispatch run completed.");
        } catch (Exception e) {
            ConsoleUI.error("Database error: " + e.getMessage());
        }
    }

    private static void runInteractiveDispatch(Scanner scanner) {
        ConsoleUI.section("INTERACTIVE DISPATCH");
        ConsoleUI.warning("You will be asked to confirm individual assignments.");
        try (Connection conn = DatabaseConnection.getConnection()) {
            new IntegratedDispatchService().runInteractive(conn, scanner);
        } catch (Exception e) {
            ConsoleUI.error("Database error: " + e.getMessage());
        }
    }

    private static void demoKnapsack() {
        ConsoleUI.section("GREEDY VS DYNAMIC PROGRAMMING");
        try (Connection conn = DatabaseConnection.getConnection()) {
            List<ServiceRequest> pending = toList(new DataLoader().loadServiceRequests(conn)).stream()
                    .filter(r -> "NEW".equals(r.getStatus()))
                    .limit(10)
                    .toList();

            if (pending.isEmpty()) {
                ConsoleUI.warning("No NEW requests available to demo with.");
                return;
            }

            int capacity = ProjectParameters.DEFAULT_VEHICLE_CAPACITY;
            System.out.println("Capacity: " + capacity);
            System.out.println("Candidates:");
            for (ServiceRequest r : pending) {
                System.out.println("  " + r.getRequestId() + " | " + r.getCategory()
                        + " | weight=" + KnapsackOptimizer.weightOf(r)
                        + " | urgency=" + r.getUrgency());
            }

            var greedyResult = KnapsackOptimizer.solveGreedy(pending, capacity);
            var dpResult = KnapsackOptimizer.solveDp(pending, capacity);

            System.out.println();
            System.out.println("Greedy -> " + idsOf(greedyResult.selected())
                    + " | value=" + greedyResult.totalValue()
                    + " | weight=" + greedyResult.totalWeight());
            System.out.println("DP     -> " + idsOf(dpResult.selected())
                    + " | value=" + dpResult.totalValue()
                    + " | weight=" + dpResult.totalWeight());

            if (dpResult.totalValue() > greedyResult.totalValue()) {
                System.out.println("Result: DP found a better feasible combination for this input.");
            } else {
                System.out.println("Result: greedy matched the optimum for this input.");
            }
        } catch (Exception e) {
            ConsoleUI.error("Database error: " + e.getMessage());
        }
    }

    private static List<String> idsOf(List<ServiceRequest> requests) {
        return requests.stream().map(ServiceRequest::getRequestId).toList();
    }

    private static void runPerformanceLab() {
        ConsoleUI.section("PERFORMANCE LAB");
        System.out.println("Running benchmarks with 3 measured runs per input size...");
        Random rnd = new Random(ProjectParameters.RANDOM_SEED);
        PerformanceLab lab = new PerformanceLab();
        lab.runSortBenchmarks(rnd);
        lab.runSearchBenchmarks(rnd);
        lab.runGraphBenchmarks(rnd);
        lab.runTreeBenchmarks(rnd);
        lab.runHashLoadFactorBenchmarks(rnd);
        lab.runHeapBenchmarks(rnd);
        lab.printSummary();

        try {
            lab.exportCsv("results/performance_results.csv");
            ConsoleUI.success("Exported results/performance_results.csv");
        } catch (Exception e) {
            ConsoleUI.error("CSV export failed: " + e.getMessage());
        }

        try {
            lab.exportHashLoadCsv("results/hash_load_factor_results.csv");
            ConsoleUI.success("Exported results/hash_load_factor_results.csv");
        } catch (Exception e) {
            ConsoleUI.error("Hash load-factor CSV export failed: " + e.getMessage());
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            lab.saveToDatabase(conn);
            ConsoleUI.success("Saved " + lab.getResults().size() + " measurements to algorithm_runs.");
        } catch (Exception e) {
            ConsoleUI.warning("Database save failed; CSV export is still available: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------

    private static Graph loadGraphFromDb() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            DataLoader loader = new DataLoader();
            List<Road> roads = toList(loader.loadRoads(conn));
            Graph graph = new Graph();
            for (Road road : roads) graph.addRoad(road);
            System.out.println("Loaded graph: " + graph.vertexCount() + " locations, " + roads.size() + " roads");
            return graph;
        } catch (Exception e) {
            ConsoleUI.error("Database error: " + e.getMessage());
            System.out.println("Hint: run database/schema.sql and import the seed CSV files.");
            return null;
        }
    }

    private static <T> List<T> toList(MyLinkedList<T> source) {
        List<T> list = new ArrayList<>();
        MyIterator<T> it = source.iterator();
        while (it.hasNext()) list.add(it.next());
        return list;
    }

    private static <T> List<T> firstN(List<T> source, int n) {
        return source.subList(0, Math.min(n, source.size()));
    }
}
