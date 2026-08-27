package gh.dso.graph;

import gh.dso.datastructures.heap.MyPriorityQueue;
import gh.dso.model.Road;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Kruskal's algorithm: builds a minimum spanning tree (MST) over the
 * road network by repeatedly taking the cheapest edge that doesn't
 * form a cycle. Useful for planning a minimal-cost "backbone" road
 * network to keep every rider hub connected.
 */
public final class Kruskal {

    private Kruskal() { }

    public record MstResult(List<Road> edges, double totalWeight) { }

    public static MstResult buildMst(List<String> locationIds, List<Road> roads) {
        DisjointSet ds = new DisjointSet();
        for (String id : locationIds) ds.makeSet(id);

        // Defensive: ignore any road that references a location outside the
        // given set, rather than letting DisjointSet blow up on an id it was
        // never told about. This also covers the "invalid precondition" edge
        // case the brief asks for (Section 10).
        java.util.Set<String> knownIds = new java.util.HashSet<>(locationIds);

        MyPriorityQueue<Road> pq = new MyPriorityQueue<>(Comparator.comparingDouble(Road::effectiveWeight));
        for (Road road : roads) {
            if (knownIds.contains(road.getFromLocationId()) && knownIds.contains(road.getToLocationId())) {
                pq.insert(road);
            }
        }

        List<Road> mstEdges = new ArrayList<>();
        double totalWeight = 0;

        while (!pq.isEmpty() && mstEdges.size() < locationIds.size() - 1) {
            Road road = pq.extract();
            if (ds.union(road.getFromLocationId(), road.getToLocationId())) {
                mstEdges.add(road);
                totalWeight += road.effectiveWeight();
            }
        }
        return new MstResult(mstEdges, totalWeight);
    }
}