package gh.dso.algorithms.sort;

import java.util.Comparator;
import java.util.List;

/**
 * Selection, insertion, merge, and quick sort — all implemented from
 * scratch (no Collections.sort/Arrays.sort) so the empirical performance
 * lab in Phase 3 is measuring our own algorithms, not the JDK's.
 *
 * Each method sorts the list in place (ascending, per the comparator) and
 * returns a SortStats record with comparison/swap counts for trace tables.
 */
public final class SortAlgorithms {

    private SortAlgorithms() { }

    public record SortStats(long comparisons, long swaps) { }

    // -------------------------------------------------------------
    // SELECTION SORT - O(n^2), minimal swaps
    // -------------------------------------------------------------
    public static <T> SortStats selectionSort(List<T> data, Comparator<T> comparator) {
        long comparisons = 0;
        long swaps = 0;
        int n = data.size();

        for (int i = 0; i < n - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < n; j++) {
                comparisons++;
                if (comparator.compare(data.get(j), data.get(minIndex)) < 0) {
                    minIndex = j;
                }
            }
            if (minIndex != i) {
                swap(data, i, minIndex);
                swaps++;
            }
        }
        return new SortStats(comparisons, swaps);
    }

    // -------------------------------------------------------------
    // INSERTION SORT - O(n^2), fast on nearly-sorted data
    // -------------------------------------------------------------
    public static <T> SortStats insertionSort(List<T> data, Comparator<T> comparator) {
        long comparisons = 0;
        long swaps = 0;

        for (int i = 1; i < data.size(); i++) {
            T key = data.get(i);
            int j = i - 1;
            while (j >= 0) {
                comparisons++;
                if (comparator.compare(data.get(j), key) > 0) {
                    data.set(j + 1, data.get(j));
                    swaps++;
                    j--;
                } else {
                    break;
                }
            }
            data.set(j + 1, key);
        }
        return new SortStats(comparisons, swaps);
    }

    // -------------------------------------------------------------
    // MERGE SORT - O(n log n), stable, extra memory
    // -------------------------------------------------------------
    public static <T> SortStats mergeSort(List<T> data, Comparator<T> comparator) {
        long[] counters = {0, 0}; // [comparisons, swaps/writes]
        Object[] buffer = new Object[data.size()];
        mergeSortRec(data, buffer, 0, data.size() - 1, comparator, counters);
        return new SortStats(counters[0], counters[1]);
    }

    @SuppressWarnings("unchecked")
    private static <T> void mergeSortRec(List<T> data, Object[] buffer, int lo, int hi,
                                          Comparator<T> comparator, long[] counters) {
        if (lo >= hi) return;
        int mid = lo + (hi - lo) / 2;
        mergeSortRec(data, buffer, lo, mid, comparator, counters);
        mergeSortRec(data, buffer, mid + 1, hi, comparator, counters);

        for (int k = lo; k <= hi; k++) buffer[k] = data.get(k);

        int i = lo, j = mid + 1, k = lo;
        while (i <= mid && j <= hi) {
            counters[0]++; // comparison
            if (comparator.compare((T) buffer[i], (T) buffer[j]) <= 0) {
                data.set(k++, (T) buffer[i++]);
            } else {
                data.set(k++, (T) buffer[j++]);
            }
            counters[1]++; // write
        }
        while (i <= mid) { data.set(k++, (T) buffer[i++]); counters[1]++; }
        while (j <= hi) { data.set(k++, (T) buffer[j++]); counters[1]++; }
    }

    // -------------------------------------------------------------
    // QUICK SORT - O(n log n) average, in-place, Lomuto partition
    // -------------------------------------------------------------
    public static <T> SortStats quickSort(List<T> data, Comparator<T> comparator) {
        long[] counters = {0, 0};
        quickSortRec(data, 0, data.size() - 1, comparator, counters);
        return new SortStats(counters[0], counters[1]);
    }

    private static <T> void quickSortRec(List<T> data, int lo, int hi,
                                          Comparator<T> comparator, long[] counters) {
        if (lo >= hi) return;
        int pivotIndex = partition(data, lo, hi, comparator, counters);
        quickSortRec(data, lo, pivotIndex - 1, comparator, counters);
        quickSortRec(data, pivotIndex + 1, hi, comparator, counters);
    }

    private static <T> int partition(List<T> data, int lo, int hi,
                                      Comparator<T> comparator, long[] counters) {
        T pivot = data.get(hi);
        int i = lo - 1;
        for (int j = lo; j < hi; j++) {
            counters[0]++; // comparison
            if (comparator.compare(data.get(j), pivot) <= 0) {
                i++;
                swap(data, i, j);
                counters[1]++;
            }
        }
        swap(data, i + 1, hi);
        counters[1]++;
        return i + 1;
    }

    private static <T> void swap(List<T> data, int a, int b) {
        T tmp = data.get(a);
        data.set(a, data.get(b));
        data.set(b, tmp);
    }
}
