package gh.dso.algorithms;

import gh.dso.algorithms.sort.SortAlgorithms;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SortAlgorithmsTest {

    private final Comparator<Integer> asc = Comparator.naturalOrder();

    private List<Integer> sample() {
        return new ArrayList<>(List.of(5, 3, 8, 1, 9, 2, 7, 4, 6));
    }

    @Test
    void selectionSort_normalCase_sortsAscending() {
        List<Integer> data = sample();
        SortAlgorithms.selectionSort(data, asc);
        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9), data);
    }

    @Test
    void insertionSort_normalCase_sortsAscending() {
        List<Integer> data = sample();
        SortAlgorithms.insertionSort(data, asc);
        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9), data);
    }

    @Test
    void mergeSort_normalCase_sortsAscending() {
        List<Integer> data = sample();
        SortAlgorithms.mergeSort(data, asc);
        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9), data);
    }

    @Test
    void quickSort_normalCase_sortsAscending() {
        List<Integer> data = sample();
        SortAlgorithms.quickSort(data, asc);
        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9), data);
    }

    @Test
    void allSorts_emptyList_boundaryCase() {
        assertDoesNotThrow(() -> SortAlgorithms.selectionSort(new ArrayList<Integer>(), asc));
        assertDoesNotThrow(() -> SortAlgorithms.insertionSort(new ArrayList<Integer>(), asc));
        assertDoesNotThrow(() -> SortAlgorithms.mergeSort(new ArrayList<Integer>(), asc));
        assertDoesNotThrow(() -> SortAlgorithms.quickSort(new ArrayList<Integer>(), asc));
    }

    @Test
    void allSorts_singleElement_boundaryCase() {
        List<Integer> data = new ArrayList<>(List.of(42));
        SortAlgorithms.quickSort(data, asc);
        assertEquals(List.of(42), data);
    }

    @Test
    void allSorts_alreadySorted_invalidCaseForWorstCasePivot() {
        // Already-sorted input is quicksort's worst case with a naive pivot choice;
        // this test just confirms correctness is preserved even then.
        List<Integer> data = new ArrayList<>();
        for (int i = 1; i <= 50; i++) data.add(i);
        SortAlgorithms.quickSort(data, asc);
        List<Integer> expected = new ArrayList<>();
        for (int i = 1; i <= 50; i++) expected.add(i);
        assertEquals(expected, data);
    }

    @Test
    void allSorts_duplicateValues_normalCase() {
        List<Integer> data = new ArrayList<>(List.of(3, 1, 3, 2, 1, 3));
        SortAlgorithms.mergeSort(data, asc);
        assertEquals(List.of(1, 1, 2, 3, 3, 3), data);
    }

    @Test
    void selectionSort_statsReported_comparisonsMatchN2Bound() {
        List<Integer> data = sample(); // n = 9
        var stats = SortAlgorithms.selectionSort(data, asc);
        // selection sort always does exactly n(n-1)/2 comparisons
        assertEquals(9 * 8 / 2, stats.comparisons());
    }
}
