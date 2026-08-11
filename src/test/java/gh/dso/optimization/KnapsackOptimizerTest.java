package gh.dso.optimization;

import gh.dso.model.ServiceRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KnapsackOptimizerTest {

    private ServiceRequest request(String id, String category, int urgency) {
        LocalDateTime now = LocalDateTime.of(2026, 7, 1, 8, 0);
        return new ServiceRequest(id, "L001", "L002", category, urgency, now, now.plusHours(1), "NEW");
    }

    @Test
    void solveDp_normalCase_picksOptimalCombination() {
        // weights: Document/Medical/Pharmacy=1, Food/Parcel=2, Grocery=3
        List<ServiceRequest> candidates = List.of(
                request("Q1", "Grocery", 5),  // weight 3, value 5
                request("Q2", "Document", 2), // weight 1, value 2
                request("Q3", "Food", 4)      // weight 2, value 4
        );

        var result = KnapsackOptimizer.solveDp(candidates, 3);

        // Q2 + Q3 (weight 1+2=3, value 2+4=6) beats Q1 alone (weight 3, value 5)
        assertEquals(6, result.totalValue());
        assertEquals(3, result.totalWeight());
        assertTrue(result.selected().containsAll(List.of(
                candidates.get(1), candidates.get(2))));
    }

    @Test
    void greedyVsDp_counterexample_greedyIsStrictlyWorse() {
        List<ServiceRequest> candidates = List.of(
                request("Q1", "Grocery", 5),  // weight 3, value 5 (highest single urgency)
                request("Q2", "Document", 2), // weight 1, value 2
                request("Q3", "Food", 4)      // weight 2, value 4
        );

        var greedyResult = KnapsackOptimizer.solveGreedy(candidates, 3);
        var dpResult = KnapsackOptimizer.solveDp(candidates, 3);

        // Greedy takes Q1 first (highest urgency) and then has no room left -> total 5
        assertEquals(5, greedyResult.totalValue());
        // DP correctly finds Q2+Q3 -> total 6
        assertEquals(6, dpResult.totalValue());
        assertTrue(dpResult.totalValue() > greedyResult.totalValue(),
                "DP should strictly beat greedy in this constructed case");
    }

    @Test
    void solveDp_zeroCapacity_boundaryCase_selectsNothing() {
        List<ServiceRequest> candidates = List.of(request("Q1", "Document", 3));
        var result = KnapsackOptimizer.solveDp(candidates, 0);
        assertTrue(result.selected().isEmpty());
        assertEquals(0, result.totalValue());
    }

    @Test
    void solveDp_emptyCandidateList_boundaryCase() {
        var result = KnapsackOptimizer.solveDp(List.of(), 5);
        assertTrue(result.selected().isEmpty());
        assertEquals(0, result.totalValue());
    }

    @Test
    void solveDp_capacityExceedsAllWeights_takesEverything() {
        List<ServiceRequest> candidates = List.of(
                request("Q1", "Document", 3),
                request("Q2", "Document", 2)
        );
        var result = KnapsackOptimizer.solveDp(candidates, 100);
        assertEquals(2, result.selected().size());
        assertEquals(5, result.totalValue());
    }

    @Test
    void weightOf_knownCategories_returnsExpectedWeights() {
        assertEquals(1, KnapsackOptimizer.weightOf(request("Q1", "Document", 1)));
        assertEquals(2, KnapsackOptimizer.weightOf(request("Q2", "Food", 1)));
        assertEquals(3, KnapsackOptimizer.weightOf(request("Q3", "Grocery", 1)));
    }
}
