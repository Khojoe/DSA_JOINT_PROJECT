package gh.dso.graph;

import gh.dso.model.Road;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GraphTraversalTest {

    /**
     * Builds:  A - B - C
     *          |       |
     *          D - - - E
     */
    private Graph sampleGraph() {
        Graph g = new Graph();
        g.addRoad(new Road("R1", "A", "B", 1, 1, 1));
        g.addRoad(new Road("R2", "B", "C", 1, 1, 1));
        g.addRoad(new Road("R3", "A", "D", 1, 1, 1));
        g.addRoad(new Road("R4", "D", "E", 1, 1, 1));
        g.addRoad(new Road("R5", "C", "E", 1, 1, 1));
        return g;
    }

    @Test
    void bfs_normalCase_visitsAllReachableNodes() {
        Graph g = sampleGraph();
        List<String> order = GraphTraversal.bfs(g, "A");
        assertEquals(5, order.size());
        assertEquals("A", order.get(0)); // starts at source
        assertTrue(order.containsAll(List.of("A", "B", "C", "D", "E")));
    }

    @Test
    void dfs_normalCase_visitsAllReachableNodes() {
        Graph g = sampleGraph();
        List<String> order = GraphTraversal.dfs(g, "A");
        assertEquals(5, order.size());
        assertEquals("A", order.get(0));
        assertTrue(order.containsAll(List.of("A", "B", "C", "D", "E")));
    }

    @Test
    void bfs_disconnectedNode_boundaryCase_returnsOnlyReachable() {
        Graph g = new Graph();
        g.addRoad(new Road("R1", "A", "B", 1, 1, 1));
        g.addLocation("Z"); // isolated, no roads

        List<String> order = GraphTraversal.bfs(g, "A");
        assertEquals(2, order.size());
        assertFalse(order.contains("Z"));
    }

    @Test
    void bfs_unknownStartNode_invalidCase_returnsEmpty() {
        Graph g = sampleGraph();
        assertTrue(GraphTraversal.bfs(g, "DOES_NOT_EXIST").isEmpty());
    }

    @Test
    void isConnected_normalCase() {
        Graph g = sampleGraph();
        assertTrue(GraphTraversal.isConnected(g, "A"));
    }

    @Test
    void isConnected_disconnectedGraph_returnsFalse() {
        Graph g = new Graph();
        g.addRoad(new Road("R1", "A", "B", 1, 1, 1));
        g.addLocation("Z");
        assertFalse(GraphTraversal.isConnected(g, "A"));
    }
}
