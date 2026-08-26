package gh.dso.graph;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DisjointSetTest {

    @Test
    void union_normalCase_connectsTwoSets() {
        DisjointSet ds = new DisjointSet();
        ds.makeSet("A");
        ds.makeSet("B");

        assertFalse(ds.connected("A", "B"));
        assertTrue(ds.union("A", "B"));
        assertTrue(ds.connected("A", "B"));
    }

    @Test
    void union_alreadyConnected_invalidCase_returnsFalse() {
        DisjointSet ds = new DisjointSet();
        ds.makeSet("A");
        ds.makeSet("B");
        ds.union("A", "B");

        assertFalse(ds.union("A", "B")); // already in the same set -> would be a cycle
    }

    @Test
    void find_singleElement_boundaryCase_isOwnRoot() {
        DisjointSet ds = new DisjointSet();
        ds.makeSet("Solo");
        assertEquals("Solo", ds.find("Solo"));
    }

    @Test
    void transitiveUnion_normalCase() {
        DisjointSet ds = new DisjointSet();
        for (String id : List.of("A", "B", "C")) ds.makeSet(id);
        ds.union("A", "B");
        ds.union("B", "C");

        assertTrue(ds.connected("A", "C"));
    }
}
