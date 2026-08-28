package gh.dso.algorithms;

import gh.dso.algorithms.search.SearchAlgorithms;
import gh.dso.algorithms.search.SearchAlgorithms.SearchResult;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SearchAlgorithmsTest {

    private final Comparator<Integer> asc = Comparator.naturalOrder();

    @Test
    void linearSearch_normalCase_findsMiddleElement() {
        List<Integer> data = List.of(5, 3, 8, 1, 9, 2);
        SearchResult result = SearchAlgorithms.linearSearch(data, 8, asc);
        assertEquals(2, result.index());
    }

    @Test
    void linearSearch_notFound_invalidCase_returnsMinusOne() {
        List<Integer> data = List.of(5, 3, 8);
        SearchResult result = SearchAlgorithms.linearSearch(data, 99, asc);
        assertEquals(-1, result.index());
        assertEquals(3, result.comparisons()); // checked every element
    }

    @Test
    void linearSearch_emptyList_boundaryCase() {
        SearchResult result = SearchAlgorithms.linearSearch(List.of(), 1, asc);
        assertEquals(-1, result.index());
        assertEquals(0, result.comparisons());
    }

    @Test
    void binarySearch_normalCase_findsElement() {
        List<Integer> sorted = List.of(1, 3, 5, 7, 9, 11, 13);
        SearchResult result = SearchAlgorithms.binarySearch(sorted, 7, asc);
        assertEquals(3, result.index());
    }

    @Test
    void binarySearch_notFound_invalidCase_returnsMinusOne() {
        List<Integer> sorted = List.of(1, 3, 5, 7, 9);
        SearchResult result = SearchAlgorithms.binarySearch(sorted, 4, asc);
        assertEquals(-1, result.index());
    }

    @Test
    void binarySearch_singleElement_boundaryCase() {
        List<Integer> sorted = List.of(42);
        assertEquals(0, SearchAlgorithms.binarySearch(sorted, 42, asc).index());
        assertEquals(-1, SearchAlgorithms.binarySearch(sorted, 1, asc).index());
    }

    @Test
    void binarySearch_comparisonCount_isLogarithmic() {
        List<Integer> sorted = new java.util.ArrayList<>();
        for (int i = 0; i < 1000; i++) sorted.add(i);
        SearchResult result = SearchAlgorithms.binarySearch(sorted, 999, asc);
        // log2(1000) ~= 10, so this should never need anywhere close to 1000 comparisons
        assertTrue(result.comparisons() <= 15);
    }

    @Test
    void binarySearch_unsortedInput_counterexample_failsToFindElement() {
        // Binary search requires input to be sorted.
        // On unsorted input, it might fail to find an element that actually exists.
        List<Integer> unsorted = List.of(29, 4, 71, 15, 8, 42, 3);

        // Element 8 is present in the list at index 4.
        // However, binary search on this unsorted array will fail.
        SearchResult result = SearchAlgorithms.binarySearch(unsorted, 8, asc);

        // Assert that it returned -1 (not found) even though 8 is present
        assertEquals(-1, result.index());
    }
}
