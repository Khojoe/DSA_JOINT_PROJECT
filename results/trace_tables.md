# Generated Course Brief Trace Tables

This document contains the six trace tables required by **Section 10 of the Joint DSA Semester Project Brief**.

---

## Trace Table 1: Binary Search

Target: `7` | Input array (sorted): `[1, 3, 5, 7, 9, 11, 13]`

| Step | low | high | mid | value at mid | comparison | action |
|---|---|---|---|---|---|---|
| 1 | 0 | 6 | 3 | 7 | equal | Return index 3 |


---

## Trace Table 2: Insertion Sort

Initial Array: `[29, 4, 71, 15, 8]`

| Pass | i | key | Array State After Pass | Shifts Made |
|---|---|---|---|---|
| 0 | - | - | [29, 4, 71, 15, 8] | - |
| 1 | 1 | 4 | [4, 29, 71, 15, 8] | 1 |
| 2 | 2 | 71 | [4, 29, 71, 15, 8] | 0 |
| 3 | 3 | 15 | [4, 15, 29, 71, 8] | 2 |
| 4 | 4 | 8 | [4, 8, 15, 29, 71] | 3 |


---

## Trace Table 3: Quick Sort

Initial Array: `[29, 4, 71, 15, 8]` (Lomuto Partitioning, Pivot is the last element)

| Call | lo | hi | Pivot Selected | Partition Index | Swaps Made | Resulting Array Partition |
|---|---|---|---|---|---|---|
| 1 | 0 | 4 | 8 | 1 | 2 | [4, 8, 71, 15, 29] |
| 2 | 2 | 4 | 29 | 3 | 2 | [15, 29, 71] |


---

## Trace Table 4: Dijkstra's Shortest Path

Graph:
- A -> B (weight 2.0)
- A -> C (weight 5.0)
- B -> C (weight 1.0)
- B -> D (weight 6.0)
- C -> D (weight 3.0)

Source: `A`

| Step | Settled | Selected Node | dist[A] | dist[B] | dist[C] | dist[D] | prev[A] | prev[B] | prev[C] | prev[D] |
|---|---|---|---|---|---|---|---|---|---|---|
| 1 | [A] | A | 0.0 | INF | INF | INF | - | - | - | - |
| 2 | [A, B] | B | 0.0 | 2.0 | 5.0 | INF | - | A | A | - |
| 3 | [A, B, C] | C | 0.0 | 2.0 | 3.0 | 8.0 | - | A | B | B |
| 4 | [A, B, C, D] | D | 0.0 | 2.0 | 3.0 | 6.0 | - | A | B | C |


---

## Trace Table 5: Kruskal's Minimum Spanning Tree

Graph Nodes: `A, B, C, D`

Edges sorted by weight:
1. B-C (weight 1.0)
2. A-B (weight 2.0)
3. C-D (weight 3.0)
4. A-C (weight 5.0)
5. B-D (weight 6.0)

| Step | Edge Considered | Weight | Union-Find Sets / Component Mapping | Cycle Detected? | Included in MST? | Total MST Weight |
|---|---|---|---|---|---|---|
| 1 | B-C | 1.0 | {A:A, B:B, C:B, D:D} | NO | YES | 1.0 |
| 2 | A-B | 2.0 | {A:B, B:B, C:B, D:D} | NO | YES | 3.0 |
| 3 | C-D | 3.0 | {A:B, B:B, C:B, D:B} | NO | YES | 6.0 |
| 4 | A-C | 5.0 | {A:B, B:B, C:B, D:B} | YES | NO | 6.0 |
| 5 | B-D | 6.0 | {A:B, B:B, C:B, D:B} | YES | NO | 6.0 |


---

## Trace Table 6: Knapsack DP Tabulation

Vehicle Capacity: `5` units
Candidates:
- Item 1: Document (weight 1, urgency 2)
- Item 2: Food (weight 2, urgency 4)
- Item 3: Grocery (weight 3, urgency 5)

### DP State Grid (Columns represent capacities 0 to 5)

| Item Index | Category | Weight | Value | c=0 | c=1 | c=2 | c=3 | c=4 | c=5 |
|---|---|---|---|---|---|---|---|---|---|
| 0 | - | - | - | 0 | 0 | 0 | 0 | 0 | 0 |
| 1 | Document | 1 | 2 | 0 | 2 | 2 | 2 | 2 | 2 |
| 2 | Food | 2 | 4 | 0 | 2 | 4 | 6 | 6 | 6 |
| 3 | Grocery | 3 | 5 | 0 | 2 | 4 | 6 | 7 | 9 |

### DP Backtracking Trace

| Backtracking Step | Remaining Capacity | Row i | Selected? | Reasoning |
|---|---|---|---|---|
| 1 | 5 | 3 (Grocery) | YES | dp[3][5] (9) != dp[2][5] (6) -> item chosen |
| 2 | 2 | 2 (Food) | YES | dp[2][2] (4) != dp[1][2] (2) -> item chosen |
| 3 | 0 | 1 (Document) | NO | dp[1][0] (0) == dp[0][0] (0) -> skip item |

**Final Optimal Selection**: `[Grocery, Food]` | **Total Urgency Value**: `9`
