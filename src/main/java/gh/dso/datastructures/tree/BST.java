package gh.dso.datastructures.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom binary search tree, keyed by a Comparable key with an associated value.
 * Used by the indexing engine to support ordered search over, e.g., request IDs
 * or urgency-sorted lookups.
 */
public class BST<K extends Comparable<K>, V> {

    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> left;
        Node<K, V> right;
        Node(K key, V value) { this.key = key; this.value = value; }
    }

    private Node<K, V> root;
    private int size;

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    public void insert(K key, V value) {
        root = insertRec(root, key, value);
    }

    private Node<K, V> insertRec(Node<K, V> node, K key, V value) {
        if (node == null) {
            size++;
            return new Node<>(key, value);
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = insertRec(node.left, key, value);
        } else if (cmp > 0) {
            node.right = insertRec(node.right, key, value);
        } else {
            node.value = value; // update existing key
        }
        return node;
    }

    /** Returns the value for key, or null if not found. Records the search path length. */
    public V search(K key) {
        Node<K, V> current = root;
        while (current != null) {
            int cmp = key.compareTo(current.key);
            if (cmp == 0) return current.value;
            current = cmp < 0 ? current.left : current.right;
        }
        return null;
    }

    /** Search that also returns how many comparisons were made (for trace tables). */
    public int searchPathLength(K key) {
        Node<K, V> current = root;
        int steps = 0;
        while (current != null) {
            steps++;
            int cmp = key.compareTo(current.key);
            if (cmp == 0) return steps;
            current = cmp < 0 ? current.left : current.right;
        }
        return steps;
    }

    public boolean contains(K key) {
        return search(key) != null;
    }

    public List<K> inorderKeys() {
        List<K> result = new ArrayList<>();
        inorderRec(root, result);
        return result;
    }

    private void inorderRec(Node<K, V> node, List<K> result) {
        if (node == null) return;
        inorderRec(node.left, result);
        result.add(node.key);
        inorderRec(node.right, result);
    }

    public int height() {
        return heightRec(root);
    }

    private int heightRec(Node<K, V> node) {
        if (node == null) return -1;
        return 1 + Math.max(heightRec(node.left), heightRec(node.right));
    }
}
