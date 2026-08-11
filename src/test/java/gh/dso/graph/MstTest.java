package gh.dso.graph;

import gh.dso.model.Road;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MstTest {

    /**
     * Classic 4-node example with a known-optimal MST weight of 6:
     *   A -1- B
     *   |  \  |
     *   4   2 3
     *   |    \|
     *   D -5- C
     * Optimal MST: A-B(1), B-C(3)... actually let's use simple values
     * where the optimal total is unambiguous.
     */
    private List<Road> sampleRoads() {
        return List.of(
                new Road("R1", "A", "B", 1, 1, 1),  // weight 1
                new Road("R2", "B", "C", 1, 2, 1),  // weight 2
                new Road("R3", "A", "C", 1, 4, 1),  // weight 4 (redundant, should be excluded)
                new Road("R4", "C", "D", 1, 3, 1),  // weight 3
                new Road("R5", "A", "D", 1, 10, 1)  // weight 10 (redundant, expensive)
        );
    }

    @Test
    void kruskal_normalCase_findsMinimumWeightSpanningTree() {
        List<String> locationIds = List.of("A", "B", "C", "D");
        Kruskal.MstResult result = Kruskal.buildMst(locationIds, sampleRoads());

        assertEquals(3, result.edges().size()); // n-1 edges for 4 nodes
        assertEquals(6.0, result.totalWeight(), 0.0001); // 1 + 2 + 3
    }

    @Test
    void prim_normalCase_findsSameMinimumWeight() {
        Graph g = new Graph();
        for (Road r : sampleRoads()) g.addRoad(r);

        Prim.MstResult result = Prim.buildMst(g, "A");

        assertEquals(3, result.edges().size());
        assertEquals(6.0, result.totalWeight(), 0.0001);
    }

    @Test
    void kruskal_singleNode_boundaryCase_noEdges() {
        Kruskal.MstResult result = Kruskal.buildMst(List.of("A"), List.of());
        assertTrue(result.edges().isEmpty());
        assertEquals(0.0, result.totalWeight(), 0.0001);
    }

    @Test
    void prim_singleNode_boundaryCase_noEdges() {
        Graph g = new Graph();
        g.addLocation("A");
        Prim.MstResult result = Prim.buildMst(g, "A");
        assertTrue(result.edges().isEmpty());
    }

    @Test
    void kruskal_disconnectedGraph_invalidCase_fewerThanNMinus1Edges() {
        List<String> locationIds = List.of("A", "B", "C", "Isolated");
        Kruskal.MstResult result = Kruskal.buildMst(locationIds, sampleRoads());
        // "Isolated" has no roads, so the MST can only ever connect A, B, C -> 2 edges
        assertTrue(result.edges().size() < locationIds.size() - 1);
    }
}
