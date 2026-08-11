package gh.dso.datastructures;

import gh.dso.datastructures.tree.BTree;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class BTreeTest {

    @Test
    public void testInsertAndSearch() {
        BTree<Integer, String> tree = new BTree<>();
        assertNull(tree.search(10));

        tree.insert(10, "Ten");
        tree.insert(20, "Twenty");
        tree.insert(5, "Five");

        assertEquals("Five", tree.search(5));
        assertEquals("Ten", tree.search(10));
        assertEquals("Twenty", tree.search(20));
        assertNull(tree.search(99));
    }

    @Test
    public void testSplitsAndSortedTraversal() {
        BTree<Integer, String> tree = new BTree<>();
        // Insert enough elements to trigger splits (order T=3, splits at 5 keys)
        int[] keysToInsert = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
        for (int key : keysToInsert) {
            tree.insert(key, "Val-" + key);
        }

        // Verify all keys can be searched successfully
        for (int key : keysToInsert) {
            assertEquals("Val-" + key, tree.search(key));
        }

        // Traversal of keys should be in ascending sorted order
        List<Integer> sortedKeys = tree.traverseKeys();
        assertEquals(keysToInsert.length, sortedKeys.size());
        for (int i = 0; i < sortedKeys.size() - 1; i++) {
            assertTrue(sortedKeys.get(i) < sortedKeys.get(i + 1));
        }
    }
}
