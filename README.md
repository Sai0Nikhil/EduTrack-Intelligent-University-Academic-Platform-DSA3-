# EduTrack – Intelligent University Academic Platform
### Advanced Algorithms (DSA-3) Project & System Implementation

EduTrack is an intelligent university academic platform engineered from first principles in Java. It models university academic operations, including student and faculty records, course catalog search, assignment plagiarism detection, examination constraint scheduling, classroom and workstation network flow allocations, and continuous student activity stream analytics.

In strict compliance with the DSA curriculum requirements, **the core algorithm engine contains zero `java.util.*` dependencies**—all foundational data structures, string algorithms, dynamic programming tables, network flow residual graphs, NP reduction gadgets, and randomized/parallel primitives are handcrafted from scratch.

---

## 📋 Table of Contents
1. [Core Features & Syllabus Mapping](#core-features--syllabus-mapping)
2. [Zero-java.util Architecture](#zero-javautil-architecture)
3. [Project Directory Structure](#project-directory-structure)
4. [Dataset Catalog](#dataset-catalog)
5. [Interactive CLI Navigation](#interactive-cli-navigation)
6. [Compilation & Execution Guide](#compilation--execution-guide)
7. [Benchmark & Verification Suite](#benchmark--verification-suite)
8. [Comprehensive 30--50+ Page LaTeX Report & Overleaf Bundle](#comprehensive-30-50-page-latex-report--overleaf-bundle)

---

## 🎯 Core Features & Syllabus Mapping

### Module 1 & 2: String Algorithms & Suffix Structures
- **Knuth-Morris-Pratt (KMP) (`KmpMatcher.java`)**: Preprocesses prefix failure function $\pi[q]$ in $O(m)$ time; searches course names and academic queries in $O(n)$ with zero text backtracking.
- **Z-Algorithm (`ZAlgorithm.java`)**: Computes linear-time $Z$-box array in $O(n)$; detects exact phrase repetitions and duplicated sentences in student submissions.
- **Rabin-Karp Rolling Hash (`RabinKarp.java`)**: Double-hashing ($B_1=313, M_1=10^9+7$ and $B_2=317, M_2=10^9+9$) with $O(1)$ rolling window updates for instant course and assignment code matching without hash collisions.
- **Aho-Corasick Multi-Pattern Automaton (`AhoCorasick.java`)**: Constructs a Trie with suffix failure links and dictionary output links; tags multi-keyword academic taxonomies across documents in a single $O(|T| + \text{matches})$ pass.
- **Suffix Array (`SuffixArray.java`)**: Handcrafted prefix-doubling construction in $O(n \log^2 n)$ with $O(m \log n)$ binary search for indexing university textbooks and lecture notes.
- **SA-IS Linear Induced Sorting (`SAIS.java`)**: Demonstrates $O(n)$ induced sorting using S/L-type suffix classification, Leftmost S-type (LMS) character detection, and bucket sorting.
- **Kasai's LCP Array (`KasaiLCP.java`)**: Calculates Longest Common Prefix array in $O(n)$ time. Used on combined documents (`doc1 + "#" + doc2`) to detect plagiarized excerpts and shared text blocks.
- **Suffix Automaton (`SuffixAutomaton.java`)**: Minimal Directed Acyclic Word Graph (DAWG) with $\le 2n-1$ states and $\le 3n-4$ transitions; performs instant $O(|P|)$ substring queries, distinct substring counting, and Longest Common Substrings.

### Module 3: Advanced Dynamic Programming
- **Levenshtein Distance (`Levenshtein.java`)**: 2D Wagner-Fischer dynamic programming table providing automated spelling corrections and query suggestions for course catalogs.
- **Damerau-Levenshtein Distance (`DamerauLevenshtein.java`)**: Optimal String Alignment accounting for adjacent character transpositions (e.g. typos like `SC201` for `CS201`).
- **Matrix-Chain Multiplication (`MatrixChainMult.java`)**: Dynamic programming in $O(n^3)$ determining the optimal parenthesization order for chained academic data projection matrices.
- **Bitmask DP Traveling Academic Auditor (`BitmaskDP.java`)**: Solves the NP-hard Traveling Salesperson Problem (TSP) in $O(2^n \cdot n^2)$ over campus departments and reconstructs the minimum transit tour.
- **Optimal Binary Search Tree (`OptimalBST.java`)**: Organizes non-uniform academic search keys by query frequencies in $O(n^3)$ to minimize average key access depth.

### Module 4: Network Flow & Bipartite Matching
- **Residual Flow Network (`FlowNetwork.java`)**: Graph representation supporting forward and backward residual edges.
- **Ford-Fulkerson (`FordFulkerson.java`)**: Augmenting paths via Depth-First Search.
- **Edmonds-Karp (`EdmondsKarp.java`)**: Shortest augmenting paths via Breadth-First Search in $O(V E^2)$.
- **Dinic's Algorithm (`DinicsAlgorithm.java`)**: BFS level graphs and DFS blocking flows with pointer pruning in $O(V^2 E)$.
- **Bipartite Matching (`BipartiteMatching.java`)**: Maximum faculty-to-course allocation respecting instructor workload limits and subject qualifications.
- **König's Theorem (`KonigsTheorem.java`)**: Establishes duality between maximum matching and minimum vertex cover in bipartite networks, identifying the minimal bottleneck set of conflicting faculty and course nodes.

### Module 5: NP-Completeness, Reductions & Approximation
- **DPLL SAT Solver (`DpllSatSolver.java`)**: Solves exam timetabling and student scheduling constraints in 3-CNF using unit propagation, pure literal elimination, and backtracking.
- **3-SAT $\to$ CLIQUE Reduction (`SatToCliqueReduction.java`)**: Transforms $m$ 3-literal clauses into a graph with $3m$ vertices where non-conflicting literals share edges.
- **CLIQUE $\to$ INDEPENDENT SET Reduction (`CliqueToISReduction.java`)**: Constructs graph complement $\overline{G}$; demonstrates that a clique of size $k$ in $G$ corresponds to an independent set of size $k$ in $\overline{G}$.
- **INDEPENDENT SET $\to$ VERTEX COVER Reduction (`ISToVertexCover.java`)**: Demonstrates complementary duality where $S$ is an independent set $\iff V \setminus S$ is a vertex cover.
- **Vertex Cover 2-Approximation (`VertexCover2Approx.java`)**: Greedy maximal matching heuristic achieving a provable 2-approximation ratio for minimizing exam proctors/invigilators.

### Module 6: Randomized & Parallel Algorithms
- **Randomized QuickSort (`RandomizedQuickSort.java`)**: Expected $O(n \log n)$ academic merit ranking of students with random pivot selection and custom PRNG.
- **Reservoir Sampling (Algorithm R) (`ReservoirSampling.java`)**: Single-pass uniform sampling of $k$ items from continuous, unbounded student LMS activity streams with exact $k/N$ probability.
- **Miller-Rabin Primality Test (`MillerRabin.java`)**: Large numerical prime verification with 64-bit modular multiplication for generating secure cryptographic student authentication tokens.
- **Blelloch Parallel Scan (`BlellochScan.java`)**: Work-efficient parallel prefix sum (Up-sweep reduce phase + Down-sweep distribution phase) in $O(n)$ work and $O(\log n)$ span for cumulative student credits and percentiles.
- **Parallel Reduce (`ParallelReduce.java`)**: Multi-threaded tree reduction across CPU cores for aggregating student GPAs, minimums, maximums, and averages.
- **Brent's Theorem Analyzer (`BrentsTheorem.java`)**: Models parallel performance: $T_P \le \frac{T_1 - T_\infty}{P} + T_\infty$, calculating speedup, efficiency, and asymptotic ceilings.

---

## 🔒 Zero-java.util Architecture

All foundational collections inside `src/edutrack/core` were built without importing `java.util.*`:

| Custom Class | Underlying Structure | Time Complexity |
| :--- | :--- | :--- |
| `MyArrayList<T>` | Resizable Object array | Amortized $O(1)$ append, $O(1)$ get/set |
| `MyLinkedList<T>` | Doubly-linked nodes | $O(1)$ insert/remove first/last |
| `MyQueue<T>` | Linked FIFO queue | $O(1)$ enqueue and dequeue |
| `MyStack<T>` | Dynamic LIFO array | $O(1)$ push and pop |
| `MyHashMap<K, V>` | Chained bucket hash table with rehashing | $O(1)$ expected get and put |
| `MyMinHeap<T>` | Array binary min-heap with swim/sink | $O(\log N)$ insert and extractMin |
| `DisjointSetUnion` | Union-by-rank & path compression | Near $O(1)$ ($\alpha(N)$) find and union |
| `Pair<A, B>` | Generic 2-tuple | $O(1)$ |

*(The only standard Java import in the entire project is `java.util.Scanner` in `edutrack.ui.EduTrackCLI`, strictly for reading interactive console input).*

---

## 📁 Project Directory Structure

```
c:/DSA_3/
├── bin/                                  # Compiled .class binaries
├── data/                                 # Realistic Academic Datasets
│   ├── courses.csv                       # 25 courses across CS, AI, Math, Data Science
│   ├── students.csv                      # 20 student profiles with GPA, credits, attendance
│   ├── faculty.csv                       # 12 faculty profiles with workload & course qualifications
│   ├── assignments/                      # Student assignment submissions
│   │   ├── submission_cs101_alice.txt
│   │   ├── submission_cs101_bob.txt      # Contains shared paragraphs with Alice (plagiarism case)
│   │   └── submission_cs101_carol.txt
│   ├── library_docs/
│   │   └── algorithms_handbook.txt       # University library compendium for suffix indexing
│   └── activity_stream.log               # Streaming student LMS activity events
├── src/
│   └── edutrack/
│       ├── core/                         # Handcrafted Zero-java.util Data Structures
│       ├── model/                        # Course, Student, Faculty, Assignment domain models
│       ├── algorithms/
│       │   ├── strings/                  # KMP, Z-Algorithm, Rabin-Karp, Aho-Corasick
│       │   ├── suffix/                   # Suffix Array, SA-IS, Kasai LCP, Suffix Automaton
│       │   ├── dp/                       # Levenshtein, Damerau, MCM, Bitmask DP, OBST
│       │   ├── flow/                     # Ford-Fulkerson, Edmonds-Karp, Dinic, Bipartite, König
│       │   ├── np/                       # DPLL SAT, 3-SAT->Clique->IS->VC, 2-Approx VC
│       │   └── parallel_random/          # QuickSort, Reservoir, Miller-Rabin, Scan, Brent
│       ├── service/                      # Orchestration & business logic services
│       ├── io/                           # CSV and text file dataset loaders
│       └── ui/                           # Interactive numbered CLI application
├── run.bat                               # Windows Command Prompt launcher
├── run.ps1                               # PowerShell launcher
└── README.md                             # Platform documentation
```

---

## 🖥️ Interactive CLI Navigation

The CLI offers numbered menu navigation:

```
================================================================================
           EDUTRACK – INTELLIGENT UNIVERSITY ACADEMIC PLATFORM
                      [DSA-3 Advanced Algorithms Engine]
================================================================================
  [1] String Search & Keyword Analytics (KMP, Z-Algorithm, Rabin-Karp, Aho-Corasick)
  [2] Suffix Structures & Document Similarity (Suffix Array, SA-IS, Kasai LCP, SAM)
  [3] Advanced Dynamic Programming (Levenshtein, Damerau, MCM, Bitmask DP, OBST)
  [4] Network Flow & Resource Allocation (Bipartite Matching, EK, Dinic, König)
  [5] NP-Completeness, Reductions & Approximation (SAT, 3-SAT->Clique->IS->VC, 2-Approx)
  [6] Randomized & Parallel Algorithms (QuickSort, Reservoir, Miller-Rabin, Scan, Brent)
  [7] Run Full Integrated Verification & Benchmark Suite
  [0] Exit EduTrack
================================================================================
```

---

## 🚀 Compilation & Execution Guide

### Option 1: Desktop GUI (Windows Batch / Double-Click)
Launch the graphical user interface:
```bat
run-gui.bat
```

### Option 2: Desktop GUI (PowerShell)
```powershell
.\run-gui.ps1
```

### Option 3: Interactive CLI (Console)
Run the numbered console menu:
```bat
run.bat
```
*(or `.\run.ps1` in PowerShell. You can also press option `8` inside the CLI to launch the GUI).*

### Option 4: Automated Benchmark
To run the verification suite directly without manual menu navigation:
```powershell
.\run.ps1 --benchmark
```

---

## ✅ Benchmark & Verification Suite

All 16 core algorithms were tested with programmatic unit assertions:

```
================================================================================
             RUNNING COMPLETE EDUTRACK VERIFICATION BENCHMARK SUITE
================================================================================
[PASS] 1. KMP String Matcher: Correct prefix function & occurrences
[PASS] 2. Z-Algorithm: Accurate Z-box intervals and values
[PASS] 3. Rabin-Karp: Double-hash rolling window match
[PASS] 4. Aho-Corasick: Multi-keyword simultaneous matches
[PASS] 5. Suffix Array: O(N log^2 N) construction & binary search
[PASS] 6. Kasai LCP: Linear-time prefix array computation
[PASS] 7. Suffix Automaton: O(N) minimal DAG substring queries
[PASS] 8. Levenshtein: Wagner-Fischer 2D recurrence distance
[PASS] 9. Damerau-Levenshtein: Adjacent character transposition check
[PASS] 10. Matrix-Chain Multiplication: Optimal cost & parenthesization
[PASS] 11. Network Flow: Dinic's blocking flow max-flow match
[PASS] 12. DPLL SAT Solver: Satisfying truth assignment with unit propagation
[PASS] 13. Vertex Cover 2-Approximation: Maximal matching bound satisfied
[PASS] 14. Miller-Rabin: Accurate primality witness testing
[PASS] 15. Blelloch Parallel Scan: Work-efficient prefix sum equality
[PASS] 16. Brent's Theorem Analyzer: Analytical speedup & efficiency
================================================================================
VERIFICATION SUMMARY: 16 / 16 ALGORITHMS PASSED.
================================================================================
```

---

## 📚 Comprehensive 30--50+ Page LaTeX Report & Overleaf Bundle

A publication-grade, 11-chapter LaTeX technical report is provided in `latex/` and pre-packaged as `EduTrack_LaTeX_Report.zip` for instant one-click compilation in [Overleaf](https://www.overleaf.com/).

### Report Structure:
1. **Executive Abstract (`chapters/abstract.tex`)**: Algorithmic problem formulation and zero-`java.util` pedagogical paradigm.
2. **Chapter 1 (`chapters/ch01_introduction.tex`)**: Introduction, domain challenge, and scope.
3. **Chapter 2 (`chapters/ch02_core_structures.tex`)**: First-principles data structure engineering (Amortized array analysis, DSU invariants, min-heap swim/sink, quadratic hash probing).
4. **Chapter 3 (`chapters/ch03_string_algorithms.tex`)**: String matching (KMP, Z-Algorithm, Rabin-Karp, Aho-Corasick) with proofs, independent non-project examples, and EduTrack catalog search.
5. **Chapter 4 (`chapters/ch04_suffix_structures.tex`)**: Suffix structures (Suffix Array, SA-IS $O(N)$ induced sorting, Kasai LCP, Suffix Automaton DAWG) with the 278-character Alice vs. Bob plagiarism analysis.
6. **Chapter 5 (`chapters/ch05_dynamic_programming.tex`)**: Advanced Dynamic Programming (Levenshtein, Damerau-Levenshtein, Matrix Chain Multiplication, Bitmask DP TSP, Optimal BST).
7. **Chapter 6 (`chapters/ch06_network_flow.tex`)**: Network Flow & Matching (Ford-Fulkerson, Edmonds-Karp, Dinic's blocking flow, König's duality).
8. **Chapter 7 (`chapters/ch07_np_completeness.tex`)**: NP-Completeness, Reductions \& Approximation (DPLL 3-SAT solver, 3-SAT $\to$ CLIQUE $\to$ IS $\to$ VC reduction chain, 2-Approx Vertex Cover).
9. **Chapter 8 (`chapters/ch08_parallel_randomized.tex`)**: Randomized \& Parallel Primitives (Randomized QuickSort with indicator variables, Reservoir Sampling Algorithm R, Miller-Rabin primality, Blelloch prefix scan, Parallel Reduce, Brent's Theorem).
10. **Chapter 9 (`chapters/ch09_system_architecture.tex`)**: System Architecture, Data Engineering schemas, Service orchestration, and Dual Presentation (Terminal CLI + Swing GUI).
11. **Chapter 10 (`chapters/ch10_evaluation_screenshots.tex`)**: Empirical Evaluation, 16/16 algorithmic assertion pass table, and **Complete Screenshot Catalog with 10 visual placeholders & capture instructions**.
12. **Chapter 11 (`chapters/ch11_conclusion.tex`)**: Master Asymptotic Complexity Reference Table, empirical tradeoffs, pedagogical reflections, and future work.
13. **Bibliography (`references.bib`)**: 16 canonical textbook and peer-reviewed citations (CLRS, Kleinberg-Tardos, Erickson, Gusfield, Knuth, Rabin, Aho, Kasai, Nong, Dinic, Cook, Karp, etc.).

### Instant Overleaf Compilation:
- Simply upload `c:\DSA_3\EduTrack_LaTeX_Report.zip` directly to [Overleaf](https://www.overleaf.com/) $\to$ **New Project** $\to$ **Upload Project**, and press **Recompile**!
- Instructions for local compilation and screenshot placement are documented in `latex/COMPILE_GUIDE.md` and `latex/screenshots/README.md`.

