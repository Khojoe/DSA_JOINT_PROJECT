package gh.dso.datastructures.array;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MyDynamicArrayTest {
    @Test void addAndGet() { MyDynamicArray<Integer> a = new MyDynamicArray<>(2); a.add(10); a.add(20); assertEquals(20, a.get(1)); }
    @Test void autoResizeTrace() { MyDynamicArray<Integer> a = new MyDynamicArray<>(2); a.add(1); a.add(2); a.add(3); assertEquals(4, a.capacity()); assertEquals(1, a.resizeCount()); }
    @Test void insertShifts() { MyDynamicArray<String> a = new MyDynamicArray<>(); a.add("A"); a.add("C"); a.insert(1, "B"); assertEquals("B", a.get(1)); }
    @Test void setUpdates() { MyDynamicArray<Integer> a = new MyDynamicArray<>(); a.add(1); a.set(0, 9); assertEquals(9, a.get(0)); }
    @Test void removeShiftsAndShrinks() { MyDynamicArray<Integer> a = new MyDynamicArray<>(); a.add(1); a.add(2); assertEquals(1, a.remove(0)); assertEquals(2, a.get(0)); }
    @Test void emptyState() { assertTrue(new MyDynamicArray<>().isEmpty()); }
    @Test void invalidGet() { assertThrows(IndexOutOfBoundsException.class, () -> new MyDynamicArray<>().get(0)); }
    @Test void invalidInsert() { assertThrows(IndexOutOfBoundsException.class, () -> new MyDynamicArray<>().insert(2, 1)); }
    @Test void explicitResize() { MyDynamicArray<Integer> a = new MyDynamicArray<>(2); a.add(1); a.resize(5); assertEquals(5, a.capacity()); }
    @Test void invalidResize() { MyDynamicArray<Integer> a = new MyDynamicArray<>(); a.add(1); assertThrows(IllegalArgumentException.class, () -> a.resize(0)); }
}
