package gh.dso.datastructures.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom B-tree of degree T=3 (max keys per node = 5, max child pointers = 6).
 * Fulfills the B-Tree requirement in Section 6.
 * Logs node splits as traces.
 */
public class BTree<K extends Comparable<K>, V> {

    private static final int T = 3; // Minimum degree (defines range for number of keys)

    public static class BTreeNode<K, V> {
        public int n; // current number of keys
        public Object[] keys; // keys array
        public Object[] values; // values array
        public BTreeNode<K, V>[] children; // child pointers
        public boolean leaf; // true if leaf node

        @SuppressWarnings("unchecked")
        public BTreeNode(boolean leaf) {
            this.leaf = leaf;
            this.keys = new Object[2 * T - 1];
            this.values = new Object[2 * T - 1];
            this.children = new BTreeNode[2 * T];
            this.n = 0;
        }

        @SuppressWarnings("unchecked")
        public K getKey(int idx) {
            return (K) keys[idx];
        }

        @SuppressWarnings("unchecked")
        public V getValue(int idx) {
            return (V) values[idx];
        }
    }

    private BTreeNode<K, V> root;

    public BTree() {
        this.root = new BTreeNode<>(true);
    }

    public BTreeNode<K, V> getRoot() {
        return root;
    }

    public V search(K key) {
        return search(root, key);
    }

    private V search(BTreeNode<K, V> x, K key) {
        int i = 0;
        while (i < x.n && key.compareTo(x.getKey(i)) > 0) {
            i++;
        }
        if (i < x.n && key.compareTo(x.getKey(i)) == 0) {
            return x.getValue(i);
        }
        if (x.leaf) {
            return null;
        }
        return search(x.children[i], key);
    }

    public void insert(K key, V value) {
        BTreeNode<K, V> r = root;
        if (r.n == 2 * T - 1) {
            BTreeNode<K, V> s = new BTreeNode<>(false);
            root = s;
            s.children[0] = r;
            splitChild(s, 0, r);
            insertNonFull(s, key, value);
        } else {
            insertNonFull(r, key, value);
        }
    }

    private void insertNonFull(BTreeNode<K, V> x, K key, V value) {
        int i = x.n - 1;
        if (x.leaf) {
            while (i >= 0 && key.compareTo(x.getKey(i)) < 0) {
                x.keys[i + 1] = x.keys[i];
                x.values[i + 1] = x.values[i];
                i--;
            }
            x.keys[i + 1] = key;
            x.values[i + 1] = value;
            x.n = x.n + 1;
        } else {
            while (i >= 0 && key.compareTo(x.getKey(i)) < 0) {
                i--;
            }
            i++;
            if (x.children[i].n == 2 * T - 1) {
                splitChild(x, i, x.children[i]);
                if (key.compareTo(x.getKey(i)) > 0) {
                    i++;
                }
            }
            insertNonFull(x.children[i], key, value);
        }
    }

    private void splitChild(BTreeNode<K, V> x, int i, BTreeNode<K, V> y) {
        BTreeNode<K, V> z = new BTreeNode<>(y.leaf);
        z.n = T - 1;

        // Trace of splitting keys
        List<K> splittingKeys = new ArrayList<>();
        for (int k = 0; k < y.n; k++) {
            splittingKeys.add(y.getKey(k));
        }
        System.out.println("[TRACE] BTree: Splitting full node containing keys " + splittingKeys + " at median key: " + y.getKey(T - 1));

        // Copy keys and values to new node z
        for (int j = 0; j < T - 1; j++) {
            z.keys[j] = y.keys[j + T];
            z.values[j] = y.values[j + T];
            y.keys[j + T] = null;
            y.values[j + T] = null;
        }

        // Copy child pointers if not a leaf
        if (!y.leaf) {
            for (int j = 0; j < T; j++) {
                z.children[j] = y.children[j + T];
                y.children[j + T] = null;
            }
        }

        y.n = T - 1;

        // Shift child pointers in x to make room for z
        for (int j = x.n; j >= i + 1; j--) {
            x.children[j + 1] = x.children[j];
        }
        x.children[i + 1] = z;

        // Shift keys and values in x to make room for median key from y
        for (int j = x.n - 1; j >= i; j--) {
            x.keys[j + 1] = x.keys[j];
            x.values[j + 1] = x.values[j];
        }
        x.keys[i] = y.keys[T - 1];
        x.values[i] = y.values[T - 1];
        y.keys[T - 1] = null;
        y.values[T - 1] = null;
        x.n = x.n + 1;
    }

    public List<K> traverseKeys() {
        List<K> list = new ArrayList<>();
        traverseKeys(root, list);
        return list;
    }

    private void traverseKeys(BTreeNode<K, V> node, List<K> list) {
        int i;
        for (i = 0; i < node.n; i++) {
            if (!node.leaf) {
                traverseKeys(node.children[i], list);
            }
            list.add(node.getKey(i));
        }
        if (!node.leaf) {
            traverseKeys(node.children[i], list);
        }
    }
}
