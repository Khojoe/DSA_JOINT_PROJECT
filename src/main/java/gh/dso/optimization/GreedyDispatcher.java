package gh.dso.optimization;

import gh.dso.graph.Dijkstra;
import gh.dso.graph.Graph;
import gh.dso.model.Resource;
import gh.dso.model.ServiceRequest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Greedy nearest-rider assignment: process requests highest-urgency-first,
 * and for each one assign whichever AVAILABLE resource has the shortest
 * real road-network distance (via Dijkstra) from its home hub to the
 * request's source location. Once assigned, that resource is removed
 * from the pool for the rest of this batch.
 *
 * Greedy is fast (one pass) and locally optimal at each step, but it is
 * NOT guaranteed to minimise total distance across the whole batch —
 * see GreedyCounterexampleTest for a concrete case where a different
 * assignment would have done better overall. That's the trade-off this
 * class exists to demonstrate.
 */
public final class GreedyDispatcher {

    private GreedyDispatcher() { }

    public record Assignment(ServiceRequest request, Resource resource, double distance) { }

    public record DispatchResult(List<Assignment> assignments, List<ServiceRequest> unassigned) { }

    /**
     * requests should already be sorted highest-urgency-first (see DispatchScheduler).
     * graph must contain the road network built from Road rows.
     */
    public static DispatchResult assign(List<ServiceRequest> requests, List<Resource> availableResources,
                                         Graph graph) {
        List<Assignment> assignments = new ArrayList<>();
        List<ServiceRequest> unassigned = new ArrayList<>();
        Set<String> usedResourceIds = new HashSet<>();

        for (ServiceRequest request : requests) {
            Resource best = null;
            double bestDistance = Double.POSITIVE_INFINITY;

            for (Resource resource : availableResources) {
                if (usedResourceIds.contains(resource.getResourceId())) continue;
                if (!graph.containsLocation(resource.getHomeLocationId())) continue;

                Dijkstra.PathResult paths = Dijkstra.shortestPaths(graph, resource.getHomeLocationId());
                Double distance = paths.distances().get(request.getSourceLocationId());
                if (distance != null && distance < bestDistance) {
                    bestDistance = distance;
                    best = resource;
                }
            }

            if (best != null) {
                assignments.add(new Assignment(request, best, bestDistance));
                usedResourceIds.add(best.getResourceId());
            } else {
                unassigned.add(request);
            }
        }

        return new DispatchResult(assignments, unassigned);
    }
}
