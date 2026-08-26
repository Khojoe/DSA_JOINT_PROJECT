package gh.dso.datastructures.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic B-tree for comparable keys. Minimum degree t controls node capacity:
 * each non-root node has t-1..2t-1 keys and t..2t children.
 */
public class BTree<K extends Comparable<K>, V> {
    private final int minDegree;
    private final class Node {
        final List<K> keys = new ArrayList<>();
        final List<V> values = new ArrayList<>();
        final List<Node> children = new ArrayList<>();
        boolean leaf = true;
    }
    private Node root = new Node();
    private int size;
    private int splitCount;

    public BTree(int minDegree) {
        if (minDegree < 2) throw new IllegalArgumentException("minimum degree must be >= 2");
        this.minDegree = minDegree;
    }
    public int minDegree() { return minDegree; }
    public int size() { return size; }
    public int splitCount() { return splitCount; }
    public boolean isEmpty() { return size == 0; }

    public V search(K key) { return search(root, key); }
    public boolean contains(K key) { return search(key) != null; }

    public void insert(K key, V value) {
        if (key == null) throw new IllegalArgumentException("key cannot be null");
        if (search(key) != null) { update(root, key, value); return; }
        if (root.keys.size() == 2 * minDegree - 1) {
            Node newRoot = new Node(); newRoot.leaf = false; newRoot.children.add(root); root = newRoot;
            splitChild(newRoot, 0);
        }
        insertNonFull(root, key, value); size++;
    }

    public List<K> inorderKeys() {
        List<K> out = new ArrayList<>(); inorder(root, out); return out;
    }

    /** Human-readable node trace for B-tree split evidence. */
    public String structureSummary() { return summarize(root); }

    private V search(Node node, K key) {
        int i = 0;
        while (i < node.keys.size() && key.compareTo(node.keys.get(i)) > 0) i++;
        if (i < node.keys.size() && key.compareTo(node.keys.get(i)) == 0) return node.values.get(i);
        return node.leaf ? null : search(node.children.get(i), key);
    }

    private void update(Node node, K key, V value) {
        int i = 0;
        while (i < node.keys.size() && key.compareTo(node.keys.get(i)) > 0) i++;
        if (i < node.keys.size() && key.compareTo(node.keys.get(i)) == 0) { node.values.set(i, value); return; }
        if (!node.leaf) update(node.children.get(i), key, value);
    }

    private void insertNonFull(Node node, K key, V value) {
        int i = node.keys.size() - 1;
        if (node.leaf) {
            node.keys.add(null); node.values.add(null);
            while (i >= 0 && key.compareTo(node.keys.get(i)) < 0) {
                node.keys.set(i + 1, node.keys.get(i)); node.values.set(i + 1, node.values.get(i)); i--;
            }
            node.keys.set(i + 1, key); node.values.set(i + 1, value);
        } else {
            while (i >= 0 && key.compareTo(node.keys.get(i)) < 0) i--;
            i++;
            if (node.children.get(i).keys.size() == 2 * minDegree - 1) {
                splitChild(node, i);
                if (key.compareTo(node.keys.get(i)) > 0) i++;
            }
            insertNonFull(node.children.get(i), key, value);
        }
    }

    private void splitChild(Node parent, int index) {
        Node full = parent.children.get(index);
        Node right = new Node(); right.leaf = full.leaf;
        K medianKey = full.keys.get(minDegree - 1); V medianValue = full.values.get(minDegree - 1);
        for (int j = minDegree; j < full.keys.size(); j++) { right.keys.add(full.keys.get(j)); right.values.add(full.values.get(j)); }
        if (!full.leaf) for (int j = minDegree; j < full.children.size(); j++) right.children.add(full.children.get(j));
        while (full.keys.size() >= minDegree) { full.keys.remove(full.keys.size() - 1); full.values.remove(full.values.size() - 1); }
        if (!full.leaf) while (full.children.size() > minDegree) full.children.remove(full.children.size() - 1);
        parent.children.add(index + 1, right); parent.keys.add(index, medianKey); parent.values.add(index, medianValue); splitCount++;
    }

    private void inorder(Node n, List<K> out) {
        for (int i = 0; i < n.keys.size(); i++) { if (!n.leaf) inorder(n.children.get(i), out); out.add(n.keys.get(i)); }
        if (!n.leaf) inorder(n.children.get(n.keys.size()), out);
    }

    private String summarize(Node n) {
        StringBuilder sb = new StringBuilder(); sb.append(n.keys);
        if (!n.leaf) { sb.append(" -> ["); for (int i = 0; i < n.children.size(); i++) { if (i > 0) sb.append(", "); sb.append(summarize(n.children.get(i))); } sb.append("]"); }
        return sb.toString();
    }
}
