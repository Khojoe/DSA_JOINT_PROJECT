# Ghana Courier Service — DSA Architecture

The application is organised as a real courier workflow rather than a collection of unrelated DSA demonstrations.

```text
                    GHANA COURIER SERVICE
                             |
                        MySQL Database
                             |
              +--------------+--------------+
              |              |              |
          Locations       Requests       Resources
              |              |              |
              |       Custom DSA Layer     |
              |              |              |
              |      +-------+-------+      |
              |      |       |       |      |
              |    Queue    Heap   Index    |
              |      |       |       |      |
              |      +-------+-------+      |
              |              |              |
              |        Search & Sort        |
              |              |              |
              +--------------+--------------+
                             |
                     DISPATCH ENGINE
                             |
              +--------------+--------------+
              |              |              |
            Routing      Scheduling    Optimisation
              |              |              |
          BFS / DFS      FIFO /       Greedy / DP
          Dijkstra       Priority          |
              |              |              |
              +--------------+--------------+
                             |
                    Courier Assignment
                             |
                       Database Update
```

## Operational mapping

| Courier operation | DSA implementation | Purpose |
|---|---|---|
| Load records | `MyLinkedList` | Primary custom in-memory record store |
| Request lookup | `MyHashTable` | Request-ID index for fast lookup |
| Location lookup | `BST` | Ordered location index and search path evidence |
| Request search | Linear / Binary Search | Find requests by ID and location |
| Request prioritisation | Selection / Insertion / Merge / Quick Sort | Order jobs by urgency, deadline or submission time |
| Normal dispatch line | `MyDeque` | FIFO request handling |
| Critical requests | `MyDeque` | Insert urgent work at the front |
| Rider hub waiting line | `CircularQueue` | Bounded FIFO resource queue |
| Urgency dispatch | `MyPriorityQueue` | Highest urgency first, deadline/submission tie-breakers |
| Delivery routing | Graph + Dijkstra | Find effective shortest route |
| Reachability | BFS / DFS | Explore the road network |
| Courier suggestion | Greedy Dispatcher | Select nearest reachable available courier |
| Capacity optimisation | Greedy + DP | Compare fast heuristic and optimal selection |
| Final dispatch | `IntegratedDispatchService` | Write assignment/status changes back to MySQL |

## Live workflow example

A dispatcher can search for a request, inspect its details, sort the pending queue by urgency, preview FIFO/urgent/priority dispatch order, calculate the request's shortest route, and receive a nearest-courier suggestion. The existing integrated dispatch menu can then perform the database write-back.

The DSA Lab menus remain available separately for trace tables, correctness demonstrations, edge cases, and oral-defense evidence.
