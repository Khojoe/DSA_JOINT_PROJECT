package gh.dso.datastructures.tree;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BTreeTest {
    @Test void insertAndSearch() { BTree<Integer,String> t = new BTree<>(2); t.insert(10,"A"); t.insert(20,"B"); assertEquals("B",t.search(20)); }
    @Test void splitOccurs() { BTree<Integer,Integer> t = new BTree<>(2); for(int i=1;i<=20;i++) t.insert(i,i); assertTrue(t.splitCount()>0); assertEquals(20,t.size()); }
    @Test void inorderSorted() { BTree<Integer,Integer> t = new BTree<>(2); for(int i=10;i>=1;i--) t.insert(i,i); assertEquals(java.util.List.of(1,2,3,4,5,6,7,8,9,10),t.inorderKeys()); }
    @Test void duplicateUpdates() { BTree<Integer,String> t = new BTree<>(2); t.insert(1,"A"); t.insert(1,"B"); assertEquals(1,t.size()); assertEquals("B",t.search(1)); }
    @Test void missingSearch() { assertNull(new BTree<Integer,Integer>(2).search(4)); }
    @Test void invalidDegree() { assertThrows(IllegalArgumentException.class, () -> new BTree<Integer,Integer>(1)); }
}
