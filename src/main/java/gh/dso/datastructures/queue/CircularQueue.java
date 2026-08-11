package gh.dso.datastructures.queue;

import java.util.ArrayList;
import java.util.List;

/**
 * Fixed-capacity circular queue (array-backed, wraps using modulo).
 * Models a rider hub's waiting line of a fixed number of dispatch slots.
 */
public class CircularQueue<T> implements MyQueue<T> {
    private final Object[] data;
    private int front = 0;
    private int rear = 0;   // points to next insertion slot
    private int size = 0;

    public CircularQueue(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("capacity must be positive");
        data = new Object[capacity];
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean isFull() {
        return size == data.length;
    }

    @Override
    public boolean enqueue(T value) {
        if (isFull()) return false;
        data[rear] = value;
        rear = (rear + 1) % data.length;
        size++;
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T dequeue() {
        if (isEmpty()) return null;
        T removed = (T) data[front];
        data[front] = null;
        front = (front + 1) % data.length;
        size--;
        return removed;
    }

    @Override
    public int size() {
        return size;
    }

    public int capacity() {
        return data.length;
    }

    /** Front-to-rear snapshot, useful for trace tables. */
    @SuppressWarnings("unchecked")
    public List<T> snapshotInQueueOrder() {
        List<T> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add((T) data[(front + i) % data.length]);
        }
        return result;
    }
}
