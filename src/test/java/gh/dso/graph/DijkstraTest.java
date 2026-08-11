package gh.dso.graph;

import gh.dso.model.Road;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DijkstraTest {

    /**
     * A --1-- B --1-- C
     * |               |
     * 5-------10------
     * (direct A-D-C via D costs 5+10=15; the A-B-C path costs only 2)
     */
    private Graph sampleGraph() {
        Graph g = new Graph();
        g.addRoad(new Road("R1", "A", "B", 1, 1, 1)); // weight 1
        g.addRoad(new Road("R2", "B", "C", 1, 1, 1)); // weight 1
        g.addRoad(new Road("R3", "A", "D", 1, 5, 1)); // weight 5
        g.addRoad(new Road("R4", "D", "C", 1, 10, 1)); // weight 10
        return g;
    }

    @Test
    void shortestPaths_normalCase_picksCheaperRoute() {
        Graph g = sampleGraph();
        Dijkstra.PathResult result = Dijkstra.shortestPaths(g, "A");

        assertEquals(2.0, result.distances().get("C"), 0.0001);
        assertEquals(List.of("A", "B", "C"), result.pathTo("C"));
    }

    @Test
    void shortestPaths_sourceToSelf_boundaryCase_isZero() {
        Graph g = sampleGraph();
        Dijkstra.PathResult result = Dijkstra.shortestPaths(g, "A");
        assertEquals(0.0, result.distances().get("A"), 0.0001);
    }

    @Test
    void shortestPaths_unreachableNode_invalidCase_notInDistances() {
        Graph g = sampleGraph();
        g.addLocation("Isolated");
        Dijkstra.PathResult result = Dijkstra.shortestPaths(g, "A");

        assertFalse(result.distances().containsKey("Isolated"));
        assertTrue(result.pathTo("Isolated").isEmpty());
    }

    @Test
    void shortestPaths_singleNodeGraph_boundaryCase() {
        Graph g = new Graph();
        g.addLocation("Solo");
        Dijkstra.PathResult result = Dijkstra.shortestPaths(g, "Solo");
        assertEquals(0.0, result.distances().get("Solo"), 0.0001);
    }
}
