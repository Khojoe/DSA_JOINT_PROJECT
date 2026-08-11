package gh.dso.graph;

import gh.dso.model.Road;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Weighted graph over location IDs, built from Road rows.
 * Adjacency list representation (Map<locationId, List<Edge>>).
 *
 * Treated as undirected for routing purposes (a courier can travel a
 * road in either direction), which doubles each Road into two directed
 * adjacency entries.
 */
public class Graph {

    public record Edge(String to, double weight, String roadId) { }

    private final Map<String, List<Edge>> adjacency = new LinkedHashMap<>();

    public void addLocation(String locationId) {
        adjacency.putIfAbsent(locationId, new ArrayList<>());
    }

    public void addRoad(Road road) {
        addLocation(road.getFromLocationId());
        addLocation(road.getToLocationId());
        double weight = road.effectiveWeight();
        adjacency.get(road.getFromLocationId())
                .add(new Edge(road.getToLocationId(), weight, road.getRoadId()));
        adjacency.get(road.getToLocationId())
                .add(new Edge(road.getFromLocationId(), weight, road.getRoadId()));
    }

    public List<Edge> neighborsOf(String locationId) {
        return adjacency.getOrDefault(locationId, List.of());
    }

    public List<String> allLocationIds() {
        return new ArrayList<>(adjacency.keySet());
    }

    public int vertexCount() {
        return adjacency.size();
    }

    public boolean containsLocation(String locationId) {
        return adjacency.containsKey(locationId);
    }

    /**
     * Converts the adjacency list representation of the graph into an adjacency matrix.
     * Fulfills the "adjacency list and matrix" requirement in Section 6.
     */
    public double[][] toAdjacencyMatrix() {
        int n = vertexCount();
        double[][] matrix = new double[n][n];
        List<String> ids = allLocationIds();

        // Initialize matrix with 0 on diagonals and Infinity for disconnected vertices
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    matrix[i][j] = 0.0;
                } else {
                    matrix[i][j] = Double.POSITIVE_INFINITY;
                }
            }
        }

        // Populate weights from neighbors
        for (int i = 0; i < n; i++) {
            String u = ids.get(i);
            List<Edge> edges = neighborsOf(u);
            if (edges != null) {
                for (Edge edge : edges) {
                    int j = ids.indexOf(edge.to());
                    if (j != -1) {
                        matrix[i][j] = edge.weight();
                    }
                }
            }
        }
        return matrix;
    }
}
