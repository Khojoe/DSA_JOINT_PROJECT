package gh.dso.datastructures.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom AVL tree (self-balancing binary search tree) satisfying the balanced-tree
 * requirement in Section 6. Supports insertions with balance-restoring rotations
 * and outputs balance trace logs.
 */
public class AVLTree<K extends Comparable<K>, V> {

    public static boolean debugPrint = true;

    public static class AVLNode<K, V> {
        public K key;
        public V value;
        public AVLNode<K, V> left;
        public AVLNode<K, V> right;
        public int height;

        public AVLNode(K key, V value) {
            this.key = key;
            this.value = value;
            this.height = 1;
        }
    }

    private AVLNode<K, V> root;
    private int size;

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public AVLNode<K, V> getRoot() {
        return root;
    }

    public int height() {
        return root == null ? 0 : root.height;
    }

    private int height(AVLNode<K, V> node) {
        return node == null ? 0 : node.height;
    }

    private int balanceFactor(AVLNode<K, V> node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    private void updateHeight(AVLNode<K, V> node) {
        if (node != null) {
            node.height = 1 + Math.max(height(node.left), height(node.right));
        }
    }

    public void insert(K key, V value) {
        root = insertRec(root, key, value);
    }

    private AVLNode<K, V> insertRec(AVLNode<K, V> node, K key, V value) {
        if (node == null) {
            size++;
            return new AVLNode<>(key, value);
        }

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = insertRec(node.left, key, value);
        } else if (cmp > 0) {
            node.right = insertRec(node.right, key, value);
        } else {
            node.value = value; // update
            return node;
        }

        updateHeight(node);

        int balance = balanceFactor(node);

        // Left Left Case
        if (balance > 1 && key.compareTo(node.left.key) < 0) {
            if (debugPrint) {
                System.out.println("[TRACE] AVLTree: LL imbalance at key '" + node.key + "'. Performing Right Rotation.");
            }
            return rightRotate(node);
        }

        // Right Right Case
        if (balance < -1 && key.compareTo(node.right.key) > 0) {
            if (debugPrint) {
                System.out.println("[TRACE] AVLTree: RR imbalance at key '" + node.key + "'. Performing Left Rotation.");
            }
            return leftRotate(node);
        }

        // Left Right Case
        if (balance > 1 && key.compareTo(node.left.key) > 0) {
            if (debugPrint) {
                System.out.println("[TRACE] AVLTree: LR imbalance at key '" + node.key + "'. Performing Left-Right Rotation.");
            }
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // Right Left Case
        if (balance < -1 && key.compareTo(node.right.key) < 0) {
            if (debugPrint) {
                System.out.println("[TRACE] AVLTree: RL imbalance at key '" + node.key + "'. Performing Right-Left Rotation.");
            }
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    private AVLNode<K, V> rightRotate(AVLNode<K, V> y) {
        AVLNode<K, V> x = y.left;
        AVLNode<K, V> T2 = x.right;

        // Perform rotation
        x.right = y;
        y.left = T2;

        // Update heights
        updateHeight(y);
        updateHeight(x);

        return x;
    }

    private AVLNode<K, V> leftRotate(AVLNode<K, V> x) {
        AVLNode<K, V> y = x.right;
        AVLNode<K, V> T2 = y.left;

        // Perform rotation
        y.left = x;
        x.right = T2;

        // Update heights
        updateHeight(x);
        updateHeight(y);

        return y;
    }

    public V search(K key) {
        AVLNode<K, V> current = root;
        while (current != null) {
            int cmp = key.compareTo(current.key);
            if (cmp == 0) return current.value;
            current = cmp < 0 ? current.left : current.right;
        }
        return null;
    }

    public boolean contains(K key) {
        return search(key) != null;
    }

    public List<K> inorderKeys() {
        List<K> result = new ArrayList<>();
        inorderRec(root, result);
        return result;
    }

    private void inorderRec(AVLNode<K, V> node, List<K> result) {
        if (node == null) return;
        inorderRec(node.left, result);
        result.add(node.key);
        inorderRec(node.right, result);
    }
}
