package gh.dso.datastructures.map;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class MyMapTest {
 @Test void putGet(){MyMap<Integer,String> m=new MyMap<>(5);m.put(1,"A");assertEquals("A",m.get(1));}
 @Test void update(){MyMap<Integer,String> m=new MyMap<>(5);m.put(1,"A");m.put(1,"B");assertEquals("B",m.get(1));}
 @Test void remove(){MyMap<Integer,String> m=new MyMap<>(5);m.put(1,"A");assertEquals("A",m.remove(1));assertNull(m.get(1));}
 @Test void size(){MyMap<Integer,String> m=new MyMap<>(5);m.put(1,"A");m.put(2,"B");assertEquals(2,m.size());}
}
