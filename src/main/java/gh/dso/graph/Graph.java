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
}
