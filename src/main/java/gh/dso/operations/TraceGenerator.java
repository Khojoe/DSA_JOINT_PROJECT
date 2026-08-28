package gh.dso.operations;

import gh.dso.datastructures.list.MyLinkedList;
import gh.dso.graph.DisjointSet;
import gh.dso.model.Road;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Automates the generation of the six required trace tables (Section 10).
 * Generates beautiful Markdown tables and exports them to results/trace_tables.md.
 */
public final class TraceGenerator {

    private TraceGenerator() { }

    public static String generateAllTraces() {
        StringBuilder sb = new StringBuilder();
        sb.append("# Generated Course Brief Trace Tables\n\n");
        sb.append("This document contains the six trace tables required by **Section 10 of the Joint DSA Semester Project Brief**.\n\n");
        sb.append("---\n\n");

        sb.append(generateBinarySearchTrace());
        sb.append(generateInsertionSortTrace());
        sb.append(generateQuickSortTrace());
        sb.append(generateDijkstraTrace());
        sb.append(generateKruskalTrace());
        sb.append(generateKnapsackDpTrace());

        return sb.toString();
    }

    public static void saveTracesToFile(String filename, String content) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            out.print(content);
        }
    }

    // -----------------------------------------------------------------
    // 1. Binary Search Trace
    // -----------------------------------------------------------------
    private static String generateBinarySearchTrace() {
        StringBuilder sb = new StringBuilder();
        sb.append("## Trace Table 1: Binary Search\n\n");
        sb.append("Target: `7` | Input array (sorted): `[1, 3, 5, 7, 9, 11, 13]`\n\n");
        sb.append("| Step | low | high | mid | value at mid | comparison | action |\n");
        sb.append("|---|---|---|---|---|---|---|\n");

        int[] data = {1, 3, 5, 7, 9, 11, 13};
        int target = 7;
        int lo = 0;
        int hi = data.length - 1;
        int step = 1;

        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int val = data[mid];
            String comp = val == target ? "equal" : (val < target ? "less than target" : "greater than target");
            String action = val == target ? "Return index " + mid : (val < target ? "Set low = " + (mid + 1) : "Set high = " + (mid - 1));

            sb.append(String.format("| %d | %d | %d | %d | %d | %s | %s |\n", step++, lo, hi, mid, val, comp, action));

            if (val == target) break;
            else if (val < target) lo = mid + 1;
            else hi = mid - 1;
        }
        sb.append("\n\n---\n\n");
        return sb.toString();
    }

    // -----------------------------------------------------------------
    // 2. Insertion Sort Trace
    // -----------------------------------------------------------------
    private static String generateInsertionSortTrace() {
        StringBuilder sb = new StringBuilder();
        sb.append("## Trace Table 2: Insertion Sort\n\n");
        sb.append("Initial Array: `[29, 4, 71, 15, 8]`\n\n");
        sb.append("| Pass | i | key | Array State After Pass | Shifts Made |\n");
        sb.append("|---|---|---|---|---|\n");

        int[] data = {29, 4, 71, 15, 8};
        sb.append(String.format("| 0 | - | - | %s | - |\n", Arrays.toString(data)));

        for (int i = 1; i < data.length; i++) {
            int key = data[i];
            int j = i - 1;
            int shifts = 0;
            while (j >= 0 && data[j] > key) {
                data[j + 1] = data[j];
                shifts++;
                j--;
            }
            data[j + 1] = key;
            sb.append(String.format("| %d | %d | %d | %s | %d |\n", i, i, key, Arrays.toString(data), shifts));
        }
        sb.append("\n\n---\n\n");
        return sb.toString();
    }

    // -----------------------------------------------------------------
    // 3. Quick Sort Trace
    // -----------------------------------------------------------------
    private static String generateQuickSortTrace() {
        StringBuilder sb = new StringBuilder();
        sb.append("## Trace Table 3: Quick Sort\n\n");
        sb.append("Initial Array: `[29, 4, 71, 15, 8]` (Lomuto Partitioning, Pivot is the last element)\n\n");
        sb.append("| Call | lo | hi | Pivot Selected | Partition Index | Swaps Made | Resulting Array Partition |\n");
        sb.append("|---|---|---|---|---|---|---|\n");

        int[] data = {29, 4, 71, 15, 8};
        List<String> logs = new ArrayList<>();
        int[] callCounter = {1};
        quickSortTraceRec(data, 0, data.length - 1, logs, callCounter);

        for (String log : logs) {
            sb.append(log);
        }
        sb.append("\n\n---\n\n");
        return sb.toString();
    }

    private static void quickSortTraceRec(int[] data, int lo, int hi, List<String> logs, int[] callCounter) {
        if (lo >= hi) return;
        int call = callCounter[0]++;
        int pivot = data[hi];
        int i = lo - 1;
        int swaps = 0;

        for (int j = lo; j < hi; j++) {
            if (data[j] <= pivot) {
                i++;
                int temp = data[i];
                data[i] = data[j];
                data[j] = temp;
                swaps++;
            }
        }
        int temp = data[i + 1];
        data[i + 1] = data[hi];
        data[hi] = temp;
        swaps++;

        int pIdx = i + 1;
        logs.add(String.format("| %d | %d | %d | %d | %d | %d | %s |\n",
                call, lo, hi, pivot, pIdx, swaps, Arrays.toString(Arrays.copyOfRange(data, lo, hi + 1))));

        quickSortTraceRec(data, lo, pIdx - 1, logs, callCounter);
        quickSortTraceRec(data, pIdx + 1, hi, logs, callCounter);
    }

    // -----------------------------------------------------------------
    // 4. Dijkstra Shortest Path Trace
    // -----------------------------------------------------------------
    private static String generateDijkstraTrace() {
        StringBuilder sb = new StringBuilder();
        sb.append("## Trace Table 4: Dijkstra's Shortest Path\n\n");
        sb.append("Graph:\n");
        sb.append("- A -> B (weight 2.0)\n");
        sb.append("- A -> C (weight 5.0)\n");
        sb.append("- B -> C (weight 1.0)\n");
        sb.append("- B -> D (weight 6.0)\n");
        sb.append("- C -> D (weight 3.0)\n\n");
        sb.append("Source: `A`\n\n");
        sb.append("| Step | Settled | Selected Node | dist[A] | dist[B] | dist[C] | dist[D] | prev[A] | prev[B] | prev[C] | prev[D] |\n");
        sb.append("|---|---|---|---|---|---|---|---|---|---|---|\n");

        Map<String, List<String[]>> graph = new HashMap<>();
        graph.put("A", Arrays.asList(new String[]{"B", "2.0"}, new String[]{"C", "5.0"}));
        graph.put("B", Arrays.asList(new String[]{"C", "1.0"}, new String[]{"D", "6.0"}));
        graph.put("C", Collections.singletonList(new String[]{"D", "3.0"}));
        graph.put("D", new ArrayList<>());

        Map<String, Double> dist = new HashMap<>(Map.of("A", 0.0, "B", Double.POSITIVE_INFINITY, "C", Double.POSITIVE_INFINITY, "D", Double.POSITIVE_INFINITY));
        Map<String, String> prev = new HashMap<>(Map.of("A", "-", "B", "-", "C", "-", "D", "-"));
        Set<String> settled = new LinkedHashSet<>();
        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingDouble(dist::get));
        pq.add("A");

        int step = 1;
        while (!pq.isEmpty()) {
            String u = pq.poll();
            if (settled.contains(u)) continue;
            settled.add(u);

            sb.append(String.format("| %d | %s | %s | %s | %s | %s | %s | %s | %s | %s | %s |\n",
                    step++,
                    settled,
                    u,
                    fmtDist(dist.get("A")), fmtDist(dist.get("B")), fmtDist(dist.get("C")), fmtDist(dist.get("D")),
                    prev.get("A"), prev.get("B"), prev.get("C"), prev.get("D")
            ));

            for (String[] edge : graph.getOrDefault(u, List.of())) {
                String v = edge[0];
                double w = Double.parseDouble(edge[1]);
                if (!settled.contains(v)) {
                    double newDist = dist.get(u) + w;
                    if (newDist < dist.get(v)) {
                        dist.put(v, newDist);
                        prev.put(v, u);
                        pq.add(v);
                    }
                }
            }
        }
        sb.append("\n\n---\n\n");
        return sb.toString();
    }

    private static String fmtDist(double d) {
        return d == Double.POSITIVE_INFINITY ? "INF" : String.format("%.1f", d);
    }

    // -----------------------------------------------------------------
    // 5. Kruskal Minimum Spanning Tree Trace
    // -----------------------------------------------------------------
    private static String generateKruskalTrace() {
        StringBuilder sb = new StringBuilder();
        sb.append("## Trace Table 5: Kruskal's Minimum Spanning Tree\n\n");
        sb.append("Graph Nodes: `A, B, C, D`\n\n");
        sb.append("Edges sorted by weight:\n");
        sb.append("1. B-C (weight 1.0)\n");
        sb.append("2. A-B (weight 2.0)\n");
        sb.append("3. C-D (weight 3.0)\n");
        sb.append("4. A-C (weight 5.0)\n");
        sb.append("5. B-D (weight 6.0)\n\n");
        sb.append("| Step | Edge Considered | Weight | Union-Find Sets / Component Mapping | Cycle Detected? | Included in MST? | Total MST Weight |\n");
        sb.append("|---|---|---|---|---|---|---|\n");

        Road[] roads = {
                new Road("R1", "B", "C", 1.0, 1.0, 1.0),
                new Road("R2", "A", "B", 2.0, 2.0, 1.0),
                new Road("R3", "C", "D", 3.0, 3.0, 1.0),
                new Road("R4", "A", "C", 5.0, 5.0, 1.0),
                new Road("R5", "B", "D", 6.0, 6.0, 1.0)
        };

        DisjointSet ds = new DisjointSet();
        for (String id : List.of("A", "B", "C", "D")) ds.makeSet(id);

        double totalWeight = 0;
        int step = 1;

        for (Road road : roads) {
            String u = road.getFromLocationId();
            String v = road.getToLocationId();
            boolean isCycle = ds.find(u).equals(ds.find(v));
            boolean unioned = false;
            if (!isCycle) {
                unioned = ds.union(u, v);
                totalWeight += road.effectiveWeight();
            }

            // Print component layout trace
            String components = String.format("{A:%s, B:%s, C:%s, D:%s}", ds.find("A"), ds.find("B"), ds.find("C"), ds.find("D"));
            sb.append(String.format("| %d | %s-%s | %.1f | %s | %s | %s | %.1f |\n",
                    step++, u, v, road.effectiveWeight(), components, isCycle ? "YES" : "NO", unioned ? "YES" : "NO", totalWeight));
        }

        sb.append("\n\n---\n\n");
        return sb.toString();
    }

    // -----------------------------------------------------------------
    // 6. DP Knapsack Tabulation Trace
    // -----------------------------------------------------------------
    private static String generateKnapsackDpTrace() {
        StringBuilder sb = new StringBuilder();
        sb.append("## Trace Table 6: Knapsack DP Tabulation\n\n");
        sb.append("Vehicle Capacity: `5` units\n");
        sb.append("Candidates:\n");
        sb.append("- Item 1: Document (weight 1, urgency 2)\n");
        sb.append("- Item 2: Food (weight 2, urgency 4)\n");
        sb.append("- Item 3: Grocery (weight 3, urgency 5)\n\n");

        int[] w = {0, 1, 2, 3}; // 1-based index weights
        int[] val = {0, 2, 4, 5}; // 1-based index urgencies
        String[] names = {"-", "Document", "Food", "Grocery"};
        int capacity = 5;
        int n = 3;

        int[][] dp = new int[n + 1][capacity + 1];
        for (int i = 1; i <= n; i++) {
            for (int c = 0; c <= capacity; c++) {
                dp[i][c] = dp[i - 1][c];
                if (w[i] <= c) {
                    dp[i][c] = Math.max(dp[i][c], dp[i - 1][c - w[i]] + val[i]);
                }
            }
        }

        sb.append("### DP State Grid (Columns represent capacities 0 to 5)\n\n");
        sb.append("| Item Index | Category | Weight | Value | c=0 | c=1 | c=2 | c=3 | c=4 | c=5 |\n");
        sb.append("|---|---|---|---|---|---|---|---|---|---|\n");
        sb.append(String.format("| 0 | - | - | - | %d | %d | %d | %d | %d | %d |\n",
                dp[0][0], dp[0][1], dp[0][2], dp[0][3], dp[0][4], dp[0][5]));

        for (int i = 1; i <= n; i++) {
            sb.append(String.format("| %d | %s | %d | %d | %d | %d | %d | %d | %d | %d |\n",
                    i, names[i], w[i], val[i], dp[i][0], dp[i][1], dp[i][2], dp[i][3], dp[i][4], dp[i][5]));
        }

        // Backtracking selection trace
        sb.append("\n### DP Backtracking Trace\n\n");
        sb.append("| Backtracking Step | Remaining Capacity | Row i | Selected? | Reasoning |\n");
        sb.append("|---|---|---|---|---|\n");

        int c = capacity;
        int step = 1;
        List<String> selected = new ArrayList<>();
        for (int i = n; i >= 1; i--) {
            boolean took = dp[i][c] != dp[i - 1][c];
            String reason = took ?
                    String.format("dp[%d][%d] (%d) != dp[%d][%d] (%d) -> item chosen", i, c, dp[i][c], i-1, c, dp[i-1][c]) :
                    String.format("dp[%d][%d] (%d) == dp[%d][%d] (%d) -> skip item", i, c, dp[i][c], i-1, c, dp[i-1][c]);

            sb.append(String.format("| %d | %d | %d (%s) | %s | %s |\n", step++, c, i, names[i], took ? "YES" : "NO", reason));
            if (took) {
                selected.add(names[i]);
                c -= w[i];
            }
        }
        sb.append("\n**Final Optimal Selection**: `").append(selected).append("` | **Total Urgency Value**: `").append(dp[n][capacity]).append("`\n");

        return sb.toString();
    }
}
