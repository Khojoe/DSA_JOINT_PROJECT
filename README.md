# Ghana Smart Service Operations Optimizer — Courier / Delivery

DCIT 204/308 Joint DSA Semester Project. Local context: a courier/food
delivery platform operating across Accra, Ghana (vendors, customer zones,
rider hubs, and the road network between them).

**Status: All 3 phases complete and integrated into one working system.**

- Phase 1: dataset, database schema, seed data, custom data-structure
  library with unit tests, and a console demo that loads real data from
  MySQL into those structures. Verified: 47/47 unit tests passing,
  `mvn compile` and `mvn test` both green, DB load confirmed against a
  real MySQL instance (67 locations, 310 requests loaded live).
- Phase 2: search & sort suite, graph engine (BFS/DFS/Dijkstra/Prim/
  Kruskal + disjoint set), and a dispatch scheduling engine — all built
  on top of the Phase 1 custom structures.
- Phase 3: greedy nearest-rider dispatch (using real Dijkstra routing),
  a DP knapsack optimizer with a demonstrated greedy-vs-DP counterexample,
  an **IntegratedDispatchService** that runs Phase 1's data, Phase 2's
  algorithms, and Phase 3's optimisation together as one real operation
  (menu options 14-16), and a **PerformanceLab** (option 17) that runs
  real timed measurements of every sort/search/graph algorithm at
  increasing input sizes, saves them to the `algorithm_runs` table, and
  exports a CSV for charting.

## Project phases

- **Phase 1 (done):** dataset + DB schema + custom data structures
  (linked list, stack, circular queue, deque, priority queue/heap,
  BST, hash table) + unit tests + DB loader.
- **Phase 2 (done):** search/sort suite (linear/binary search,
  selection/insertion/merge/quick sort), graph engine (BFS/DFS via
  Phase 1's deque/stack, Dijkstra, Prim, Kruskal + disjoint set), and
  a dispatch scheduling engine (FIFO + urgency-priority).
- **Phase 3 (done):** greedy nearest-rider dispatch, DP knapsack
  optimizer (with a demonstrated greedy-vs-DP counterexample), and the
  IntegratedDispatchService that runs everything together as one real
  dispatch operation with database write-back and an audit trail.

## Schema note

Column names and ID formats (`L001`, `R001`, `Q001`, `V001`/`R001`/`B001`/`T001`)
follow the team's shared CSV templates (`locations_template.csv`,
`roads_template.csv`, `resources_template.csv`, `service_requests_template.csv`)
so everyone's data lines up in one database. Location/resource "type" fields
use the templates' single-word title-case style (`Vendor`, `CustomerZone`,
`RiderHub`, `Landmark`, `Rider`, `Bicycle`, `Tricycle`, `Van`), and
timestamps use `yyyy-MM-ddTHH:mm` in the CSVs (stored as `DATETIME` in MySQL).

## Requirements

- Java 17+ (JDK)
- Maven
- MySQL 8.x running locally

## Setup

1. **Create the database:**

   ```bash
   mysql -u root -p < database/schema.sql
   ```

2. **Configure credentials:** edit
   `src/main/java/gh/dso/db/DatabaseConnection.java` with your local
   MySQL username/password (do not commit real credentials — this file
   currently has placeholder values).

3. **Seed data** is already generated in `database/seed/*.csv`
   (67 locations, 110 roads, 310 service requests, 32 resources,
   32 algorithm runs — all above the brief's minimums). To regenerate
   with different randomisation:

   ```bash
   cd database/seed && python3 generate_seed_data.py
   ```

4. **Import CSVs into MySQL.** Simplest is `LOAD DATA LOCAL INFILE` per
   table, or write a one-off runner that calls the `DataLoader.importXFromCsv(...)`
   methods already provided in `gh.dso.db.DataLoader`.

5. **Build and run:**

   ```bash
   mvn compile
   mvn exec:java -Dexec.mainClass="gh.dso.Main"
   ```

   (or run `Main.java` directly from your IDE)

6. **Run tests:**
   ```bash
   mvn test
   ```

## Project structure

```
database/
  schema.sql              -- MySQL schema (6 tables)
  seed/
    generate_seed_data.py -- generates the CSVs below
    locations.csv
    roads.csv
    service_requests.csv
    resources.csv
    algorithm_runs.csv
src/main/java/gh/dso/
  datastructures/
    list/       MyLinkedList, MyIterator
    stack/      MyStack
    queue/      MyQueue (interface), CircularQueue
    deque/      MyDeque
    heap/       MyPriorityQueue (binary heap)
    tree/       BST
    hash/       MyHashTable
  algorithms/
    search/     SearchAlgorithms (linear, binary)
    sort/       SortAlgorithms (selection, insertion, merge, quick)
  graph/        Graph, GraphTraversal (BFS/DFS), Dijkstra,
                DisjointSet, Kruskal, Prim
  scheduling/   DispatchScheduler (FIFO + urgency-priority)
  optimization/ GreedyDispatcher, KnapsackOptimizer (greedy + DP),
                IntegratedDispatchService (auto + interactive dispatch)
  performance/  PerformanceLab (real timing benchmarks -> DB + CSV)
  model/        Location, Road, ServiceRequest, Resource, AlgorithmRun
  db/           DatabaseConnection, DataLoader, SeedImporter
  ProjectParameters.java   Index-number-derived constants (documented)
  Main.java     Console app covering all 3 phases (options 1-17)
src/test/java/gh/dso/
  datastructures/  MyLinkedListTest, MyStackTest, CircularQueueTest,
                   MyDequeTest, MyPriorityQueueTest, BSTTest, MyHashTableTest
  algorithms/      SearchAlgorithmsTest, SortAlgorithmsTest
  graph/           GraphTraversalTest, DijkstraTest, DisjointSetTest, MstTest
  optimization/    GreedyDispatcherTest, KnapsackOptimizerTest
  performance/     PerformanceLabTest
```

## Notes for the report / brief compliance

- **No built-in Java collections used for assessed core logic** —
  `MyLinkedList`, `MyStack`, `CircularQueue`, `MyDeque`, `MyPriorityQueue`,
  `BST`, and `MyHashTable` are all implemented from scratch, per
  Section 8(i) of the brief.
- **Index-number-derived parameters:** all three required parameters
  are now derived from real team index numbers and centralised in
  `gh.dso.ProjectParameters` (with the derivation formula documented in
  the code comments):
  - `HASH_TABLE_SIZE = 31` — from Immanuel's index (22243130): last 2
    digits (30) rounded up to the next prime (31).
  - `RANDOM_SEED = 830` — sum of the last 3 digits of all three members'
    index numbers: 130 + 544 + 156 = 830. Used by both
    `generate_seed_data.py` and `PerformanceLab` so results are
    reproducible.
  - `DEFAULT_VEHICLE_CAPACITY = 7` — from Jonas's index (22198544): last
    2 digits (44), `44 % 10 = 4`, plus a base capacity of 3.
  - Team members used: Immanuel Oheneba Debe (22243130), Jonas Kudzo
    Amuzu (22198544), Cedric Dzodzodzi (22046156).
- **Trace tables:** `MyStack.capacity()`, `CircularQueue.snapshotInQueueOrder()`,
  and `BST.searchPathLength()` were added specifically so you can print
  before/after states for the trace-table evidence required in Section 6.
- **Edge cases covered in tests:** empty structure, single element,
  duplicate keys (BST), invalid input (negative capacity/table size),
  full queue, hash collisions — per Section 10.
- Every custom structure ships with unit tests covering normal, boundary,
  and invalid-input cases (Section 8(iii)).

## What's still outstanding

The code and its unit tests are complete and internally consistent, but
these are NOT done yet and still need real work before submission:

- **Written report**: trace tables, correctness proof sketches, and
  complexity analysis. Helper methods exist for this (`MyStack.capacity()`,
  `CircularQueue.snapshotInQueueOrder()`, `BST.searchPathLength()`,
  `SortAlgorithms`/`SearchAlgorithms` comparison/swap counts) but the
  actual written document hasn't been drafted.
- **Verification on real hardware**: options 1, 2, and 13 have been
  confirmed working against a live MySQL database. Options 3-12 and
  14-17 are unit-tested but have not yet been run end-to-end by a team
  member — do this before relying on them for a demo.
- **Team integration**: this was built as a solo effort covering the
  full brief; if teammates are contributing their own modules, those
  need to be merged in and re-tested.
- **`results/performance_results.csv`** (generated by option 17) still
  needs to be opened in Excel/Sheets and turned into actual charts for
  the report — the CSV export gives you the data, not the chart image.
