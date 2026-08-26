package gh.dso.datastructures.set;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class MySetTest {
 @Test void addAndContains(){MySet<String> s=new MySet<>(5);assertTrue(s.add("Osu"));assertTrue(s.contains("Osu"));}
 @Test void duplicateRejected(){MySet<String> s=new MySet<>(5);s.add("Osu");assertFalse(s.add("Osu"));assertEquals(1,s.size());}
 @Test void remove(){MySet<Integer> s=new MySet<>(5);s.add(1);assertTrue(s.remove(1));assertFalse(s.contains(1));}
}
