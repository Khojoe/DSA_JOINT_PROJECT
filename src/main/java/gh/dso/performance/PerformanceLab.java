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

    public record BenchmarkRow(String algorithmName, int inputSize, int runNumber, long timeNs) { }
    private final DataLoader loader = new DataLoader();
    private final List<BenchmarkRow> results = new ArrayList<>();

    public List<BenchmarkRow> getResults() { return results; }
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
