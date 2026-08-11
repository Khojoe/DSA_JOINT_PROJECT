package gh.dso.graph;

import gh.dso.datastructures.heap.MyPriorityQueue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Dijkstra's shortest-path algorithm, using our own MyPriorityQueue
 * (binary heap) as the frontier instead of java.util.PriorityQueue.
 *
 * Finds the minimum-weight path from a source location to every other
 * reachable location — this is the routing core for "fastest way to get
 * this delivery from vendor to customer".
 */
public final class Dijkstra {

    private Dijkstra() { }

    public record PathResult(Map<String, Double> distances, Map<String, String> previous) {

        /** Reconstructs the path from source to target as a list of location IDs. */
        public List<String> pathTo(String target) {
            List<String> path = new ArrayList<>();
            String current = target;
            while (current != null) {
                path.add(0, current);
                current = previous.get(current);
            }
            // If the path doesn't actually start at a node with distance 0, it's unreachable.
            if (path.isEmpty() || !distances.containsKey(target)) return List.of();
            return path;
        }
    }

    private record Candidate(String locationId, double distance) { }

    public static PathResult shortestPaths(Graph graph, String sourceId) {
        Map<String, Double> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        Set<String> settled = new HashSet<>();

        MyPriorityQueue<Candidate> frontier =
                new MyPriorityQueue<>(Comparator.comparingDouble(Candidate::distance));

        distances.put(sourceId, 0.0);
        frontier.insert(new Candidate(sourceId, 0.0));

        while (!frontier.isEmpty()) {
            Candidate current = frontier.extract();
            if (settled.contains(current.locationId())) continue;
            settled.add(current.locationId());

            for (Graph.Edge edge : graph.neighborsOf(current.locationId())) {
                if (settled.contains(edge.to())) continue;
                double newDist = current.distance() + edge.weight();
                Double known = distances.get(edge.to());
                if (known == null || newDist < known) {
                    distances.put(edge.to(), newDist);
                    previous.put(edge.to(), current.locationId());
                    frontier.insert(new Candidate(edge.to(), newDist));
                }
            }
        }
        return new PathResult(distances, previous);
    }
}
