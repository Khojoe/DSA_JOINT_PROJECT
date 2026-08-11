package gh.dso.model;

/**
 * A weighted edge between two locations, used to build the delivery
 * network graph for BFS/DFS/Dijkstra/Prim/Kruskal.
 * Field names and string ID convention follow roads_template.csv.
 */
public class Road {
    private final String roadId;
    private final String fromLocationId;
    private final String toLocationId;
    private final double distanceKm;
    private final double travelTimeMin;
    private final double conditionWeight;

    public Road(String roadId, String fromLocationId, String toLocationId,
                double distanceKm, double travelTimeMin, double conditionWeight) {
        this.roadId = roadId;
        this.fromLocationId = fromLocationId;
        this.toLocationId = toLocationId;
        this.distanceKm = distanceKm;
        this.travelTimeMin = travelTimeMin;
        this.conditionWeight = conditionWeight;
    }

    public String getRoadId() { return roadId; }
    public String getFromLocationId() { return fromLocationId; }
    public String getToLocationId() { return toLocationId; }
    public double getDistanceKm() { return distanceKm; }
    public double getTravelTimeMin() { return travelTimeMin; }
    public double getConditionWeight() { return conditionWeight; }

    /** Effective edge weight for shortest-path algorithms: time scaled by road condition. */
    public double effectiveWeight() {
        return travelTimeMin * conditionWeight;
    }

    @Override
    public String toString() {
        return fromLocationId + " -> " + toLocationId + " (" + distanceKm + "km, w=" +
                String.format("%.2f", effectiveWeight()) + ")";
    }
}
