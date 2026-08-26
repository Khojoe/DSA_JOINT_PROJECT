package gh.dso.datastructures.array;

import java.util.NoSuchElementException;

/** Custom generic dynamic array implemented without ArrayList. */
public class MyDynamicArray<T> {
    private Object[] elements;
    private int size;
    private int resizeCount;

    public MyDynamicArray() { this(8); }

    public MyDynamicArray(int initialCapacity) {
        if (initialCapacity < 1) throw new IllegalArgumentException("initial capacity must be positive");
        elements = new Object[initialCapacity];
    }

    public int size() { return size; }
    public int capacity() { return elements.length; }
    public int resizeCount() { return resizeCount; }
    public boolean isEmpty() { return size == 0; }

    public void add(T value) {
        ensureCapacity(size + 1);
        elements[size++] = value;
    }

    public void insert(int index, T value) {
        checkPositionIndex(index);
        ensureCapacity(size + 1);
        System.arraycopy(elements, index, elements, index + 1, size - index);
        elements[index] = value;
        size++;
    }

    @SuppressWarnings("unchecked")
    public T get(int index) {
        checkElementIndex(index);
        return (T) elements[index];
    }

    public void set(int index, T value) {
        checkElementIndex(index);
        elements[index] = value;
    }

    @SuppressWarnings("unchecked")
    public T remove(int index) {
        checkElementIndex(index);
        T old = (T) elements[index];
        int moved = size - index - 1;
        if (moved > 0) System.arraycopy(elements, index + 1, elements, index, moved);
        elements[--size] = null;
        return old;
    }

    /** Explicitly resizes the backing array. It cannot shrink below size. */
    public void resize(int newCapacity) {
        if (newCapacity < size) throw new IllegalArgumentException("capacity cannot be smaller than size");
        if (newCapacity == elements.length) return;
        Object[] replacement = new Object[newCapacity];
        System.arraycopy(elements, 0, replacement, 0, size);
        elements = replacement;
        resizeCount++;
    }

    public String resizeTrace() {
        return "size=" + size + ", capacity=" + capacity() + ", resizes=" + resizeCount;
    }

    private void ensureCapacity(int required) {
        if (required <= elements.length) return;
        int newCapacity = Math.max(required, elements.length * 2);
        resize(newCapacity);
    }

    private void checkElementIndex(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException("index=" + index + ", size=" + size);
    }

    private void checkPositionIndex(int index) {
        if (index < 0 || index > size) throw new IndexOutOfBoundsException("index=" + index + ", size=" + size);
    }
}
