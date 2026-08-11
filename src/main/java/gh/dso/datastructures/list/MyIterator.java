package gh.dso.datastructures.list;

/**
 * A minimal custom iterator interface (we avoid java.util.Iterator so the
 * whole traversal contract is our own, as required by the project brief).
 */
public interface MyIterator<T> {
    boolean hasNext();
    T next();
}
