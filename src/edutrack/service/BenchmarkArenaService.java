package edutrack.service;

import edutrack.algorithms.dp.DamerauLevenshtein;
import edutrack.algorithms.dp.Levenshtein;
import edutrack.algorithms.dp.MatrixChainMult;
import edutrack.algorithms.flow.DinicsAlgorithm;
import edutrack.algorithms.flow.EdmondsKarp;
import edutrack.algorithms.flow.FlowNetwork;
import edutrack.algorithms.flow.FordFulkerson;
import edutrack.algorithms.parallel_random.BlellochScan;
import edutrack.algorithms.parallel_random.ParallelReduce;
import edutrack.algorithms.parallel_random.RandomizedQuickSort;
import edutrack.algorithms.strings.KmpMatcher;
import edutrack.algorithms.strings.RabinKarp;
import edutrack.algorithms.strings.ZAlgorithm;
import edutrack.algorithms.suffix.SAIS;
import edutrack.algorithms.suffix.SuffixArray;
import edutrack.algorithms.suffix.SuffixAutomaton;
import edutrack.core.MyArrayList;

/**
 * Benchmark Arena Service orchestrating reproducible, multi-algorithm showdowns.
 * Strictly zero java.util.* dependencies.
 */
public class BenchmarkArenaService {

    public static class ArenaResult {
        public String algorithmName;
        public String complexity;
        public long elapsedNanos;
        public double elapsedMicros;
        public long operations;
        public double speedup;
        public String notes;

        public ArenaResult(String algorithmName, String complexity, long elapsedNanos, long operations, String notes) {
            this.algorithmName = algorithmName;
            this.complexity = complexity;
            this.elapsedNanos = elapsedNanos;
            this.elapsedMicros = elapsedNanos / 1000.0;
            this.operations = operations;
            this.speedup = 1.0;
            this.notes = notes;
        }
    }

    public static class ShowdownCategory {
        public String title;
        public String description;
        public String inputDescription;
        public MyArrayList<ArenaResult> results;

        public ShowdownCategory(String title, String description, String inputDescription) {
            this.title = title;
            this.description = description;
            this.inputDescription = inputDescription;
            this.results = new MyArrayList<ArenaResult>();
        }
    }

    // =========================================================================
    // 1. STRING SEARCH SHOWDOWN
    // =========================================================================
    public ShowdownCategory runStringSearchShowdown(int textLength) {
        ShowdownCategory cat = new ShowdownCategory(
                "String Pattern Matching Showdown",
                "Head-to-head race between Exact Pattern Search paradigms under adversarial repetitive text.",
                "Corpus: " + textLength + " chars with periodic repetitive prefixes ('ABABCABAB...') | Pattern: 12 chars"
        );

        // Generate synthetic repetitive text to stress naive search
        StringBuilder sb = new StringBuilder(textLength);
        String unit = "ABABCABAB";
        while (sb.length() < textLength) {
            sb.append(unit);
        }
        String text = sb.substring(0, textLength);
        String pattern = "ABABCABABX"; // mismatch at the end to trigger maximum backtracking

        // 1. Naive Search
        long opsNaive = 0;
        long start = System.nanoTime();
        int naiveMatches = 0;
        int n = text.length();
        int m = pattern.length();
        for (int i = 0; i <= n - m; i++) {
            boolean match = true;
            for (int j = 0; j < m; j++) {
                opsNaive++;
                if (text.charAt(i + j) != pattern.charAt(j)) {
                    match = false;
                    break;
                }
            }
            if (match) naiveMatches++;
        }
        long timeNaive = Math.max(1, System.nanoTime() - start);
        cat.results.add(new ArenaResult("Naive Brute-Force", "O(N * M)", timeNaive, opsNaive, "High backtracking overhead on periodic prefixes"));

        // 2. Rabin-Karp Rolling Hash
        start = System.nanoTime();
        MyArrayList<Integer> rkMatches = RabinKarp.search(text, pattern);
        long timeRK = Math.max(1, System.nanoTime() - start);
        cat.results.add(new ArenaResult("Rabin-Karp (Double Hash)", "O(N + M) avg", timeRK, n, "64-bit double hash rolling window with 0 collisions"));

        // 3. Z-Algorithm Box Search
        start = System.nanoTime();
        String concat = pattern + "$" + text;
        int[] z = ZAlgorithm.computeZ(concat);
        int zMatches = 0;
        for (int i = m + 1; i < z.length; i++) {
            if (z[i] == m) zMatches++;
        }
        long timeZ = Math.max(1, System.nanoTime() - start);
        cat.results.add(new ArenaResult("Z-Algorithm (Z-Box)", "O(N + M)", timeZ, concat.length(), "Constructs [L, R] segment boxes in single linear pass"));

        // 4. Knuth-Morris-Pratt (KMP)
        start = System.nanoTime();
        MyArrayList<Integer> kmpMatches = KmpMatcher.search(text, pattern);
        long timeKmp = Math.max(1, System.nanoTime() - start);
        cat.results.add(new ArenaResult("Knuth-Morris-Pratt (KMP)", "O(N + M)", timeKmp, n + m, "Prefix failure function π with zero text backtracking"));

        computeSpeedups(cat.results);
        return cat;
    }

    // =========================================================================
    // 2. NETWORK FLOW SHOWDOWN
    // =========================================================================
    public ShowdownCategory runNetworkFlowShowdown(int vertices) {
        ShowdownCategory cat = new ShowdownCategory(
                "Maximum Flow & Augmenting Paths Showdown",
                "Compares Ford-Fulkerson (DFS), Edmonds-Karp (BFS shortest paths), and Dinic's Blocking Flow.",
                "Dense Multi-Stage Network: " + vertices + " vertices, " + (vertices * 4) + " residual edges."
        );

        int source = 0;
        int sink = vertices - 1;

        // Build 3 identical networks
        FlowNetwork netFF = createTestFlowNetwork(vertices, source, sink);
        FlowNetwork netEK = createTestFlowNetwork(vertices, source, sink);
        FlowNetwork netDinic = createTestFlowNetwork(vertices, source, sink);

        // 1. Ford-Fulkerson
        long start = System.nanoTime();
        int flowFF = FordFulkerson.maxFlow(netFF, source, sink);
        long timeFF = Math.max(1, System.nanoTime() - start);
        cat.results.add(new ArenaResult("Ford-Fulkerson (DFS)", "O(E * |f*|)", timeFF, flowFF, "DFS augmenting paths; vulnerable to path lengths"));

        // 2. Edmonds-Karp
        start = System.nanoTime();
        int flowEK = EdmondsKarp.maxFlow(netEK, source, sink);
        long timeEK = Math.max(1, System.nanoTime() - start);
        cat.results.add(new ArenaResult("Edmonds-Karp (BFS)", "O(V * E^2)", timeEK, flowEK, "BFS guarantees shortest augmenting paths; avoids long loops"));

        // 3. Dinic's Algorithm
        start = System.nanoTime();
        int flowDinic = DinicsAlgorithm.maxFlow(netDinic, source, sink);
        long timeDinic = Math.max(1, System.nanoTime() - start);
        cat.results.add(new ArenaResult("Dinic's Algorithm", "O(V^2 * E)", timeDinic, flowDinic, "Layered level graph + DFS blocking flows with pointer pruning"));

        computeSpeedups(cat.results);
        return cat;
    }

    private FlowNetwork createTestFlowNetwork(int n, int s, int t) {
        FlowNetwork net = new FlowNetwork(n);
        for (int i = 1; i <= n / 4; i++) {
            net.addEdge(s, i, 15);
        }
        for (int i = 1; i <= n / 4; i++) {
            for (int j = n / 4 + 1; j <= n / 2; j++) {
                net.addEdge(i, j, (i + j) % 7 + 1);
            }
        }
        for (int i = n / 4 + 1; i <= n / 2; i++) {
            for (int j = n / 2 + 1; j <= 3 * n / 4; j++) {
                net.addEdge(i, j, (i * 2 + j) % 5 + 1);
            }
        }
        for (int i = n / 2 + 1; i <= 3 * n / 4; i++) {
            net.addEdge(i, t, 12);
        }
        return net;
    }

    // =========================================================================
    // 3. SUFFIX INDEXING SHOWDOWN
    // =========================================================================
    public ShowdownCategory runSuffixIndexingShowdown(int textLength) {
        ShowdownCategory cat = new ShowdownCategory(
                "Suffix Structures & Text Indexing Showdown",
                "Evaluates Construction and Query Performance: Suffix Array vs Linear SA-IS vs Suffix Automaton DAWG.",
                "Academic Document Corpus: " + textLength + " characters."
        );

        StringBuilder sb = new StringBuilder(textLength);
        String phrase = "DataStructuresAndAlgorithmsForIntelligentAcademicPlatforms";
        while (sb.length() < textLength) {
            sb.append(phrase);
        }
        String doc = sb.substring(0, textLength);

        // 1. Standard Suffix Array (Prefix Doubling)
        long start = System.nanoTime();
        SuffixArray sa = new SuffixArray(doc);
        long timeSA = Math.max(1, System.nanoTime() - start);
        cat.results.add(new ArenaResult("Prefix-Doubling Suffix Array", "O(N log^2 N)", timeSA, doc.length(), "Prefix doubling with merge sort; compact array storage"));

        // 2. Linear SA-IS
        start = System.nanoTime();
        int[] saLinear = SAIS.buildSuffixArray(doc);
        long timeSAIS = Math.max(1, System.nanoTime() - start);
        cat.results.add(new ArenaResult("SA-IS Induced Sorting", "O(N)", timeSAIS, saLinear.length, "Strictly linear time bucket sorting with LMS character classification"));

        // 3. Suffix Automaton (DAWG)
        start = System.nanoTime();
        SuffixAutomaton sam = new SuffixAutomaton(doc);
        long distinctSubstrings = sam.countDistinctSubstrings();
        long timeSAM = Math.max(1, System.nanoTime() - start);
        cat.results.add(new ArenaResult("Suffix Automaton (DAWG)", "O(N) build / O(|P|) query", timeSAM, distinctSubstrings, "Minimal directed acyclic word graph with <= 2N-1 states"));

        computeSpeedups(cat.results);
        return cat;
    }

    // =========================================================================
    // 4. DYNAMIC PROGRAMMING SHOWDOWN
    // =========================================================================
    public ShowdownCategory runDynamicProgrammingShowdown(int stringLength) {
        ShowdownCategory cat = new ShowdownCategory(
                "Dynamic Programming Recurrences Showdown",
                "Benchmarks 2D Edit Distance vs Matrix-Chain Multiplication.",
                "Strings: " + stringLength + " characters | Matrix Chain: 20 matrices."
        );

        StringBuilder s1b = new StringBuilder(stringLength);
        StringBuilder s2b = new StringBuilder(stringLength);
        for (int i = 0; i < stringLength; i++) {
            s1b.append((char) ('a' + (i % 26)));
            s2b.append((char) ('a' + ((i + 1) % 26)));
        }
        String s1 = s1b.toString();
        String s2 = s2b.toString();

        // 1. Levenshtein
        long start = System.nanoTime();
        int lev = Levenshtein.computeDistance(s1, s2);
        long timeLev = Math.max(1, System.nanoTime() - start);
        cat.results.add(new ArenaResult("Wagner-Fischer Levenshtein", "O(N * M)", timeLev, (long) stringLength * stringLength, "2D recurrence table; insert/delete/substitute costs"));

        // 2. Damerau-Levenshtein
        start = System.nanoTime();
        int dam = DamerauLevenshtein.computeDistance(s1, s2);
        long timeDam = Math.max(1, System.nanoTime() - start);
        cat.results.add(new ArenaResult("Damerau-Levenshtein (Transposition)", "O(N * M)", timeDam, (long) stringLength * stringLength, "Optimal String Alignment handling adjacent transpositions"));

        // 3. Matrix Chain Multiplication
        int[] dims = new int[25];
        for (int i = 0; i < dims.length; i++) {
            dims[i] = 10 + (i * 7) % 50;
        }
        start = System.nanoTime();
        MatrixChainMult.McmResult mcm = MatrixChainMult.solve(dims, null);
        long timeMCM = Math.max(1, System.nanoTime() - start);
        cat.results.add(new ArenaResult("Matrix-Chain Multiplication", "O(N^3)", timeMCM, mcm.minMultiplications, "Dynamic parenthesization over " + (dims.length - 1) + " projection matrices"));

        computeSpeedups(cat.results);
        return cat;
    }

    // =========================================================================
    // 5. PARALLEL & SORTING SHOWDOWN
    // =========================================================================
    public ShowdownCategory runParallelSortingShowdown(int arraySize) {
        ShowdownCategory cat = new ShowdownCategory(
                "Parallel Primitives & High-Throughput Sorting Showdown",
                "Races Randomized QuickSort, Blelloch Work-Efficient Scan, and Multi-Threaded Reduce.",
                "Dataset: " + arraySize + " elements in memory."
        );

        long[] arrLong = new long[arraySize];
        double[] arrDouble = new double[arraySize];
        MyArrayList<Integer> listInt = new MyArrayList<Integer>(arraySize);

        long seed = 88172645463325252L;
        for (int i = 0; i < arraySize; i++) {
            seed ^= (seed << 13);
            seed ^= (seed >>> 7);
            seed ^= (seed << 17);
            int val = (int) (Math.abs(seed) % 1000);
            arrLong[i] = val;
            arrDouble[i] = val;
            listInt.add(val);
        }

        // 1. Sequential Scan
        long start = System.nanoTime();
        long[] seqScan = new long[arraySize];
        long running = 0;
        for (int i = 0; i < arraySize; i++) {
            running += arrLong[i];
            seqScan[i] = running;
        }
        long timeSeq = Math.max(1, System.nanoTime() - start);
        cat.results.add(new ArenaResult("Sequential Prefix Scan", "O(N) work, O(N) span", timeSeq, arraySize, "Single-threaded linear accumulation"));

        // 2. Blelloch Parallel Scan
        start = System.nanoTime();
        long[] blelloch = BlellochScan.inclusiveScan(arrLong);
        long timeBlelloch = Math.max(1, System.nanoTime() - start);
        cat.results.add(new ArenaResult("Blelloch Parallel Scan", "O(N) work, O(log N) span", timeBlelloch, arraySize * 2, "Two-pass up-sweep reduce and down-sweep tree distribution"));

        // 3. Randomized QuickSort
        start = System.nanoTime();
        RandomizedQuickSort.sort(listInt);
        long timeSort = Math.max(1, System.nanoTime() - start);
        cat.results.add(new ArenaResult("Randomized QuickSort", "O(N log N) expected", timeSort, (long) arraySize * 17, "Uniform pseudo-random pivot selection with indicator variable bound"));

        // 4. Multi-Threaded Parallel Reduce
        start = System.nanoTime();
        ParallelReduce.AggregateStats stats = ParallelReduce.reduce(arrDouble, 4);
        long timeReduce = Math.max(1, System.nanoTime() - start);
        cat.results.add(new ArenaResult("Multi-Threaded Parallel Reduce", "O(N) work, O(log N) span", timeReduce, (long) stats.sum, "Fork-join tree reduction across active CPU cores"));

        computeSpeedups(cat.results);
        return cat;
    }

    private void computeSpeedups(MyArrayList<ArenaResult> list) {
        if (list.size() == 0) return;
        long maxTime = 1;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).elapsedNanos > maxTime) {
                maxTime = list.get(i).elapsedNanos;
            }
        }
        for (int i = 0; i < list.size(); i++) {
            ArenaResult res = list.get(i);
            res.speedup = (double) maxTime / Math.max(1, res.elapsedNanos);
        }
    }
}
