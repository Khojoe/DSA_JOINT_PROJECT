package gh.dso.algorithms.search;

import java.util.Comparator;
import java.util.List;

/**
 * Linear and binary search over a List<T>.
 *
 * Each method returns a SearchResult carrying both the found index (or -1)
 * and the number of comparisons made, so trace tables and the empirical
 * performance lab (Phase 3) can report real comparison counts rather than
 * just wall-clock time.
 */
public final class SearchAlgorithms {

    private SearchAlgorithms() { }

    public record SearchResult(int index, int comparisons) { }

    /** O(n) search. Works on unsorted data. */
    public static <T> SearchResult linearSearch(List<T> data, T target, Comparator<T> comparator) {
        int comparisons = 0;
        for (int i = 0; i < data.size(); i++) {
            comparisons++;
            if (comparator.compare(data.get(i), target) == 0) {
                return new SearchResult(i, comparisons);
            }
        }
        return new SearchResult(-1, comparisons);
    }

    /**
     * O(log n) search. REQUIRES data to already be sorted ascending
     * according to the given comparator.
     */
    public static <T> SearchResult binarySearch(List<T> data, T target, Comparator<T> comparator) {
        int lo = 0;
        int hi = data.size() - 1;
        int comparisons = 0;

        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            comparisons++;
            int cmp = comparator.compare(data.get(mid), target);
            if (cmp == 0) {
                return new SearchResult(mid, comparisons);
            } else if (cmp < 0) {
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }
        return new SearchResult(-1, comparisons);
    }
}
