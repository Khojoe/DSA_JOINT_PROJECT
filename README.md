# Ghana Smart Service Operations Optimizer — Courier / Delivery

DCIT 204/308 Joint DSA Semester Project. Local context: a courier/food
delivery platform operating across Accra, Ghana (vendors, customer zones,
rider hubs, and the road network between them).

**Status: Core implementation complete. The application now exposes a live courier-operations workflow that connects MySQL data to custom DSA structures, search/sort algorithms, routing, scheduling, optimisation, and database write-back. Final submission evidence and end-to-end verification remain.**

- Phase 1: dataset, database schema, seed data, custom data-structure
  library with unit tests, and a console demo that loads real data from
  MySQL into those structures. The project now contains the required custom structures plus advanced structures
  (dynamic array, red-black tree, B-tree, custom set/map, and adjacency matrix).
  The source tree contains more than 100 JUnit `@Test` methods; final `mvn test`
  verification must still be run on the team machine. DB load has previously been
  confirmed against a real MySQL instance (67 locations, 310 requests loaded live).
- Phase 2: search & sort suite, graph engine (BFS/DFS/Dijkstra/Prim/
  Kruskal + disjoint set), and a dispatch scheduling engine. These are now
  exposed through live courier operations as well as dedicated evidence labs.
- Phase 3: greedy nearest-rider dispatch (using real Dijkstra routing),
  a DP knapsack optimizer with a demonstrated greedy-vs-DP counterexample,
  an **IntegratedDispatchService** that runs the project's data, algorithms,
  and optimisation together as one real operation, and a **PerformanceLab**
  that runs three measured repetitions of every sort/search/graph benchmark,
  reports average runtime, saves measurements to the `algorithm_runs` table,
  and exports CSV output for charting.

## Project phases

- **Phase 1 (done):** dataset + DB schema + custom data structures
  (dynamic array, linked list, stack, circular queue, deque, priority
  queue/heap, BST, red-black tree, B-tree, hash table, custom set/map) + unit tests + DB loader.
- **Phase 2 (done):** search/sort suite (linear/binary search,
  selection/insertion/merge/quick sort), graph engine (adjacency list + adjacency matrix, BFS/DFS via
  Phase 1's deque/stack, Dijkstra, Prim, Kruskal + disjoint set), and
  a dispatch scheduling engine (FIFO + urgency-priority).
- **Phase 3 (done):** greedy nearest-rider dispatch, DP knapsack
  optimizer (with a demonstrated greedy-vs-DP counterexample), and the
  IntegratedDispatchService that runs everything together as one real
  dispatch operation with database write-back and an audit trail.


## Live courier operations

The main application is not limited to isolated algorithm demonstrations. The
**Courier Operations** menu loads current MySQL records and feeds them into the
project's custom structures and algorithms:

```
MySQL Database
      |
      +-- Locations ----> MyLinkedList ----> BST location index
      |
      +-- Requests ------> MyLinkedList ----> Hash-table request index
      |                         |
      |                         +----> Linear / Binary Search
      |                         |
      |                         +----> Selection / Insertion / Merge / Quick Sort
      |                         |
      |                         +----> MyDeque / CircularQueue / Priority Heap
      |
      +-- Roads ----------> Graph ----> BFS / DFS / Dijkstra
      |
      +-- Resources ------> custom request/resource pipeline
      |
      +----------------------------------------------+
                                                     |
                                             Dispatch Engine
                                                     |
                              +----------------------+----------------+
                              |                                       |
                         Scheduling                           Optimisation
                         FIFO / Priority                       Greedy / DP
                              |                                       |
                              +----------------------+----------------+
                                                     |
                                             Courier Assignment
                                                     |
                                             Database write-back
```

### Examples of real system use

- **Linear search:** find a request by ID in the database-backed request set
  without requiring sorted data.
- **Binary search:** sort request IDs first, then locate a request in
  `O(log n)` search steps.
- **Sorting:** organise pending `NEW` requests by urgency, deadline, or
  submission time before dispatch.
- **Hash table:** index requests by request ID for direct operational lookup.
- **BST:** index locations by name and report the search path and tree height.
- **Queue/deque:** model normal FIFO work and move critical requests to the
  front when appropriate.
- **Priority queue/heap:** produce an urgency-first dispatch order.
- **Dijkstra:** find the effective shortest route for a selected delivery.
- **Greedy assignment:** suggest the nearest reachable available courier.

The original **DSA Lab / Evidence** menus are retained so individual data
structures and algorithms can still be demonstrated independently for marking
and oral defence.

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

2. **Configure credentials with `.env`:** copy `.env.example` to `.env`
   in the project root and replace the placeholder password with your local
   MySQL password. The `.env` file is ignored by Git; do not commit it.

   Example:
   ```env
   DB_URL=jdbc:mysql://localhost:3306/ghana_courier_dso?useSSL=false&serverTimezone=UTC
   DB_USER=root
   DB_PASSWORD=your_mysql_password
   ```

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
   ```
   Then run `gh.dso.Main` directly from IntelliJ IDEA, or use your preferred
   Maven run configuration.

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
    tree/       BST, RedBlackTree, BTree
    hash/       MyHashTable
    array/      MyDynamicArray
    map/        MyMap
    set/        MySet
  algorithms/
    search/     SearchAlgorithms (linear, binary)
    sort/       SortAlgorithms (selection, insertion, merge, quick)
  graph/        Graph (adjacency list), GraphMatrix, GraphTraversal (BFS/DFS),
                Dijkstra, DisjointSet, Kruskal, Prim
  scheduling/   DispatchScheduler (FIFO + urgency-priority)
  optimization/ GreedyDispatcher, KnapsackOptimizer (greedy + DP),
                IntegratedDispatchService (auto + interactive dispatch)
  performance/  PerformanceLab (3-run timing benchmarks -> DB + CSV)
  model/        Location, Road, ServiceRequest, Resource, AlgorithmRun
  db/           DatabaseConnection, DataLoader, SeedImporter
  ProjectParameters.java   Index-number-derived constants (documented)
  ConsoleUI.java            Console formatting / examiner-friendly navigation
  Main.java                 Console app with grouped menus for all modules
src/test/java/gh/dso/
  datastructures/  dynamic array, linked list, stack, queue, deque, heap,
                   BST, red-black tree, B-tree, hash table, set/map tests
  algorithms/      SearchAlgorithmsTest, SortAlgorithmsTest
  graph/           GraphTraversalTest, DijkstraTest, DisjointSetTest, MstTest
  optimization/    GreedyDispatcherTest, KnapsackOptimizerTest
  performance/     PerformanceLabTest
```

## Notes for the report / brief compliance

- **Custom-structure requirement:** the assessed structures are implemented in
  the project's own classes. Built-in Java collections are used only where
  they are supporting application/database logic rather than replacing the
  assessed structure implementation.
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
- **Trace support:** dynamic-array resize tracing, red-black-tree rotation
  counts/structure summaries, B-tree split counts/structure summaries,
  circular-queue snapshots, BST search-path lengths, and algorithm comparison
  counters are available for the required evidence.
- **Edge cases covered in tests:** empty structure, single element,
  duplicate keys (BST), invalid input (negative capacity/table size),
  full queue, hash collisions — per Section 10.
- Every custom structure ships with unit tests covering normal, boundary,
  and invalid-input cases (Section 8(iii)).

## What's still outstanding

The main implementation gaps identified against the supplied brief have now been
addressed in the source tree. The remaining submission work is evidence and
verification rather than adding another major algorithm family:

- **Run `mvn test` on the team machine** and record the actual final test count.
- **Generate the six required trace tables** (binary search, insertion sort,
  merge/quick sort, Dijkstra, Kruskal/Prim, and DP).
- **Write at least three proof sketches**, including a search/sort invariant,
  a recursive/divide-and-conquer argument, and a greedy/DP correctness idea.
- **Document the two required counterexamples** (greedy failure and binary
  search on unsorted input).
- **Run the three-repetition performance lab on the same machine** and retain
  the raw CSV.
- **Create the required performance graphs** and interpret theory versus
  observed runtime.
- **Prepare the technical report in DOCX and PDF**, including screenshots,
  schema, data dictionary, architecture, testing, performance and appendices.
- **Prepare the development log, individual contribution statement, oral-defense
  notes, and 5–8 minute demonstration script/video.**
- **Perform a final live database/demo verification** before submission.


## AI Assistance & Academic Integrity

This project was developed with the support of AI-assisted tools during selected stages of the development process, including code review, debugging, documentation, algorithmic clarification, and refinement of implementation ideas.

In accordance with **Section 15(vi) of the DCIT 204/308 Joint DSA Semester Project Brief**, all AI assistance used during development is acknowledged. The use of AI was treated as a development aid rather than a replacement for the team's understanding, implementation, testing, or decision-making.

### Use of AI Assistance

AI tools were used selectively to support activities such as:

* identifying and debugging implementation issues;
* reviewing and improving code structure and readability;
* clarifying data-structure and algorithm concepts;
* suggesting approaches for testing, performance analysis, and documentation;
* assisting with project organization and technical writing.

All generated suggestions were reviewed, adapted, tested, and integrated by the project team where appropriate. The final implementation, database configuration, test results, performance measurements, demonstrations, and project-specific decisions remain the responsibility of the team.

### Supporting Prompts

The prompts and development interactions used during AI-assisted development have been retained and will be provided as part of the final project submission in accordance with the project requirements.

### Team Accountability

The project team acknowledges full responsibility for understanding and defending the submitted implementation. Every team member is expected to understand the data structure and algorithmic components relevant to their contribution and to be able to explain, modify, and demonstrate those components during the oral defense.

### Project Team

**DCIT 204/308 Joint DSA Semester Project**

* **Immanuel Oheneba Debe** - 22243130
* **Jonas Kudzo Amuzu** - 22198544
* **Cedric Dzodzodzi** - 22046156
* **Bilson Priscilla Essirifua** - 22015128
* **Emerald Aryee** - 22032619
* **Meshach Ashitei Amarh** - 22231662
* **Bamfo Kwabena Asante** - 22262799
* **Frederick Addison** -22310210
* **Lawrencia Ametu** - 22413790
* **Gladys Anobea Antwi** - 22376230
* **Bernard Tetteh** - 22392052
* **Samuel Osei- Bonsu** - 22380629
* **Roland Otabil Kwesi** - 22268717
* **Oduah Chisom** - 22254852
* **Eduful Eugene Nana Yaw Addo** - 22383968

> **Academic integrity statement:** AI-assisted development does not replace individual or team accountability. All team members remain responsible for the correctness, originality, understanding, and defense of the work submitted.

