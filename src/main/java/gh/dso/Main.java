package gh.dso;

import gh.dso.algorithms.search.SearchAlgorithms;
import gh.dso.algorithms.sort.SortAlgorithms;
import gh.dso.datastructures.deque.MyDeque;
import gh.dso.datastructures.hash.MyHashTable;
import gh.dso.datastructures.heap.MyPriorityQueue;
import gh.dso.datastructures.list.MyIterator;
import gh.dso.datastructures.list.MyLinkedList;
import gh.dso.datastructures.list.MyArrayList;
import gh.dso.datastructures.queue.CircularQueue;
import gh.dso.datastructures.stack.MyStack;
import gh.dso.datastructures.tree.BST;
import gh.dso.datastructures.tree.AVLTree;
import gh.dso.datastructures.tree.BTree;
import gh.dso.datastructures.setmap.MyMap;
import gh.dso.datastructures.setmap.MySet;
import gh.dso.db.DataLoader;
import gh.dso.db.DatabaseConnection;
import gh.dso.graph.Dijkstra;
import gh.dso.graph.Graph;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Console app covering all three phases:
 *   Phase 1 - custom data structures + DB loader
 *   Phase 2 - search/sort, graph engine, dispatch scheduling
 *   Phase 3 - greedy/DP optimisation, wired together into one
 *             real dispatch operation (options 14-16 below)
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n=== Ghana Courier DSO - Demo Menu ===");
            System.out.println("--- Phase 1: data + structures ---");
            System.out.println("1. Load locations from database");
            System.out.println("2. Load service requests from database");
            System.out.println("3. Demo: MyStack (bracket balance check)");
            System.out.println("4. Demo: CircularQueue (rider hub waiting line)");
            System.out.println("5. Demo: MyDeque (urgent request insertion)");
            System.out.println("6. Demo: MyPriorityQueue (urgency dispatch order)");
            System.out.println("7. Demo: BST (location name index)");
            System.out.println("8. Demo: MyHashTable (request lookup by id)");
            System.out.println("--- Phase 2: algorithms + graph engine ---");
            System.out.println("9. Demo: Search & Sort algorithms");
            System.out.println("10. Demo: BFS & DFS traversal (from DB road network)");
            System.out.println("11. Demo: Dijkstra shortest path (from DB road network)");
            System.out.println("12. Demo: Kruskal vs Prim minimum spanning tree");
            System.out.println("13. Demo: Dispatch scheduler (FIFO vs urgency)");
            System.out.println("--- Phase 3: integrated dispatch (writes to database) ---");
            System.out.println("14. Run dispatch - AUTO (assigns all pending requests)");
            System.out.println("15. Run dispatch - INTERACTIVE (confirm one at a time)");
            System.out.println("16. Demo: Greedy vs DP (capacity-constrained request selection)");
            System.out.println("17. Run empirical performance lab (real timings -> DB + CSV)");
            System.out.println("18. Demo: New custom structures (ArrayList, AVL, B-Tree, Set/Map, Graph Matrix)");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> demoLoadLocations();
                case "2" -> demoLoadServiceRequests();
                case "3" -> demoStack();
                case "4" -> demoCircularQueue();
                case "5" -> demoDeque();
                case "6" -> demoPriorityQueue();
                case "7" -> demoBST();
                case "8" -> demoHashTable();
                case "9" -> demoSearchAndSort();
                case "10" -> demoGraphTraversal();
                case "11" -> demoDijkstra();
                case "12" -> demoMst();
                case "13" -> demoScheduler();
                case "14" -> runAutoDispatch();
                case "15" -> runInteractiveDispatch(scanner);
                case "16" -> demoKnapsack();
                case "17" -> runPerformanceLab();
                case "18" -> demoAdvancedStructures();
                case "0" -> running = false;
                default -> System.out.println("Invalid option.");
            }
        }
        scanner.close();
        try {
            com.mysql.cj.jdbc.AbandonedConnectionCleanupThread.checkedShutdown();
        } catch (Throwable t) {
            // Ignore
        }
    }

    private static void demoLoadLocations() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            MyLinkedList<Location> locations = new DataLoader().loadLocations(conn);
            System.out.println("Loaded " + locations.size() + " locations:");
            MyIterator<Location> it = locations.iterator();
            int shown = 0;
            while (it.hasNext() && shown < 5) {
                System.out.println("  " + it.next());
                shown++;
            }
            System.out.println("  ... (" + (locations.size() - shown) + " more)");
        } catch (Exception e) {
            System.out.println("DB error: " + e.getMessage());
            System.out.println("(Have you run database/schema.sql and imported the seed CSVs?)");
        }
    }

    private static void demoLoadServiceRequests() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            MyLinkedList<ServiceRequest> requests = new DataLoader().loadServiceRequests(conn);
            System.out.println("Loaded " + requests.size() + " service requests.");
        } catch (Exception e) {
            System.out.println("DB error: " + e.getMessage());
        }
    }

    private static void demoStack() {
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
        System.out.println("Command: " + command + " -> balanced=" + balanced);
    }

    private static void demoCircularQueue() {
        CircularQueue<String> queue = new CircularQueue<>(3);
        queue.enqueue("Rider-A");
        queue.enqueue("Rider-B");
        queue.enqueue("Rider-C");
        System.out.println("Queue full? " + queue.isFull());
        System.out.println("Dequeue: " + queue.dequeue());
        queue.enqueue("Rider-D");
        System.out.println("Snapshot: " + queue.snapshotInQueueOrder());
    }

    private static void demoDeque() {
        MyDeque<String> deque = new MyDeque<>();
        deque.addRear("Order-101");
        deque.addRear("Order-102");
        deque.addFront("Order-URGENT-PHARMACY");
        System.out.println("Dispatch line: " + deque.snapshot());
    }

    private static void demoPriorityQueue() {
        record DemoTicket(String id, int urgency, int arrival) { }
        MyPriorityQueue<DemoTicket> pq = new MyPriorityQueue<>(
                Comparator.<DemoTicket>comparingInt(t -> -t.urgency())
                        .thenComparingInt(DemoTicket::arrival));
        pq.insert(new DemoTicket("R1", 2, 1));
        pq.insert(new DemoTicket("R2", 5, 2));
        pq.insert(new DemoTicket("R3", 4, 3));
        while (!pq.isEmpty()) {
            DemoTicket t = pq.extract();
            System.out.println("Dispatch: " + t.id() + " (urgency=" + t.urgency() + ")");
        }
    }

    private static void demoBST() {
        BST<String, Integer> index = new BST<>();
        index.insert("Osu", 1);
        index.insert("Madina", 2);
        index.insert("East Legon", 3);
        index.insert("Achimota", 4);
        System.out.println("Inorder: " + index.inorderKeys());
        System.out.println("Search 'Madina': " + index.search("Madina"));
        System.out.println("Tree height: " + index.height());
    }

    private static void demoHashTable() {
        MyHashTable<Integer, String> table = new MyHashTable<>(ProjectParameters.HASH_TABLE_SIZE);
        table.put(101, "FOOD delivery to Osu");
        table.put(102, "PARCEL to Madina");
        table.put(118, "GROCERY to East Legon"); // may collide with 102 depending on hash spread
        System.out.println("Lookup 101: " + table.get(101));
        System.out.println("Load factor: " + table.loadFactor());
        System.out.println("Collisions so far: " + table.collisionCount());
    }

    // =========================================================
    // PHASE 2 DEMOS
    // =========================================================

    private static void demoSearchAndSort() {
        List<Integer> unsorted = new ArrayList<>(List.of(29, 4, 71, 15, 8, 42, 3, 56, 19, 1));
        System.out.println("Unsorted: " + unsorted);

        List<Integer> forQuickSort = new ArrayList<>(unsorted);
        var quickStats = SortAlgorithms.quickSort(forQuickSort, Comparator.naturalOrder());
        System.out.println("QuickSort result: " + forQuickSort +
                " (comparisons=" + quickStats.comparisons() + ", swaps=" + quickStats.swaps() + ")");

        List<Integer> forMergeSort = new ArrayList<>(unsorted);
        var mergeStats = SortAlgorithms.mergeSort(forMergeSort, Comparator.naturalOrder());
        System.out.println("MergeSort result: " + forMergeSort +
                " (comparisons=" + mergeStats.comparisons() + ")");

        var searchResult = SearchAlgorithms.binarySearch(forQuickSort, 42, Comparator.naturalOrder());
        System.out.println("Binary search for 42 -> index=" + searchResult.index() +
                " in " + searchResult.comparisons() + " comparisons");
    }

    private static void demoGraphTraversal() {
        Graph graph = loadGraphFromDb();
        if (graph == null) return;

        String start = graph.allLocationIds().get(0);
        System.out.println("Starting from: " + start);
        System.out.println("BFS order (first 10): " + firstN(GraphTraversal.bfs(graph, start), 10));
        System.out.println("DFS order (first 10): " + firstN(GraphTraversal.dfs(graph, start), 10));
        System.out.println("Fully connected? " + GraphTraversal.isConnected(graph, start));
    }

    private static void demoDijkstra() {
        Graph graph = loadGraphFromDb();
        if (graph == null) return;

        List<String> ids = graph.allLocationIds();
        String source = ids.get(0);
        String target = ids.get(ids.size() / 2);

        Dijkstra.PathResult result = Dijkstra.shortestPaths(graph, source);
        Double distance = result.distances().get(target);
        System.out.println("Shortest weighted distance " + source + " -> " + target + ": " +
                (distance == null ? "unreachable" : String.format("%.2f", distance)));
        System.out.println("Path: " + result.pathTo(target));
    }

    private static void demoMst() {
        Graph graph = loadGraphFromDb();
        if (graph == null) return;

        List<Road> roads;
        try (Connection conn = DatabaseConnection.getConnection()) {
            MyLinkedList<Road> loaded = new DataLoader().loadRoads(conn);
            roads = toList(loaded);
        } catch (Exception e) {
            System.out.println("DB error: " + e.getMessage());
            return;
        }

        Kruskal.MstResult kruskalResult = Kruskal.buildMst(graph.allLocationIds(), roads);
        Prim.MstResult primResult = Prim.buildMst(graph, graph.allLocationIds().get(0));

        System.out.println("Kruskal MST: " + kruskalResult.edges().size() + " edges, total weight=" +
                String.format("%.2f", kruskalResult.totalWeight()));
        System.out.println("Prim MST:    " + primResult.edges().size() + " edges, total weight=" +
                String.format("%.2f", primResult.totalWeight()));
    }

    private static void demoScheduler() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            List<ServiceRequest> requests = toList(new DataLoader().loadServiceRequests(conn));
            DispatchScheduler scheduler = new DispatchScheduler();

            List<ServiceRequest> urgencyOrder = scheduler.dispatchInUrgencyOrder(requests);
            System.out.println("First 5 by urgency-first dispatch:");
            urgencyOrder.stream().limit(5).forEach(r -> System.out.println("  " + r));

            MyDeque<ServiceRequest> fifo = scheduler.buildFifoQueue(requests);
            System.out.println("First 5 by FIFO (submission order):");
            for (int i = 0; i < 5 && !fifo.isEmpty(); i++) {
                System.out.println("  " + fifo.removeFront());
            }
        } catch (Exception e) {
            System.out.println("DB error: " + e.getMessage());
        }
    }

    // =========================================================
    // PHASE 3 DEMOS - integrated dispatch (real DB writes)
    // =========================================================

    private static void runAutoDispatch() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("Running auto dispatch (this WILL update the database)...");
            IntegratedDispatchService.DispatchSummary summary = new IntegratedDispatchService().runAuto(conn);
            System.out.printf("%nDone. Assigned=%d, Unassigned=%d, Total distance=%.2f%n",
                    summary.assignedCount(), summary.unassignedCount(), summary.totalDistance());
        } catch (Exception e) {
            System.out.println("DB error: " + e.getMessage());
        }
    }

    private static void runInteractiveDispatch(Scanner scanner) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            new IntegratedDispatchService().runInteractive(conn, scanner);
        } catch (Exception e) {
            System.out.println("DB error: " + e.getMessage());
        }
    }

    private static void demoKnapsack() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            List<ServiceRequest> pending = toList(new DataLoader().loadServiceRequests(conn)).stream()
                    .filter(r -> "NEW".equals(r.getStatus()))
                    .limit(10)
                    .toList();

            if (pending.isEmpty()) {
                System.out.println("No NEW requests available to demo with.");
                return;
            }

            int capacity = ProjectParameters.DEFAULT_VEHICLE_CAPACITY; // derived from index numbers, see ProjectParameters
            System.out.println("Candidate requests (weight=parcel size, value=urgency), capacity=" + capacity + ":");
            for (ServiceRequest r : pending) {
                System.out.println("  " + r.getRequestId() + " " + r.getCategory() +
                        " weight=" + KnapsackOptimizer.weightOf(r) + " urgency=" + r.getUrgency());
            }

            var greedyResult = KnapsackOptimizer.solveGreedy(pending, capacity);
            var dpResult = KnapsackOptimizer.solveDp(pending, capacity);

            System.out.println("\nGreedy picks: " + idsOf(greedyResult.selected()) +
                    " totalValue=" + greedyResult.totalValue() + " totalWeight=" + greedyResult.totalWeight());
            System.out.println("DP picks:     " + idsOf(dpResult.selected()) +
                    " totalValue=" + dpResult.totalValue() + " totalWeight=" + dpResult.totalWeight());

            if (dpResult.totalValue() > greedyResult.totalValue()) {
                System.out.println("-> DP found a strictly better combination than greedy this time.");
            } else {
                System.out.println("-> Greedy happened to match DP's optimum for this particular input.");
            }
        } catch (Exception e) {
            System.out.println("DB error: " + e.getMessage());
        }
    }

    private static List<String> idsOf(List<ServiceRequest> requests) {
        return requests.stream().map(ServiceRequest::getRequestId).toList();
    }

    private static void runPerformanceLab() {
        System.out.println("Running empirical performance lab (this takes a few seconds)...");
        Random rnd = new Random(ProjectParameters.RANDOM_SEED); // reproducible, see ProjectParameters

        PerformanceLab lab = new PerformanceLab();
        lab.runSortBenchmarks(rnd);
        lab.runSearchBenchmarks(rnd);
        lab.runGraphBenchmarks(rnd);
        lab.runTreeBenchmarks(rnd);

        lab.printSummary();

        try {
            lab.exportCsv("results/performance_results.csv");
            System.out.println("\nExported to results/performance_results.csv (chart this in Excel/Sheets).");
        } catch (Exception e) {
            System.out.println("CSV export failed: " + e.getMessage());
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            lab.saveToDatabase(conn);
            System.out.println("Saved " + lab.getResults().size() + " measurements to the algorithm_runs table.");
        } catch (Exception e) {
            System.out.println("DB save failed (CSV export above still succeeded): " + e.getMessage());
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
            System.out.println("DB error: " + e.getMessage());
            System.out.println("(Have you run database/schema.sql and imported the seed CSVs?)");
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

    private static void demoAdvancedStructures() {
        System.out.println("\n--- A. MyArrayList & Resize Trace ---");
        MyArrayList<String> list = new MyArrayList<>(3);
        System.out.println("Initial capacity: " + list.capacity());
        list.add("Accra");
        list.add("Kumasi");
        list.add("Tamale");
        System.out.println("Adding 4th element (will trigger resize trace)...");
        list.add("Takoradi");
        System.out.println("List elements: " + list.get(0) + ", " + list.get(1) + ", " + list.get(2) + ", " + list.get(3));
        System.out.println("New capacity: " + list.capacity());

        System.out.println("\n--- B. AVL Tree (Balanced BST) Rotations ---");
        AVLTree<Integer, String> avl = new AVLTree<>();
        System.out.println("Inserting 10, 20, 30 (should trigger RR imbalance -> Left Rotation)...");
        avl.insert(10, "Ten");
        avl.insert(20, "Twenty");
        avl.insert(30, "Thirty");
        System.out.println("Inorder keys: " + avl.inorderKeys());
        System.out.println("AVL Height (expected 2): " + avl.height());

        System.out.println("\nInserting 40, 50 (should trigger another Left Rotation)...");
        avl.insert(40, "Forty");
        avl.insert(50, "Fifty");
        System.out.println("Inorder keys: " + avl.inorderKeys());
        System.out.println("AVL Height: " + avl.height());

        System.out.println("\n--- C. B-Tree Node Splits & Search ---");
        BTree<Integer, String> btree = new BTree<>();
        System.out.println("Inserting keys 1 to 6 (order T=3, splits at 5 keys)...");
        for (int i = 1; i <= 6; i++) {
            btree.insert(i, "Value-" + i);
        }
        System.out.println("B-Tree traversal keys: " + btree.traverseKeys());
        System.out.println("Search key 4: " + btree.search(4));

        System.out.println("\n--- D. Custom Set and Map (on MyHashTable) ---");
        MySet<String> set = new MySet<>(5);
        set.add("Rider-1");
        set.add("Rider-2");
        set.add("Rider-1"); // duplicate
        System.out.println("Set size (expected 2): " + set.size());
        System.out.println("Set contains Rider-1? " + set.contains("Rider-1"));

        MyMap<String, String> map = new MyMap<>(5);
        map.put("R001", "Assigned");
        map.put("R002", "Pending");
        System.out.println("Map size: " + map.size());
        System.out.println("Map lookup R001: " + map.get("R001"));

        System.out.println("\n--- E. Graph Adjacency Matrix ---");
        Graph graph = loadGraphFromDb();
        if (graph != null) {
            double[][] matrix = graph.toAdjacencyMatrix();
            List<String> ids = graph.allLocationIds();
            System.out.println("Adjacency Matrix (First 5 locations):");
            // print header
            System.out.printf("%-8s", "");
            for (int i = 0; i < Math.min(5, ids.size()); i++) {
                System.out.printf("%-10s", ids.get(i));
            }
            System.out.println();
            for (int i = 0; i < Math.min(5, ids.size()); i++) {
                System.out.printf("%-8s", ids.get(i));
                for (int j = 0; j < Math.min(5, ids.size()); j++) {
                    double val = matrix[i][j];
                    if (val == Double.POSITIVE_INFINITY) {
                        System.out.printf("%-10s", "INF");
                    } else {
                        System.out.printf("%-10.2f", val);
                    }
                }
                System.out.println();
            }
        }
    }
}
