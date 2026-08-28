# Mathematical Proof Sketches - Ghana Smart Service Operations Optimizer

This document provides formal proof sketches for key algorithms implemented in the project. These sketches address the correctness and efficiency requirements in Section 10 and Section 7 of the course brief.

---

## 1. Loop Invariant for Insertion Sort

### Algorithm Context
We analyze the Insertion Sort implementation in [`SortAlgorithms.insertionSort`](file:///k:/3008/DSA_JOINT/New%20folder/ghana-courier-dso/src/main/java/gh/dso/algorithms/sort/SortAlgorithms.java#L47-L67):

```java
for (int i = 1; i < data.size(); i++) {
    T key = data.get(i);
    int j = i - 1;
    while (j >= 0 && comparator.compare(data.get(j), key) > 0) {
        data.set(j + 1, data.get(j));
        j--;
    }
    data.set(j + 1, key);
}
```

### Loop Invariant Statement
At the start of each iteration of the outer `for` loop (with index `i`), the subarray `data[0...i-1]` consists of the elements originally in `data[0...i-1]`, but in sorted order.

### Proof by Induction

#### 1. Initialization
Before the first loop iteration, `i = 1`. The subarray is `data[0...0]`, which consists of a single element (the element originally at index 0). A single-element array is trivially sorted. Thus, the invariant holds before the first iteration.

#### 2. Maintenance
Assume the loop invariant holds at the start of iteration `i`. So `data[0...i-1]` is sorted.
During iteration `i`:
- We store `data[i]` in the variable `key`.
- The inner `while` loop shifts elements in the sorted subarray `data[0...i-1]` that are strictly greater than `key` one position to the right.
- This shifting opens a vacancy at index `j + 1` where all elements to its left are $\le$ `key` (or it is the start of the array), and all elements to its right are $>$ `key`.
- We insert `key` into this vacancy.
- The subarray `data[0...i]` now contains all original elements of `data[0...i]` in sorted order.
- When the outer loop increments `i` to `i + 1` for the next iteration, the sorted subarray is now `data[0...i]`, which satisfies the invariant.

#### 3. Termination
The outer loop terminates when `i = n` (where `n = data.size()`).
Substituting `i = n` into the loop invariant statement, we conclude that the subarray `data[0...n-1]` consists of the elements originally in `data[0...n-1]`, but in sorted order. Since `data[0...n-1]` is the entire array, the entire array is sorted. $\blacksquare$

---

## 2. Recursive Divide-and-Conquer Recurrence (Merge Sort)

### Recurrence Formulation
Merge Sort recursively splits the array of size $n$ into two halves of size $n/2$, sorts them, and then merges them in linear time. The time complexity recurrence relation is:
$$T(n) = 2T(n/2) + \Theta(n)$$
With base case $T(1) = \Theta(1)$.

### Master Theorem Application
The Master Theorem provides a recipe for recurrences of the form $T(n) = aT(n/b) + f(n)$.
Here:
- $a = 2$ (number of subproblems)
- $b = 2$ (factor by which subproblem size is reduced)
- $f(n) = \Theta(n)$ (work done outside recursive calls, i.e., merging)

We compute the critical exponent value:
$$\log_b a = \log_2 2 = 1$$
We compare $f(n)$ with $n^{\log_b a} = n^1 = n$.
Since $f(n) = \Theta(n) = \Theta(n^{\log_b a})$, we are in **Case 2** of the Master Theorem.

Thus, the recurrence has the solution:
$$T(n) = \Theta(n^{\log_b a} \log n) = \Theta(n \log n)$$

### Structural Induction Correctness Sketch
We prove that `mergeSort(data, lo, hi)` correctly sorts the range `[lo, hi]` by induction on the subproblem size $n = hi - lo + 1$.

- **Base Case ($n = 1$)**: If `lo >= hi`, the range has at most 1 element. The method returns immediately without changing anything, which is correct since a single element is already sorted.
- **Inductive Step**: Assume `mergeSort` correctly sorts ranges of size $k < n$.
  - For range `[lo, hi]` of size $n$, the midpoint is $mid = \lfloor(lo+hi)/2\rfloor$.
  - The subproblem sizes are $\lfloor n/2 \rfloor$ and $\lceil n/2 \rceil$. Since $n \ge 2$, both sizes are strictly less than $n$.
  - By the induction hypothesis, `mergeSortRec(data, buffer, lo, mid, ...)` and `mergeSortRec(data, buffer, mid + 1, hi, ...)` correctly sort the two halves.
  - The merging step reads sorted halves `data[lo...mid]` and `data[mid+1...hi]` and copies their elements into `buffer`. It then uses two pointers to compare the smallest remaining elements and write them back to `data` in ascending order.
  - Since both halves were sorted and the merge picks the minimum of the active elements, the resulting merged range `data[lo...hi]` is sorted. $\blacksquare$

---

## 3. Dijkstra's Correctness (Greedy Choice Property)

### Theorem
For any node $u \in V$, when $u$ is extracted from the priority queue and added to the settled set $S$, the distance label $d[u]$ is equal to the shortest path weight $\delta(s, u)$ from the source $s$ to $u$.

### Proof Sketch by Contradiction
Suppose there is a node for which this claim is false. Let $u$ be the **first** node added to the settled set $S$ such that $d[u] > \delta(s, u)$.

1. Since the source $s$ is the first node added to $S$, and $d[s] = 0 = \delta(s, s)$, we know $u \neq s$. This implies $S$ is non-empty before $u$ is added.
2. There must be a true shortest path $P$ from $s$ to $u$. Let's trace $P$ from the source $s$ to $u$.
3. Since $s \in S$ and $u \notin S$ (just before insertion), there must be a first edge $(x, y)$ along $P$ such that $x \in S$ and $y \notin S$.
4. Since $x \in S$ and $u$ is the *first* node for which the shortest distance was incorrect when settled, we must have:
   $$d[x] = \delta(s, x)$$
5. When $x$ was settled, the edge $(x, y)$ was relaxed. Since $y$ is on the shortest path $P$ and edge weights are non-negative ($w(x, y) \ge 0$), we have:
   $$d[y] = d[x] + w(x, y) = \delta(s, x) + w(x, y) = \delta(s, y)$$
6. Since $y$ appears before $u$ along the shortest path $P$, and all edge weights are non-negative:
   $$\delta(s, y) \le \delta(s, u)$$
7. Combining the equations:
   $$d[y] = \delta(s, y) \le \delta(s, u) < d[u]$$
   This means $d[y] < d[u]$.
8. However, both $y$ and $u$ were in the priority queue frontier, and the algorithm extracted $u$ instead of $y$. By definition of the minimum extraction greedy choice, it must be that:
   $$d[u] \le d[y]$$
9. We have reached a contradiction: $d[u] < d[u]$ (via $d[u] \le d[y] < d[u]$).
10. Therefore, the assumption was false. Thus, $d[u] = \delta(s, u)$ for every node $u$ at the moment it is settled. $\blacksquare$
