package gh.dso.graph;

import java.util.HashMap;
import java.util.Map;

/**
 * Union-Find / Disjoint-Set data structure with path compression and
 * union by rank. Used by Kruskal's algorithm to detect cycles when
 * building a minimum spanning tree.
 */
public class DisjointSet {
    private final Map<String, String> parent = new HashMap<>();
    private final Map<String, Integer> rank = new HashMap<>();

    public void makeSet(String id) {
        parent.putIfAbsent(id, id);
        rank.putIfAbsent(id, 0);
    }

    /** Find with path compression. */
    public String find(String id) {
        if (!parent.get(id).equals(id)) {
            parent.put(id, find(parent.get(id))); // path compression
        }
        return parent.get(id);
    }

    /** Union by rank. Returns true if a merge happened (i.e. they were in different sets). */
    public boolean union(String a, String b) {
        String rootA = find(a);
        String rootB = find(b);
        if (rootA.equals(rootB)) return false; // already connected -> would form a cycle

        int rankA = rank.get(rootA);
        int rankB = rank.get(rootB);
        if (rankA < rankB) {
            parent.put(rootA, rootB);
        } else if (rankA > rankB) {
            parent.put(rootB, rootA);
        } else {
            parent.put(rootB, rootA);
            rank.put(rootA, rankA + 1);
        }
        return true;
    }

    public boolean connected(String a, String b) {
        return find(a).equals(find(b));
    }
}
