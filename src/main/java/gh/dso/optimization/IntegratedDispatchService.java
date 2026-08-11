package gh.dso.optimization;

import gh.dso.datastructures.list.MyIterator;
import gh.dso.datastructures.list.MyLinkedList;
import gh.dso.db.DataLoader;
import gh.dso.graph.Graph;
import gh.dso.model.Resource;
import gh.dso.model.Road;
import gh.dso.model.ServiceRequest;
import gh.dso.scheduling.DispatchScheduler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * This is where Phase 1 (data + structures), Phase 2 (routing + scheduling
 * algorithms), and Phase 3 (greedy optimisation) actually operate together
 * as one system, instead of as separate demos:
 *
 *   1. Pull pending (NEW) requests and AVAILABLE resources from MySQL
 *      (Phase 1: DataLoader + custom structures)
 *   2. Sort requests by urgency (Phase 2: DispatchScheduler)
 *   3. Build the real Accra road network graph (Phase 2: Graph)
 *   4. Assign each request to the nearest available rider using real
 *      shortest-path routing (Phase 2: Dijkstra, via Phase 3: GreedyDispatcher)
 *   5. Write the outcome back to the database and log an audit trail
 *      (Phase 1: DataLoader write-back)
 */
public class IntegratedDispatchService {

    private final DataLoader loader = new DataLoader();
    private final DispatchScheduler scheduler = new DispatchScheduler();

    public record DispatchSummary(int assignedCount, int unassignedCount, double totalDistance) { }

    // -------------------------------------------------------------
    // AUTO MODE: dispatch every pending request in one pass
    // -------------------------------------------------------------
    public DispatchSummary runAuto(Connection conn) throws SQLException {
        List<ServiceRequest> pending = loadPendingRequests(conn);
        List<Resource> available = loadAvailableResources(conn);
        Graph graph = buildGraph(conn);

        List<ServiceRequest> urgencyOrdered = scheduler.dispatchInUrgencyOrder(pending);
        GreedyDispatcher.DispatchResult result = GreedyDispatcher.assign(urgencyOrdered, available, graph);

        double totalDistance = 0;
        for (GreedyDispatcher.Assignment assignment : result.assignments()) {
            applyAssignment(conn, assignment);
            totalDistance += assignment.distance();
            System.out.printf("  Assigned %s -> %s (%s), distance=%.2f%n",
                    assignment.request().getRequestId(), assignment.resource().getResourceId(),
                    assignment.resource().getResourceType(), assignment.distance());
        }
        for (ServiceRequest unassigned : result.unassigned()) {
            System.out.println("  Could NOT assign " + unassigned.getRequestId() + " (no reachable rider available)");
        }

        return new DispatchSummary(result.assignments().size(), result.unassigned().size(), totalDistance);
    }

    // -------------------------------------------------------------
    // INTERACTIVE MODE: step through one request at a time
    // -------------------------------------------------------------
    public void runInteractive(Connection conn, Scanner scanner) throws SQLException {
        List<ServiceRequest> pending = scheduler.dispatchInUrgencyOrder(loadPendingRequests(conn));
        List<Resource> available = loadAvailableResources(conn);
        Graph graph = buildGraph(conn);

        if (pending.isEmpty()) {
            System.out.println("No pending (NEW) requests to dispatch.");
            return;
        }

        for (ServiceRequest request : pending) {
            GreedyDispatcher.DispatchResult suggestion =
                    GreedyDispatcher.assign(List.of(request), available, graph);

            if (suggestion.assignments().isEmpty()) {
                System.out.println("\n" + request + " -> no available rider can reach this location. Skipping.");
                continue;
            }

            GreedyDispatcher.Assignment best = suggestion.assignments().get(0);
            System.out.printf("%n%s%nSuggested: %s (%s) at distance %.2f%n",
                    request, best.resource().getResourceId(), best.resource().getResourceType(), best.distance());
            System.out.print("Confirm assignment? (y/n/q to quit): ");
            String answer = scanner.nextLine().trim().toLowerCase();

            if (answer.equals("q")) {
                System.out.println("Stopping interactive dispatch.");
                return;
            }
            if (answer.equals("y")) {
                applyAssignment(conn, best);
                available.removeIf(r -> r.getResourceId().equals(best.resource().getResourceId()));
                System.out.println("  Confirmed.");
            } else {
                System.out.println("  Skipped.");
            }
        }
        System.out.println("\nInteractive dispatch complete.");
    }

    // -------------------------------------------------------------
    // Shared helpers
    // -------------------------------------------------------------

    private void applyAssignment(Connection conn, GreedyDispatcher.Assignment assignment) throws SQLException {
        String requestId = assignment.request().getRequestId();
        String resourceId = assignment.resource().getResourceId();

        loader.updateServiceRequestStatus(conn, requestId, "ASSIGNED");
        loader.updateResourceStatus(conn, resourceId, "BUSY");
        loader.logAuditEvent(conn, requestId, "ASSIGNMENT", "NEW",
                "ASSIGNED to " + resourceId + " (dist=" + String.format("%.2f", assignment.distance()) + ")");
    }

    private List<ServiceRequest> loadPendingRequests(Connection conn) throws SQLException {
        return toList(loader.loadServiceRequests(conn)).stream()
                .filter(r -> "NEW".equals(r.getStatus()))
                .collect(Collectors.toList());
    }

    private List<Resource> loadAvailableResources(Connection conn) throws SQLException {
        return toList(loader.loadResources(conn)).stream()
                .filter(r -> "AVAILABLE".equals(r.getAvailabilityStatus()))
                .collect(Collectors.toList());
    }

    private Graph buildGraph(Connection conn) throws SQLException {
        List<Road> roads = toList(loader.loadRoads(conn));
        Graph graph = new Graph();
        for (Road road : roads) graph.addRoad(road);
        return graph;
    }

    private static <T> List<T> toList(MyLinkedList<T> source) {
        List<T> list = new ArrayList<>();
        MyIterator<T> it = source.iterator();
        while (it.hasNext()) list.add(it.next());
        return list;
    }
}
