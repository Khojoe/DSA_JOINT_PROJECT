package gh.dso.graph;

import gh.dso.datastructures.heap.MyPriorityQueue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Prim's algorithm: builds a minimum spanning tree by growing outward
 * from a single starting location, always adding the cheapest edge that
 * connects the tree to a new location. Same end goal as Kruskal (a
 * minimal-cost connected road backbone), different growth strategy —
 * having both is useful evidence for comparing algorithm design choices.
 */
public final class Prim {

    private Prim() { }

    public record MstEdge(String from, String to, double weight, String roadId) { }
    public record MstResult(List<MstEdge> edges, double totalWeight) { }

    public static MstResult buildMst(Graph graph, String startId) {
        Set<String> inTree = new HashSet<>();
        List<MstEdge> mstEdges = new ArrayList<>();
        double totalWeight = 0;

        MyPriorityQueue<MstEdge> frontier =
                new MyPriorityQueue<>(Comparator.comparingDouble(MstEdge::weight));

        inTree.add(startId);
        addFrontierEdges(graph, startId, inTree, frontier);

        while (!frontier.isEmpty() && inTree.size() < graph.vertexCount()) {
            MstEdge cheapest = frontier.extract();
            if (inTree.contains(cheapest.to())) continue; // would form a cycle

            inTree.add(cheapest.to());
            mstEdges.add(cheapest);
            totalWeight += cheapest.weight();
            addFrontierEdges(graph, cheapest.to(), inTree, frontier);
        }
        return new MstResult(mstEdges, totalWeight);
    }

    private static void addFrontierEdges(Graph graph, String from, Set<String> inTree,
                                          MyPriorityQueue<MstEdge> frontier) {
        for (Graph.Edge edge : graph.neighborsOf(from)) {
            if (!inTree.contains(edge.to())) {
                frontier.insert(new MstEdge(from, edge.to(), edge.weight(), edge.roadId()));
            }
        }
    }
}
