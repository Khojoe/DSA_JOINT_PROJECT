package gh.dso.optimization;

import gh.dso.graph.Graph;
import gh.dso.model.Resource;
import gh.dso.model.Road;
import gh.dso.model.ServiceRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GreedyDispatcherTest {

    private ServiceRequest request(String id, String source, int urgency) {
        LocalDateTime now = LocalDateTime.of(2026, 7, 1, 8, 0);
        return new ServiceRequest(id, source, "Dest", "Food", urgency, now, now.plusHours(1), "NEW");
    }

    private Resource resource(String id, String home) {
        return new Resource(id, "Rider", home, 3, "AVAILABLE");
    }

    /**
     * Hub1 --1-- Source1 --1-- Hub2
     * Hub1 is much closer to Source1 than Hub2 is.
     */
    private Graph sampleGraph() {
        Graph g = new Graph();
        g.addRoad(new Road("R1", "Hub1", "Source1", 1, 1, 1));
        g.addRoad(new Road("R2", "Source1", "Hub2", 1, 20, 1));
        return g;
    }

    @Test
    void assign_normalCase_picksNearestResource() {
        Graph g = sampleGraph();
        List<ServiceRequest> requests = List.of(request("Q1", "Source1", 5));
        List<Resource> resources = List.of(resource("V1", "Hub2"), resource("V2", "Hub1"));

        GreedyDispatcher.DispatchResult result = GreedyDispatcher.assign(requests, resources, g);

        assertEquals(1, result.assignments().size());
        assertEquals("V2", result.assignments().get(0).resource().getResourceId()); // Hub1 is closer
        assertTrue(result.unassigned().isEmpty());
    }

    @Test
    void assign_resourceReusedAcrossRequests_notAllowed() {
        Graph g = sampleGraph();
        List<ServiceRequest> requests = List.of(
                request("Q1", "Source1", 5),
                request("Q2", "Source1", 4));
        List<Resource> resources = List.of(resource("V1", "Hub1")); // only one resource available

        GreedyDispatcher.DispatchResult result = GreedyDispatcher.assign(requests, resources, g);

        assertEquals(1, result.assignments().size()); // only one resource to go around
        assertEquals(1, result.unassigned().size());
    }

    @Test
    void assign_noResourcesAvailable_boundaryCase_allUnassigned() {
        Graph g = sampleGraph();
        List<ServiceRequest> requests = List.of(request("Q1", "Source1", 5));

        GreedyDispatcher.DispatchResult result = GreedyDispatcher.assign(requests, List.of(), g);

        assertTrue(result.assignments().isEmpty());
        assertEquals(1, result.unassigned().size());
    }

    @Test
    void assign_noRequests_boundaryCase_emptyResult() {
        Graph g = sampleGraph();
        List<Resource> resources = List.of(resource("V1", "Hub1"));

        GreedyDispatcher.DispatchResult result = GreedyDispatcher.assign(List.of(), resources, g);

        assertTrue(result.assignments().isEmpty());
        assertTrue(result.unassigned().isEmpty());
    }

    @Test
    void assign_resourceHomeNotInGraph_invalidCase_skipped() {
        Graph g = sampleGraph();
        List<ServiceRequest> requests = List.of(request("Q1", "Source1", 5));
        List<Resource> resources = List.of(resource("V1", "UnknownHub"));

        GreedyDispatcher.DispatchResult result = GreedyDispatcher.assign(requests, resources, g);

        assertTrue(result.assignments().isEmpty());
        assertEquals(1, result.unassigned().size());
    }
}
