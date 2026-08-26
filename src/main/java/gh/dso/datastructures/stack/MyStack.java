package gh.dso.datastructures.stack;

import java.util.EmptyStackException;

/**
 * Custom array-backed stack (no java.util.Stack used).
 * Resizes by doubling when full, as required for the dynamic-array evidence.
 */
public class MyStack<T> {
    private Object[] data;
    private int top; // index of next free slot

    public MyStack() {
        this(8);
    }

    public MyStack(int initialCapacity) {
        data = new Object[Math.max(1, initialCapacity)];
        top = 0;
    }

    public void push(T value) {
        if (top == data.length) {
            resize(data.length * 2);
        }
        data[top++] = value;
    }

    @SuppressWarnings("unchecked")
    public T pop() {
        if (isEmpty()) throw new EmptyStackException();
        T value = (T) data[--top];
        data[top] = null; // avoid memory leak
        return value;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) throw new EmptyStackException();
        return (T) data[top - 1];
    }

    public boolean isEmpty() {
        return top == 0;
    }

    public int size() {
        return top;
    }

    public void clear() {
        for (int i = 0; i < top; i++) data[i] = null;
        top = 0;
    }

    private void resize(int newCapacity) {
        Object[] newData = new Object[newCapacity];
        System.arraycopy(data, 0, newData, 0, top);
        data = newData;
    }

    /** Current backing array capacity - exposed for resize trace tests. */
    public int capacity() {
        return data.length;
    }
}
