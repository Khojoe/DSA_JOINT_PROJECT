package gh.dso.datastructures;

import gh.dso.datastructures.tree.BST;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BSTTest {

    @Test
    void insertAndSearch_normalCase() {
        BST<String, Integer> bst = new BST<>();
        bst.insert("Madina", 1);
        bst.insert("Achimota", 2);
        bst.insert("Osu", 3);

        assertEquals(1, bst.search("Madina"));
        assertEquals(2, bst.search("Achimota"));
        assertNull(bst.search("Kasoa")); // not present
    }

    @Test
    void inorderTraversal_normalCase_returnsSortedKeys() {
        BST<Integer, String> bst = new BST<>();
        int[] keys = {50, 30, 70, 20, 40, 60, 80};
        for (int k : keys) bst.insert(k, "v" + k);

        assertEquals(java.util.List.of(20, 30, 40, 50, 60, 70, 80), bst.inorderKeys());
    }

    @Test
    void duplicateKey_updatesValue_notNewNode() {
        BST<String, Integer> bst = new BST<>();
        bst.insert("Osu", 1);
        bst.insert("Osu", 99); // duplicate key case

        assertEquals(1, bst.size());
        assertEquals(99, bst.search("Osu"));
    }

    @Test
    void emptyTree_boundaryCase() {
        BST<String, Integer> bst = new BST<>();
        assertTrue(bst.isEmpty());
        assertNull(bst.search("anything"));
        assertEquals(-1, bst.height());
    }

    @Test
    void singleNode_boundaryCase() {
        BST<String, Integer> bst = new BST<>();
        bst.insert("Root", 1);
        assertEquals(0, bst.height());
        assertEquals(1, bst.size());
    }

    @Test
    void searchPathLength_traceEvidence() {
        BST<Integer, String> bst = new BST<>();
        bst.insert(50, "root");
        bst.insert(30, "left");
        bst.insert(70, "right");
        bst.insert(20, "left-left");

        assertEquals(1, bst.searchPathLength(50)); // root, 1 comparison
        assertEquals(3, bst.searchPathLength(20));  // 50 -> 30 -> 20
    }
}
