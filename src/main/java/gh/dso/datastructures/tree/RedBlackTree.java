package gh.dso.datastructures.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom red-black tree with insertion, search and inorder traversal.
 * The implementation uses a single black NIL sentinel to simplify rotations.
 */
public class RedBlackTree<K extends Comparable<K>, V> {
    private static final boolean RED = true;
    private static final boolean BLACK = false;

    private final class Node {
        K key; V value; boolean color; Node left, right, parent;
        Node(K key, V value, boolean color) { this.key = key; this.value = value; this.color = color; }
    }

    private final Node nil = new Node(null, null, BLACK);
    private Node root = nil;
    private int size;
    private int rotationCount;

    public RedBlackTree() { nil.left = nil.right = nil.parent = nil; }
    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }
    public int rotationCount() { return rotationCount; }
    public boolean isRootBlack() { return root == nil || root.color == BLACK; }

    public void insert(K key, V value) {
        if (key == null) throw new IllegalArgumentException("key cannot be null");
        Node parent = nil, current = root;
        while (current != nil) {
            parent = current;
            int cmp = key.compareTo(current.key);
            if (cmp == 0) { current.value = value; return; }
            current = cmp < 0 ? current.left : current.right;
        }
        Node node = new Node(key, value, RED);
        node.left = node.right = nil; node.parent = parent;
        if (parent == nil) root = node;
        else if (key.compareTo(parent.key) < 0) parent.left = node;
        else parent.right = node;
        size++;
        insertFixup(node);
    }

    public V search(K key) {
        if (key == null) return null;
        Node n = findNode(key);
        return n == nil ? null : n.value;
    }

    public boolean contains(K key) { return findNode(key) != nil; }

    public List<K> inorderKeys() {
        List<K> result = new ArrayList<>();
        inorder(root, result);
        return result;
    }

    public int height() { return height(root); }

    /** Returns a compact root/children trace useful for rotation evidence. */
    public String structureSummary() {
        if (root == nil) return "EMPTY";
        return root.key + color(root) + " left=" + childLabel(root.left) + " right=" + childLabel(root.right);
    }

    private void insertFixup(Node z) {
        while (z.parent.color == RED) {
            if (z.parent == z.parent.parent.left) {
                Node y = z.parent.parent.right;
                if (y.color == RED) {
                    z.parent.color = BLACK; y.color = BLACK; z.parent.parent.color = RED; z = z.parent.parent;
                } else {
                    if (z == z.parent.right) { z = z.parent; rotateLeft(z); }
                    z.parent.color = BLACK; z.parent.parent.color = RED; rotateRight(z.parent.parent);
                }
            } else {
                Node y = z.parent.parent.left;
                if (y.color == RED) {
                    z.parent.color = BLACK; y.color = BLACK; z.parent.parent.color = RED; z = z.parent.parent;
                } else {
                    if (z == z.parent.left) { z = z.parent; rotateRight(z); }
                    z.parent.color = BLACK; z.parent.parent.color = RED; rotateLeft(z.parent.parent);
                }
            }
        }
        root.color = BLACK;
        root.parent = nil;
    }

    private void rotateLeft(Node x) {
        Node y = x.right;
        x.right = y.left;
        if (y.left != nil) y.left.parent = x;
        y.parent = x.parent;
        if (x.parent == nil) root = y;
        else if (x == x.parent.left) x.parent.left = y;
        else x.parent.right = y;
        y.left = x; x.parent = y; rotationCount++;
    }

    private void rotateRight(Node x) {
        Node y = x.left;
        x.left = y.right;
        if (y.right != nil) y.right.parent = x;
        y.parent = x.parent;
        if (x.parent == nil) root = y;
        else if (x == x.parent.right) x.parent.right = y;
        else x.parent.left = y;
        y.right = x; x.parent = y; rotationCount++;
    }

    private Node findNode(K key) {
        if (key == null) return nil;
        Node current = root;
        while (current != nil) {
            int cmp = key.compareTo(current.key);
            if (cmp == 0) return current;
            current = cmp < 0 ? current.left : current.right;
        }
        return nil;
    }

    private void inorder(Node n, List<K> out) {
        if (n == nil) return;
        inorder(n.left, out); out.add(n.key); inorder(n.right, out);
    }

    private int height(Node n) { return n == nil ? -1 : 1 + Math.max(height(n.left), height(n.right)); }
    private String color(Node n) { return n.color == RED ? "(R)" : "(B)"; }
    private String childLabel(Node n) { return n == nil ? "NIL" : n.key + color(n); }
}
