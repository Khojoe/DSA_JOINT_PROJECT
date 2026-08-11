package gh.dso.graph;

import gh.dso.datastructures.deque.MyDeque;
import gh.dso.datastructures.stack.MyStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Breadth-first and depth-first traversal, reusing our own Phase 1
 * structures for the frontier: MyDeque as an unbounded FIFO queue for
 * BFS, MyStack for DFS (instead of recursion, so large graphs don't
 * risk a stack overflow).
 */
public final class GraphTraversal {

    private GraphTraversal() { }

    /** Visit order starting from startId. Unreachable nodes are omitted. */
    public static List<String> bfs(Graph graph, String startId) {
        List<String> order = new ArrayList<>();
        if (!graph.containsLocation(startId)) return order;

        Set<String> visited = new HashSet<>();
        MyDeque<String> queue = new MyDeque<>();
        queue.addRear(startId);
        visited.add(startId);

        while (!queue.isEmpty()) {
            String current = queue.removeFront();
            order.add(current);
            for (Graph.Edge edge : graph.neighborsOf(current)) {
                if (!visited.contains(edge.to())) {
                    visited.add(edge.to());
                    queue.addRear(edge.to());
                }
            }
        }
        return order;
    }

    /** Visit order starting from startId (iterative, stack-based). */
    public static List<String> dfs(Graph graph, String startId) {
        List<String> order = new ArrayList<>();
        if (!graph.containsLocation(startId)) return order;

        Set<String> visited = new HashSet<>();
        MyStack<String> stack = new MyStack<>();
        stack.push(startId);

        while (!stack.isEmpty()) {
            String current = stack.pop();
            if (visited.contains(current)) continue;
            visited.add(current);
            order.add(current);

            // Push neighbours in reverse so traversal order is left-to-right
            List<Graph.Edge> neighbors = graph.neighborsOf(current);
            for (int i = neighbors.size() - 1; i >= 0; i--) {
                String next = neighbors.get(i).to();
                if (!visited.contains(next)) {
                    stack.push(next);
                }
            }
        }
        return order;
    }

    /** True if every location is reachable from startId (graph connectivity check). */
    public static boolean isConnected(Graph graph, String startId) {
        return bfs(graph, startId).size() == graph.vertexCount();
    }
}
