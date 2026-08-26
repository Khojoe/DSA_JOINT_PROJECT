package gh.dso.datastructures.heap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Custom binary heap-backed priority queue (no java.util.PriorityQueue used).
 * The Comparator defines "highest priority first" ordering — for dispatch,
 * we typically compare by urgency (higher first), then arrival order (earlier first).
 */
public class MyPriorityQueue<T> {
    private final List<T> heap = new ArrayList<>();
    private final Comparator<T> comparator;

    public MyPriorityQueue(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    public int size() {
        return heap.size();
    }

    public void insert(T value) {
        heap.add(value);
        heapifyUp(heap.size() - 1);
    }

    /** Removes and returns the highest-priority element (per the comparator). */
    public T extract() {
        if (heap.isEmpty()) throw new NoSuchElementException("priority queue is empty");
        T top = heap.get(0);
        T last = heap.remove(heap.size() - 1);
        if (!heap.isEmpty()) {
            heap.set(0, last);
            heapifyDown(0);
        }
        return top;
    }

    public T peek() {
        if (heap.isEmpty()) throw new NoSuchElementException("priority queue is empty");
        return heap.get(0);
    }

    private void heapifyUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (comparator.compare(heap.get(index), heap.get(parent)) < 0) {
                swap(index, parent);
                index = parent;
            } else {
                break;
            }
        }
    }

    private void heapifyDown(int index) {
        int size = heap.size();
        while (true) {
            int left = 2 * index + 1;
            int right = 2 * index + 2;
            int smallest = index;

            if (left < size && comparator.compare(heap.get(left), heap.get(smallest)) < 0) {
                smallest = left;
            }
            if (right < size && comparator.compare(heap.get(right), heap.get(smallest)) < 0) {
                smallest = right;
            }
            if (smallest == index) break;

            swap(index, smallest);
            index = smallest;
        }
    }

    private void swap(int i, int j) {
        T tmp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, tmp);
    }
}
