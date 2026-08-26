package gh.dso.datastructures.tree;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RedBlackTreeTest {
    @Test void insertSearch() { RedBlackTree<Integer,String> t = new RedBlackTree<>(); t.insert(10,"A"); t.insert(5,"B"); assertEquals("B", t.search(5)); }
    @Test void rootBlackAndRotations() { RedBlackTree<Integer,Integer> t = new RedBlackTree<>(); t.insert(10,10); t.insert(20,20); t.insert(30,30); assertTrue(t.isRootBlack()); assertTrue(t.rotationCount() > 0); }
    @Test void inorderSorted() { RedBlackTree<Integer,Integer> t = new RedBlackTree<>(); for(int x: new int[]{20,10,30,5,15}) t.insert(x,x); assertEquals(java.util.List.of(5,10,15,20,30), t.inorderKeys()); }
    @Test void duplicateUpdates() { RedBlackTree<Integer,String> t = new RedBlackTree<>(); t.insert(1,"A"); t.insert(1,"B"); assertEquals(1,t.size()); assertEquals("B",t.search(1)); }
    @Test void missingSearch() { assertNull(new RedBlackTree<Integer,Integer>().search(7)); }
    @Test void nullKeyRejected() { assertThrows(IllegalArgumentException.class, () -> new RedBlackTree<Integer,Integer>().insert(null,1)); }
}
