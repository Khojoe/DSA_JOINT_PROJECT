package gh.dso.datastructures;

import gh.dso.datastructures.tree.AVLTree;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AVLTreeTest {

    @Test
    public void testBasicInsertAndSearch() {
        AVLTree<Integer, String> tree = new AVLTree<>();
        assertTrue(tree.isEmpty());
        assertEquals(0, tree.size());

        tree.insert(15, "Fifteen");
        tree.insert(10, "Ten");
        tree.insert(20, "Twenty");

        assertEquals(3, tree.size());
        assertFalse(tree.isEmpty());
        assertEquals("Ten", tree.search(10));
        assertEquals("Fifteen", tree.search(15));
        assertEquals("Twenty", tree.search(20));
        assertNull(tree.search(99));

        // Test update
        tree.insert(10, "New Ten");
        assertEquals("New Ten", tree.search(10));
        assertEquals(3, tree.size()); // size should not increase for updates
    }

    @Test
    public void testLLRotation() {
        // Sequentially smaller elements causes LL imbalance at root
        AVLTree<Integer, Integer> tree = new AVLTree<>();
        tree.insert(30, 30);
        tree.insert(20, 20);
        tree.insert(10, 10); // Causes Right Rotation at 30

        assertEquals(3, tree.size());
        assertEquals(2, tree.height()); // Height should balance to 2, not 3
        assertEquals(20, tree.getRoot().key); // 20 becomes new root
        assertEquals(10, tree.getRoot().left.key);
        assertEquals(30, tree.getRoot().right.key);
    }

    @Test
    public void testRRRotation() {
        // Sequentially larger elements causes RR imbalance at root
        AVLTree<Integer, Integer> tree = new AVLTree<>();
        tree.insert(10, 10);
        tree.insert(20, 20);
        tree.insert(30, 30); // Causes Left Rotation at 10

        assertEquals(3, tree.size());
        assertEquals(2, tree.height()); // Height balances to 2
        assertEquals(20, tree.getRoot().key); // 20 is new root
        assertEquals(10, tree.getRoot().left.key);
        assertEquals(30, tree.getRoot().right.key);
    }

    @Test
    public void testLRRotation() {
        AVLTree<Integer, Integer> tree = new AVLTree<>();
        tree.insert(30, 30);
        tree.insert(10, 10);
        tree.insert(20, 20); // Causes Left-Right Rotation at 30

        assertEquals(3, tree.size());
        assertEquals(2, tree.height());
        assertEquals(20, tree.getRoot().key); // 20 is new root
        assertEquals(10, tree.getRoot().left.key);
        assertEquals(30, tree.getRoot().right.key);
    }

    @Test
    public void testRLRotation() {
        AVLTree<Integer, Integer> tree = new AVLTree<>();
        tree.insert(10, 10);
        tree.insert(30, 30);
        tree.insert(20, 20); // Causes Right-Left Rotation at 10

        assertEquals(3, tree.size());
        assertEquals(2, tree.height());
        assertEquals(20, tree.getRoot().key); // 20 is new root
        assertEquals(10, tree.getRoot().left.key);
        assertEquals(30, tree.getRoot().right.key);
    }
}
