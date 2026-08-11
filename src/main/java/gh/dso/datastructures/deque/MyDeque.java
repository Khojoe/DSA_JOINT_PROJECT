package gh.dso.datastructures.deque;

import gh.dso.datastructures.list.MyIterator;
import gh.dso.datastructures.list.MyLinkedList;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom double-ended queue built on MyLinkedList (no java.util.ArrayDeque used).
 * Models urgent-request insertion: normal jobs join the rear, a rider with an
 * urgent/perishable order can be pushed to the front of the dispatch line.
 */
public class MyDeque<T> {
    private final MyLinkedList<T> backing = new MyLinkedList<>();

    public void addFront(T value) {
        backing.addFirst(value);
    }

    public void addRear(T value) {
        backing.addLast(value);
    }

    public T removeFront() {
        if (backing.isEmpty()) return null;
        return backing.removeFirst();
    }

    public T removeRear() {
        if (backing.isEmpty()) return null;
        return backing.removeLast();
    }

    public boolean isEmpty() {
        return backing.isEmpty();
    }

    public int size() {
        return backing.size();
    }

    public List<T> snapshot() {
        List<T> result = new ArrayList<>();
        MyIterator<T> it = backing.iterator();
        while (it.hasNext()) {
            result.add(it.next());
        }
        return result;
    }
}
