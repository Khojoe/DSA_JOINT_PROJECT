package gh.dso.datastructures.queue;

/**
 * Minimal FIFO queue contract, implemented by CircularQueue.
 */
public interface MyQueue<T> {
    boolean enqueue(T value);
    T dequeue();
    boolean isEmpty();
    boolean isFull();
    int size();
}
