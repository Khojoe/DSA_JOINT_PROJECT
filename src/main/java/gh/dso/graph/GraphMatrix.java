package gh.dso.graph;

import gh.dso.model.Road;
import java.util.ArrayList;
import java.util.List;

/** Custom adjacency-matrix representation of the same weighted road network. */
public class GraphMatrix {
    private final List<String> ids = new ArrayList<>();
    private double[][] weights;
    private static final double INF = Double.POSITIVE_INFINITY;

    public GraphMatrix() { weights = new double[0][0]; }
    public int vertexCount() { return ids.size(); }
    public List<String> allLocationIds() { return new ArrayList<>(ids); }
    public void addLocation(String id) {
        if (ids.contains(id)) return;
        int old = ids.size(); ids.add(id);
        double[][] next = new double[old + 1][old + 1];
        for (int i = 0; i <= old; i++) for (int j = 0; j <= old; j++) next[i][j] = i == j ? 0 : INF;
        for (int i = 0; i < old; i++) System.arraycopy(weights[i], 0, next[i], 0, old);
        weights = next;
    }
    public void addRoad(Road road) {
        addLocation(road.getFromLocationId()); addLocation(road.getToLocationId());
        int a = ids.indexOf(road.getFromLocationId()), b = ids.indexOf(road.getToLocationId());
        double w = road.effectiveWeight(); weights[a][b] = Math.min(weights[a][b], w); weights[b][a] = Math.min(weights[b][a], w);
    }
    public double weight(String from, String to) {
        int a = ids.indexOf(from), b = ids.indexOf(to);
        if (a < 0 || b < 0) return INF;
        return weights[a][b];
    }
    public List<String> neighborsOf(String id) {
        int row = ids.indexOf(id); List<String> out = new ArrayList<>(); if (row < 0) return out;
        for (int j = 0; j < ids.size(); j++) if (j != row && weights[row][j] != INF) out.add(ids.get(j));
        return out;
    }
    public String matrixTrace() {
        StringBuilder sb = new StringBuilder("Adjacency Matrix\n");
        for (String id : ids) sb.append(id).append('\t'); sb.append('\n');
        for (int i = 0; i < ids.size(); i++) { sb.append(ids.get(i)).append('\t'); for (int j = 0; j < ids.size(); j++) sb.append(weights[i][j] == INF ? "INF" : String.format("%.2f", weights[i][j])).append('\t'); sb.append('\n'); }
        return sb.toString();
    }
}
