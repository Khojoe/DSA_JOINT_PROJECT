package gh.dso.performance;

import gh.dso.algorithms.search.SearchAlgorithms;
import gh.dso.algorithms.sort.SortAlgorithms;
import gh.dso.db.DataLoader;
import gh.dso.graph.Dijkstra;
import gh.dso.graph.Graph;
import gh.dso.graph.GraphTraversal;
import gh.dso.graph.Kruskal;
import gh.dso.graph.Prim;
import gh.dso.model.Road;
import gh.dso.datastructures.tree.BST;
import gh.dso.datastructures.tree.RedBlackTree;
import gh.dso.datastructures.hash.MyHashTable;
import gh.dso.datastructures.heap.MyPriorityQueue;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/** Empirical lab with three measured repetitions per algorithm/input size. */
public class PerformanceLab {
    private static final int REPEATS = 3;
    private static final int[] QUADRATIC_SIZES = {100, 500, 1000, 2000};
    private static final int[] LINEARITHMIC_SIZES = {100, 500, 1000, 5000, 10000};
    private static final int[] SEARCH_SIZES = {100, 1000, 10000, 50000};
    private static final int[] GRAPH_SIZES = {50, 100, 250, 500};
    private static final int[] TREE_SIZES = {100, 500, 1000, 2000};
    private static final int[] HASH_KEY_COUNTS = {100, 1000, 5000, 10000, 20000};
    private static final int[] HASH_TABLE_SIZES = {31, 101, 1009, 5003};
    private static final int[] HEAP_SIZES = {100, 1000, 5000, 10000, 20000};

    public record BenchmarkRow(String algorithmName, int inputSize, int runNumber, long timeNs) { }
    public record HashLoadRow(int tableSize, int numKeys, double loadFactor, int collisionCount, long putTimeNs) { }
    private final DataLoader loader = new DataLoader();
    private final List<BenchmarkRow> results = new ArrayList<>();
    private final List<HashLoadRow> hashLoadResults = new ArrayList<>();

    public List<BenchmarkRow> getResults() { return results; }
    public List<HashLoadRow> getHashLoadResults() { return hashLoadResults; }
    public int repeatCount() { return REPEATS; }

    public void runSortBenchmarks(Random rnd) {
        for (int size : QUADRATIC_SIZES) {
            for (String algorithm : List.of("SelectionSort", "InsertionSort")) {
                List<List<Integer>> datasets = new ArrayList<>();
                for (int r = 0; r < REPEATS; r++) datasets.add(randomList(size, rnd));
                for (int r = 0; r < REPEATS; r++) {
                    List<Integer> data = datasets.get(r);
                    long start = System.nanoTime();
                    if (algorithm.equals("SelectionSort")) SortAlgorithms.selectionSort(data, Comparator.naturalOrder());
                    else SortAlgorithms.insertionSort(data, Comparator.naturalOrder());
                    results.add(new BenchmarkRow(algorithm, size, r + 1, System.nanoTime() - start));
                }
            }
        }
        for (int size : LINEARITHMIC_SIZES) {
            for (String algorithm : List.of("MergeSort", "QuickSort")) {
                List<List<Integer>> datasets = new ArrayList<>();
                for (int r = 0; r < REPEATS; r++) datasets.add(randomList(size, rnd));
                for (int r = 0; r < REPEATS; r++) {
                    List<Integer> data = datasets.get(r);
                    long start = System.nanoTime();
                    if (algorithm.equals("MergeSort")) SortAlgorithms.mergeSort(data, Comparator.naturalOrder());
                    else SortAlgorithms.quickSort(data, Comparator.naturalOrder());
                    results.add(new BenchmarkRow(algorithm, size, r + 1, System.nanoTime() - start));
                }
            }
        }
    }

    public void runSearchBenchmarks(Random rnd) {
        for (int size : SEARCH_SIZES) {
            List<Integer> sorted = new ArrayList<>(size);
            for (int i = 0; i < size; i++) sorted.add(i);
            int target = rnd.nextInt(size);
            for (String algorithm : List.of("LinearSearch", "BinarySearch")) {
                for (int r = 1; r <= REPEATS; r++) {
                    long start = System.nanoTime();
                    if (algorithm.equals("LinearSearch")) SearchAlgorithms.linearSearch(sorted, target, Comparator.naturalOrder());
                    else SearchAlgorithms.binarySearch(sorted, target, Comparator.naturalOrder());
                    results.add(new BenchmarkRow(algorithm, size, r, System.nanoTime() - start));
                }
            }
        }
    }

    public void runGraphBenchmarks(Random rnd) {
        for (int size : GRAPH_SIZES) {
            List<Graph> graphs = new ArrayList<>();
            for (int r = 0; r < REPEATS; r++) graphs.add(randomConnectedGraph(size, rnd));
            for (String algorithm : List.of("BFS", "DFS", "Dijkstra", "Kruskal", "Prim")) {
                for (int r = 0; r < REPEATS; r++) {
                    Graph graph = graphs.get(r);
                    List<String> ids = graph.allLocationIds();
                    String startVertex = ids.get(0);
                    List<Road> roads = algorithm.equals("Kruskal") ? extractRoads(graph) : null;
                    long start = System.nanoTime();
                    switch (algorithm) {
                        case "BFS" -> GraphTraversal.bfs(graph, startVertex);
                        case "DFS" -> GraphTraversal.dfs(graph, startVertex);
                        case "Dijkstra" -> Dijkstra.shortestPaths(graph, startVertex);
                        case "Kruskal" -> Kruskal.buildMst(ids, roads);
                        case "Prim" -> Prim.buildMst(graph, startVertex);
                    }
                    results.add(new BenchmarkRow(algorithm, size, r + 1, System.nanoTime() - start));
                }
            }
        }
    }

    /**
     * BST (unbalanced) vs Red-Black Tree (balanced) — brief Section 9's
     * "BST vs balanced tree" experiment. Measures insert and search time,
     * and prints a height comparison table for report evidence.
     */
    public void runTreeBenchmarks(Random rnd) {
        for (int size : TREE_SIZES) {
            for (int r = 1; r <= REPEATS; r++) {
                List<Integer> keys = randomList(size, rnd);

                BST<Integer, Integer> bst = new BST<>();
                long startBst = System.nanoTime();
                for (int i = 0; i < size; i++) bst.insert(keys.get(i), i);
                results.add(new BenchmarkRow("BST_Insert", size, r, System.nanoTime() - startBst));

                RedBlackTree<Integer, Integer> rbt = new RedBlackTree<>();
                long startRbt = System.nanoTime();
                for (int i = 0; i < size; i++) rbt.insert(keys.get(i), i);
                results.add(new BenchmarkRow("RedBlackTree_Insert", size, r, System.nanoTime() - startRbt));

                long searchBstStart = System.nanoTime();
                for (int key : keys) bst.search(key);
                results.add(new BenchmarkRow("BST_Search", size, r, System.nanoTime() - searchBstStart));

                long searchRbtStart = System.nanoTime();
                for (int key : keys) rbt.search(key);
                results.add(new BenchmarkRow("RedBlackTree_Search", size, r, System.nanoTime() - searchRbtStart));
            }
        }

        System.out.println("\n--- Height Comparison: BST (unbalanced) vs Red-Black Tree (balanced) ---");
        System.out.printf("%-10s %-15s %-18s %-25s%n", "Size", "BST Height", "RBTree Height", "Worst-case BST (Sequential)");
        for (int size : TREE_SIZES) {
            BST<Integer, Integer> randBst = new BST<>();
            RedBlackTree<Integer, Integer> randRbt = new RedBlackTree<>();
            BST<Integer, Integer> seqBst = new BST<>();
            for (int i = 0; i < size; i++) {
                int key = rnd.nextInt(size * 10);
                randBst.insert(key, i);
                randRbt.insert(key, i);
                seqBst.insert(i, i);
            }
            System.out.printf("%-10d %-15d %-18d %-25d%n", size, randBst.height(), randRbt.height(), seqBst.height());
        }
        System.out.println("-----------------------------------------------------------------");
    }

    /** Hash table load-factor experiment — brief Section 9: 100 to 20,000 keys, varying table sizes. */
    public void runHashLoadFactorBenchmarks(Random rnd) {
        for (int tableSize : HASH_TABLE_SIZES) {
            for (int numKeys : HASH_KEY_COUNTS) {
                MyHashTable<Integer, Integer> table = new MyHashTable<>(tableSize);
                List<Integer> keys = randomList(numKeys, rnd);
                long start = System.nanoTime();
                for (int i = 0; i < numKeys; i++) table.put(keys.get(i), i);
                long elapsed = System.nanoTime() - start;
                hashLoadResults.add(new HashLoadRow(tableSize, numKeys, table.loadFactor(), table.collisionCount(), elapsed));
            }
        }
    }

    /** Heap priority dispatch experiment — brief Section 9: 100 to 20,000 requests. */
    public void runHeapBenchmarks(Random rnd) {
        for (int size : HEAP_SIZES) {
            for (int r = 1; r <= REPEATS; r++) {
                List<Integer> values = randomList(size, rnd);

                MyPriorityQueue<Integer> pq = new MyPriorityQueue<Integer>(Comparator.naturalOrder());
                long insertStart = System.nanoTime();
                for (int v : values) pq.insert(v);
                results.add(new BenchmarkRow("Heap_InsertAll", size, r, System.nanoTime() - insertStart));

                long extractStart = System.nanoTime();
                while (!pq.isEmpty()) pq.extract();
                results.add(new BenchmarkRow("Heap_ExtractAll", size, r, System.nanoTime() - extractStart));
            }
        }
    }

    public void saveToDatabase(Connection conn) throws SQLException {
        for (BenchmarkRow row : results) loader.recordAlgorithmRun(conn, row.algorithmName(), row.inputSize(), row.timeNs(), 0);
    }

    public void exportCsv(String path) throws IOException {
        java.io.File file = new java.io.File(path);
        java.io.File parent = file.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("algorithm_name,input_size,run_number,time_ns,average_time_ns\n");
            for (BenchmarkRow row : results) {
                writer.write(row.algorithmName() + "," + row.inputSize() + "," + row.runNumber() + "," + row.timeNs() + "," + averageNs(row.algorithmName(), row.inputSize()) + "\n");
            }
        }
    }

    public void exportHashLoadCsv(String path) throws IOException {
        java.io.File file = new java.io.File(path);
        java.io.File parent = file.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("table_size,num_keys,load_factor,collision_count,put_time_ns\n");
            for (HashLoadRow row : hashLoadResults) {
                writer.write(row.tableSize() + "," + row.numKeys() + "," + row.loadFactor() + ","
                        + row.collisionCount() + "," + row.putTimeNs() + "\n");
            }
        }
    }

    public void printSummary() {
        System.out.printf("%-16s %10s %15s %15s%n", "Algorithm", "Input Size", "Avg Time (ns)", "Runs");
        String last = ""; int lastSize = -1;
        for (BenchmarkRow row : results) {
            if (!row.algorithmName().equals(last) || row.inputSize() != lastSize) {
                System.out.printf("%-16s %10d %15d %15d%n", row.algorithmName(), row.inputSize(), averageNs(row.algorithmName(), row.inputSize()), REPEATS);
                last = row.algorithmName(); lastSize = row.inputSize();
            }
        }
    }

    private long averageNs(String algorithm, int inputSize) {
        long total = 0; int count = 0;
        for (BenchmarkRow row : results) if (row.algorithmName().equals(algorithm) && row.inputSize() == inputSize) { total += row.timeNs(); count++; }
        return count == 0 ? 0 : total / count;
    }

    private List<Integer> randomList(int size, Random rnd) {
        List<Integer> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) list.add(rnd.nextInt(Math.max(1, size * 10)));
        return list;
    }

    private Graph randomConnectedGraph(int nodeCount, Random rnd) {
        Graph graph = new Graph(); List<String> ids = new ArrayList<>();
        for (int i = 0; i < nodeCount; i++) { String id = "N" + i; ids.add(id); graph.addLocation(id); }
        int roadNum = 1;
        for (int i = 1; i < nodeCount; i++) {
            String a = ids.get(i), b = ids.get(rnd.nextInt(i)); double w = 1 + rnd.nextDouble() * 20;
            graph.addRoad(new Road("PR" + (roadNum++), a, b, w, w, 1.0));
        }
        for (int i = 0; i < nodeCount; i++) {
            String a = ids.get(rnd.nextInt(nodeCount)), b = ids.get(rnd.nextInt(nodeCount));
            if (!a.equals(b)) { double w = 1 + rnd.nextDouble() * 20; graph.addRoad(new Road("PR" + (roadNum++), a, b, w, w, 1.0)); }
        }
        return graph;
    }

    private List<Road> extractRoads(Graph graph) {
        List<Road> roads = new ArrayList<>();
        java.util.Set<String> seen = new java.util.HashSet<>();
        for (String id : graph.allLocationIds()) for (Graph.Edge edge : graph.neighborsOf(id)) if (seen.add(edge.roadId())) roads.add(new Road(edge.roadId(), id, edge.to(), edge.weight(), edge.weight(), 1.0));
        return roads;
    }
}