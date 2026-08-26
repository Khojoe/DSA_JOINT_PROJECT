package gh.dso.graph;
import gh.dso.model.Road;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class GraphMatrixTest {
 @Test void storesWeightedUndirectedEdge(){GraphMatrix g=new GraphMatrix();g.addRoad(new Road("R1","A","B",2,5,1));assertEquals(5.0,g.weight("A","B"));assertEquals(5.0,g.weight("B","A"));}
 @Test void neighbors(){GraphMatrix g=new GraphMatrix();g.addRoad(new Road("R1","A","B",2,5,1));assertEquals(java.util.List.of("B"),g.neighborsOf("A"));}
 @Test void unknownReturnsInfinity(){GraphMatrix g=new GraphMatrix();g.addLocation("A");assertTrue(Double.isInfinite(g.weight("A","X")));}
}
