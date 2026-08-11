package gh.dso.optimization;

import gh.dso.model.ServiceRequest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * When a vehicle has limited capacity but more pending requests want to
 * ride along than can fit, which subset should it take to maximise total
 * urgency served? This is a classic 0/1 knapsack: each request has a
 * "weight" (parcel space it takes up) and a "value" (its urgency), and
 * capacity is the vehicle's parcel limit.
 *
 * Weight is derived from category — a reasonable proxy for real parcel
 * bulk (documents are small, groceries are bulky).
 */
public final class KnapsackOptimizer {

    private KnapsackOptimizer() { }

    private static final Map<String, Integer> CATEGORY_WEIGHT = Map.of(
            "Document", 1,
            "Medical", 1,
            "Pharmacy", 1,
            "Food", 2,
            "Parcel", 2,
            "Grocery", 3
    );

    public static int weightOf(ServiceRequest request) {
        return CATEGORY_WEIGHT.getOrDefault(request.getCategory(), 2);
    }

    public record KnapsackResult(List<ServiceRequest> selected, int totalValue, int totalWeight) { }

    /**
     * Optimal (DP) selection: guarantees the maximum total urgency achievable
     * within the given capacity. O(n * capacity) time and space.
     */
    public static KnapsackResult solveDp(List<ServiceRequest> candidates, int capacity) {
        int n = candidates.size();
        int[][] dp = new int[n + 1][capacity + 1];

        for (int i = 1; i <= n; i++) {
            ServiceRequest request = candidates.get(i - 1);
            int weight = weightOf(request);
            int value = request.getUrgency();
            for (int c = 0; c <= capacity; c++) {
                dp[i][c] = dp[i - 1][c]; // don't take request i
                if (weight <= c) {
                    dp[i][c] = Math.max(dp[i][c], dp[i - 1][c - weight] + value);
                }
            }
        }

        // Backtrack to find which requests were actually selected.
        List<ServiceRequest> selected = new ArrayList<>();
        int remaining = capacity;
        for (int i = n; i >= 1; i--) {
            if (dp[i][remaining] != dp[i - 1][remaining]) {
                ServiceRequest request = candidates.get(i - 1);
                selected.add(request);
                remaining -= weightOf(request);
            }
        }

        int totalWeight = selected.stream().mapToInt(KnapsackOptimizer::weightOf).sum();
        return new KnapsackResult(selected, dp[n][capacity], totalWeight);
    }

    /**
     * Greedy selection: take requests in descending urgency order, skipping
     * anything that would overflow capacity. Fast (O(n log n)) but — unlike
     * solveDp — NOT guaranteed optimal. See GreedyVsDpTest for a concrete
     * case where this picks a worse total than solveDp.
     */
    public static KnapsackResult solveGreedy(List<ServiceRequest> candidates, int capacity) {
        List<ServiceRequest> sorted = candidates.stream()
                .sorted(Comparator.comparingInt(ServiceRequest::getUrgency).reversed())
                .toList();

        List<ServiceRequest> selected = new ArrayList<>();
        int remaining = capacity;
        int totalValue = 0;

        for (ServiceRequest request : sorted) {
            int weight = weightOf(request);
            if (weight <= remaining) {
                selected.add(request);
                remaining -= weight;
                totalValue += request.getUrgency();
            }
        }

        return new KnapsackResult(selected, totalValue, capacity - remaining);
    }
}
