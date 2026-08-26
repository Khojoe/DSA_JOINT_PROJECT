package gh.dso.datastructures.list;

import java.util.NoSuchElementException;

/**
 * Custom doubly linked list (no java.util.LinkedList used).
 *
 * Supports addFirst, addLast, insertAfter, remove, and a custom iterator.
 * Used as the backbone for the deque and for general record storage
 * (e.g. holding loaded ServiceRequest objects before indexing).
 */
public class MyLinkedList<T> {

    private static class Node<T> {
        T value;
        Node<T> prev;
        Node<T> next;
        Node(T value) { this.value = value; }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    public void addFirst(T value) {
        Node<T> node = new Node<>(value);
        if (head == null) {
            head = tail = node;
        } else {
            node.next = head;
            head.prev = node;
            head = node;
        }
        size++;
    }

    public void addLast(T value) {
        Node<T> node = new Node<>(value);
        if (tail == null) {
            head = tail = node;
        } else {
            node.prev = tail;
            tail.next = node;
            tail = node;
        }
        size++;
    }

    /** Insert newValue immediately after the first node holding target. */
    public boolean insertAfter(T target, T newValue) {
        Node<T> current = head;
        while (current != null) {
            if (current.value.equals(target)) {
                Node<T> node = new Node<>(newValue);
                node.prev = current;
                node.next = current.next;
                if (current.next != null) {
                    current.next.prev = node;
                } else {
                    tail = node;
                }
                current.next = node;
                size++;
                return true;
            }
            current = current.next;
        }
        return false;
    }

    /** Remove the first node holding the given value. Returns true if removed. */
    public boolean remove(T value) {
        Node<T> current = head;
        while (current != null) {
            if (current.value.equals(value)) {
                unlink(current);
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public T removeFirst() {
        if (head == null) throw new NoSuchElementException("list is empty");
        T value = head.value;
        unlink(head);
        return value;
    }

    public T removeLast() {
        if (tail == null) throw new NoSuchElementException("list is empty");
        T value = tail.value;
        unlink(tail);
        return value;
    }

    public T peekFirst() {
        if (head == null) throw new NoSuchElementException("list is empty");
        return head.value;
    }

    public T peekLast() {
        if (tail == null) throw new NoSuchElementException("list is empty");
        return tail.value;
    }

    private void unlink(Node<T> node) {
        if (node.prev != null) node.prev.next = node.next; else head = node.next;
        if (node.next != null) node.next.prev = node.prev; else tail = node.prev;
        node.prev = null;
        node.next = null;
        size--;
    }

    public MyIterator<T> iterator() {
        return new MyIterator<T>() {
            private Node<T> cursor = head;

            @Override
            public boolean hasNext() {
                return cursor != null;
            }

            @Override
            public T next() {
                if (cursor == null) throw new NoSuchElementException("no more elements");
                T value = cursor.value;
                cursor = cursor.next;
                return value;
            }
        };
    }
}
