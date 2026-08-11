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
import gh.dso.datastructures.tree.AVLTree;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Runs each Phase 2 algorithm at increasing input sizes, times it with
 * System.nanoTime(), records every measurement into the algorithm_runs
 * table (via DataLoader), and exports the same data to a CSV so it can
 * be charted (e.g. in Excel/Sheets) for the report's time-vs-input-size
 * graphs.
 *
 * This replaces the placeholder timings in the original seed CSV with
 * real, reproducible measurements (seeded via ProjectParameters.RANDOM_SEED
 * so re-runs are comparable).
 */
public class PerformanceLab {

    // O(n^2) algorithms get smaller sizes so a full run finishes in reasonable time.
    private static final int[] QUADRATIC_SIZES = {100, 500, 1000, 2000};
    // O(n log n) algorithms can handle larger sizes.
    private static final int[] LINEARITHMIC_SIZES = {100, 500, 1000, 5000, 10000};
    // Search sizes (binary search needs sorted input, built once per size).
    private static final int[] SEARCH_SIZES = {100, 1000, 10000, 50000};
    // Graph sizes are node counts; edge count scales with node count.
    private static final int[] GRAPH_SIZES = {50, 100, 250, 500};

    public record BenchmarkRow(String algorithmName, int inputSize, long timeNs) { }

    private final DataLoader loader = new DataLoader();
    private final List<BenchmarkRow> results = new ArrayList<>();

    public List<BenchmarkRow> getResults() {
        return results;
    }

    // -------------------------------------------------------------
    // SORTING
    // -------------------------------------------------------------

    public void runSortBenchmarks(Random rnd) {
        for (int size : QUADRATIC_SIZES) {
            time("SelectionSort", size, () -> {
                List<Integer> data = randomList(size, rnd);
                SortAlgorithms.selectionSort(data, Comparator.naturalOrder());
            });
            time("InsertionSort", size, () -> {
                List<Integer> data = randomList(size, rnd);
                SortAlgorithms.insertionSort(data, Comparator.naturalOrder());
            });
        }
        for (int size : LINEARITHMIC_SIZES) {
            time("MergeSort", size, () -> {
                List<Integer> data = randomList(size, rnd);
                SortAlgorithms.mergeSort(data, Comparator.naturalOrder());
            });
            time("QuickSort", size, () -> {
                List<Integer> data = randomList(size, rnd);
                SortAlgorithms.quickSort(data, Comparator.naturalOrder());
            });
        }
    }

    // -------------------------------------------------------------
    // SEARCHING
    // -------------------------------------------------------------

    public void runSearchBenchmarks(Random rnd) {
        for (int size : SEARCH_SIZES) {
            List<Integer> sorted = new ArrayList<>();
            for (int i = 0; i < size; i++) sorted.add(i);
            int target = rnd.nextInt(size);

            time("LinearSearch", size, () ->
                    SearchAlgorithms.linearSearch(sorted, target, Comparator.naturalOrder()));
            time("BinarySearch", size, () ->
                    SearchAlgorithms.binarySearch(sorted, target, Comparator.naturalOrder()));
        }
    }

    // -------------------------------------------------------------
    // GRAPH ALGORITHMS
    // -------------------------------------------------------------

    public void runGraphBenchmarks(Random rnd) {
        for (int size : GRAPH_SIZES) {
            Graph graph = randomConnectedGraph(size, rnd);
            List<String> ids = graph.allLocationIds();
            String start = ids.get(0);

            time("BFS", size, () -> GraphTraversal.bfs(graph, start));
            time("DFS", size, () -> GraphTraversal.dfs(graph, start));
            time("Dijkstra", size, () -> Dijkstra.shortestPaths(graph, start));

            List<Road> roads = extractRoads(graph);
            time("Kruskal", size, () -> Kruskal.buildMst(ids, roads));
            time("Prim", size, () -> Prim.buildMst(graph, start));
        }
    }

    // -------------------------------------------------------------
    // BST vs BALANCED TREE (AVL) BENCHMARKS
    // -------------------------------------------------------------

    public void runTreeBenchmarks(Random rnd) {
        // Disable AVL rotation logs during high-volume benchmarks to avoid I/O bottlenecks
        AVLTree.debugPrint = false;
        int[] treeSizes = {100, 500, 1000, 2000};
        for (int size : treeSizes) {
            // Benchmark BST Insert
            time("BST_Insert", size, () -> {
                BST<Integer, Integer> bst = new BST<>();
                for (int i = 0; i < size; i++) {
                    bst.insert(rnd.nextInt(size * 10), i);
                }
            });

            // Benchmark AVL Insert
            time("AVL_Insert", size, () -> {
                AVLTree<Integer, Integer> avl = new AVLTree<>();
                for (int i = 0; i < size; i++) {
                    avl.insert(rnd.nextInt(size * 10), i);
                }
            });

            // Benchmark BST Search
            BST<Integer, Integer> bst = new BST<>();
            AVLTree<Integer, Integer> avl = new AVLTree<>();
            List<Integer> keys = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                int key = rnd.nextInt(size * 10);
                bst.insert(key, i);
                avl.insert(key, i);
                keys.add(key);
            }

            time("BST_Search", size, () -> {
                for (int key : keys) {
                    bst.search(key);
                }
            });

            time("AVL_Search", size, () -> {
                for (int key : keys) {
                    avl.search(key);
                }
            });
        }

        // Print tree height comparison table to stdout for report trace evidence
        System.out.println("\n--- Height Comparison: BST vs Balanced Tree (AVL) ---");
        System.out.printf("%-10s %-15s %-15s %-25s%n", "Size", "BST Height", "AVL Height", "Worst-case BST (Sequential)");
        for (int size : treeSizes) {
            BST<Integer, Integer> seqBst = new BST<>();
            AVLTree<Integer, Integer> seqAvl = new AVLTree<>();
            BST<Integer, Integer> randBst = new BST<>();
            AVLTree<Integer, Integer> randAvl = new AVLTree<>();
            for (int i = 0; i < size; i++) {
                seqBst.insert(i, i);
                seqAvl.insert(i, i);
                int rKey = rnd.nextInt(size * 10);
                randBst.insert(rKey, i);
                randAvl.insert(rKey, i);
            }
            System.out.printf("%-10d %-15d %-15d %-25d%n", size, randBst.height(), randAvl.height(), seqBst.height());
        }
        System.out.println("-----------------------------------------------------");
        // Re-enable logs for unit tests or manual console runs
        AVLTree.debugPrint = true;
    }

    // -------------------------------------------------------------
    // PERSISTENCE
    // -------------------------------------------------------------

    /** Writes every recorded measurement into the algorithm_runs table. */
    public void saveToDatabase(Connection conn) throws SQLException {
        for (BenchmarkRow row : results) {
            loader.recordAlgorithmRun(conn, row.algorithmName(), row.inputSize(), row.timeNs(), 0);
        }
    }

    /** Exports every recorded measurement to a CSV for charting. Creates parent directories if needed. */
    public void exportCsv(String path) throws IOException {
        java.io.File file = new java.io.File(path);
        java.io.File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("algorithm_name,input_size,time_ns\n");
            for (BenchmarkRow row : results) {
                writer.write(row.algorithmName() + "," + row.inputSize() + "," + row.timeNs() + "\n");
            }
        }
    }

    /** Quick console summary: average time per algorithm per size. */
    public void printSummary() {
        System.out.printf("%-16s %10s %15s%n", "Algorithm", "Input Size", "Time (ns)");
        for (BenchmarkRow row : results) {
            System.out.printf("%-16s %10d %15d%n", row.algorithmName(), row.inputSize(), row.timeNs());
        }
    }

    // -------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------

    private interface TimedTask {
        void run();
    }

    private void time(String algorithmName, int inputSize, TimedTask task) {
        long start = System.nanoTime();
        task.run();
        long elapsed = System.nanoTime() - start;
        results.add(new BenchmarkRow(algorithmName, inputSize, elapsed));
    }

    private List<Integer> randomList(int size, Random rnd) {
        List<Integer> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) list.add(rnd.nextInt(size * 10));
        return list;
    }

    /** Builds a random connected graph: a spanning tree first, then extra random edges. */
    private Graph randomConnectedGraph(int nodeCount, Random rnd) {
        Graph graph = new Graph();
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < nodeCount; i++) {
            String id = "N" + i;
            ids.add(id);
            graph.addLocation(id);
        }

        int roadNum = 1;
        // Spanning tree: connect each new node to a random earlier node (guarantees connectivity).
        for (int i = 1; i < nodeCount; i++) {
            String a = ids.get(i);
            String b = ids.get(rnd.nextInt(i));
            double weight = 1 + rnd.nextDouble() * 20;
            graph.addRoad(new Road("PR" + (roadNum++), a, b, weight, weight, 1.0));
        }
        // Extra random edges (~1x node count more) for realistic graph density.
        for (int i = 0; i < nodeCount; i++) {
            String a = ids.get(rnd.nextInt(nodeCount));
            String b = ids.get(rnd.nextInt(nodeCount));
            if (!a.equals(b)) {
                double weight = 1 + rnd.nextDouble() * 20;
                graph.addRoad(new Road("PR" + (roadNum++), a, b, weight, weight, 1.0));
            }
        }
        return graph;
    }

    /** Rebuilds a road list from a Graph's edges (deduplicated by road id) for Kruskal. */
    private List<Road> extractRoads(Graph graph) {
        List<Road> roads = new ArrayList<>();
        java.util.Set<String> seen = new java.util.HashSet<>();
        for (String id : graph.allLocationIds()) {
            for (Graph.Edge edge : graph.neighborsOf(id)) {
                if (seen.add(edge.roadId())) {
                    roads.add(new Road(edge.roadId(), id, edge.to(), edge.weight(), edge.weight(), 1.0));
                }
            }
        }
        return roads;
    }
}
