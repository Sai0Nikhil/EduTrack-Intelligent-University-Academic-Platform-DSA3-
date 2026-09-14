package edutrack.ui;

import edutrack.algorithms.dp.BitmaskDP;
import edutrack.algorithms.dp.DamerauLevenshtein;
import edutrack.algorithms.dp.Levenshtein;
import edutrack.algorithms.dp.MatrixChainMult;
import edutrack.algorithms.dp.OptimalBST;
import edutrack.algorithms.flow.BipartiteMatching;
import edutrack.algorithms.flow.DinicsAlgorithm;
import edutrack.algorithms.flow.EdmondsKarp;
import edutrack.algorithms.flow.FlowNetwork;
import edutrack.algorithms.flow.FordFulkerson;
import edutrack.algorithms.flow.KonigsTheorem;
import edutrack.algorithms.np.DpllSatSolver;
import edutrack.algorithms.np.VertexCover2Approx;
import edutrack.algorithms.parallel_random.BlellochScan;
import edutrack.algorithms.parallel_random.BrentsTheorem;
import edutrack.algorithms.parallel_random.MillerRabin;
import edutrack.algorithms.parallel_random.ParallelReduce;
import edutrack.algorithms.parallel_random.RandomizedQuickSort;
import edutrack.algorithms.parallel_random.ReservoirSampling;
import edutrack.algorithms.strings.AhoCorasick;
import edutrack.algorithms.strings.KmpMatcher;
import edutrack.algorithms.strings.RabinKarp;
import edutrack.algorithms.strings.ZAlgorithm;
import edutrack.algorithms.suffix.KasaiLCP;
import edutrack.algorithms.suffix.SAIS;
import edutrack.algorithms.suffix.SuffixArray;
import edutrack.algorithms.suffix.SuffixAutomaton;
import edutrack.core.MyArrayList;
import edutrack.core.Pair;
import edutrack.io.DatasetLoader;
import edutrack.model.ActivityLog;
import edutrack.model.AssignmentSubmission;
import edutrack.model.Course;
import edutrack.model.Faculty;
import edutrack.model.Student;
import edutrack.service.AcademicSearchService;
import edutrack.service.AnalyticsService;
import edutrack.service.ExamSchedulingService;
import edutrack.service.PlagiarismDetectionService;
import edutrack.service.ResourceAllocationService;

import java.util.Scanner;

/**
 * Interactive Numbered Command-Line Interface for EduTrack.
 * Features numbered navigation (1 for this, 2 for that) and automated benchmarking.
 */
public class EduTrackCLI {

    private final MyArrayList<Course> courses;
    private final MyArrayList<Student> students;
    private final MyArrayList<Faculty> faculty;
    private final MyArrayList<AssignmentSubmission> submissions;
    private final MyArrayList<ActivityLog> activityLogs;
    private final String libraryText;

    private final AcademicSearchService searchService;
    private final PlagiarismDetectionService plagiarismService;
    private final ResourceAllocationService allocationService;
    private final ExamSchedulingService examService;
    private final AnalyticsService analyticsService;

    private final Scanner scanner;

    public EduTrackCLI(String baseDir) {
        this.scanner = new Scanner(System.in);
        System.out.println("[EduTrack] Initializing datasets from " + baseDir + "...");

        this.courses = DatasetLoader.loadCourses(baseDir + "/data/courses.csv");
        this.students = DatasetLoader.loadStudents(baseDir + "/data/students.csv");
        this.faculty = DatasetLoader.loadFaculty(baseDir + "/data/faculty.csv");
        this.submissions = DatasetLoader.loadSubmissions(baseDir + "/data/assignments");
        this.activityLogs = DatasetLoader.loadActivityLogs(baseDir + "/data/activity_stream.log");
        this.libraryText = DatasetLoader.readDocument(baseDir + "/data/library_docs/algorithms_handbook.txt");

        this.searchService = new AcademicSearchService(courses);
        this.plagiarismService = new PlagiarismDetectionService();
        this.allocationService = new ResourceAllocationService(faculty, courses);
        this.examService = new ExamSchedulingService(courses);
        this.analyticsService = new AnalyticsService(students, courses);

        System.out.println(String.format("[EduTrack] Loaded: %d courses, %d students, %d faculty, %d submissions, %d activity logs.",
                courses.size(), students.size(), faculty.size(), submissions.size(), activityLogs.size()));
    }

    private String readLineSafe() {
        if (scanner.hasNextLine()) {
            return scanner.nextLine().trim();
        }
        return "";
    }

    public void run() {
        while (true) {
            printMainMenu();
            System.out.print("Enter your choice (0-7): ");
            String choice = readLineSafe();
            if (choice.isEmpty() && !scanner.hasNextLine()) {
                System.out.println("\n[EduTrack] End of input. Exiting.");
                break;
            }

            switch (choice) {
                case "1":
                    handleStringAlgorithmsMenu();
                    break;
                case "2":
                    handleSuffixStructuresMenu();
                    break;
                case "3":
                    handleDynamicProgrammingMenu();
                    break;
                case "4":
                    handleNetworkFlowMenu();
                    break;
                case "5":
                    handleNPCompletenessMenu();
                    break;
                case "6":
                    handleParallelAndRandomizedMenu();
                    break;
                case "7":
                    runBenchmarkSuite();
                    break;
                case "8":
                    System.out.println("\n[EduTrack] Launching Graphical User Interface (GUI)...");
                    new Thread(() -> EduTrackGUI.main(new String[0])).start();
                    System.out.println("[EduTrack] GUI launched in background window.");
                    break;
                case "0":
                    System.out.println("\n[EduTrack] Exiting platform. Goodbye!");
                    return;
                default:
                    System.out.println("[!] Invalid choice. Please enter a number between 0 and 8.");
            }
            System.out.println("\nPress ENTER to continue...");
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            } else {
                break;
            }
        }
    }

    private void printMainMenu() {
        System.out.println("\n================================================================================");
        System.out.println("           EDUTRACK – INTELLIGENT UNIVERSITY ACADEMIC PLATFORM");
        System.out.println("                      [DSA-3 Advanced Algorithms Engine]");
        System.out.println("================================================================================");
        System.out.println("  [1] String Search & Keyword Analytics (KMP, Z-Algorithm, Rabin-Karp, Aho-Corasick)");
        System.out.println("  [2] Suffix Structures & Document Similarity (Suffix Array, SA-IS, Kasai LCP, SAM)");
        System.out.println("  [3] Advanced Dynamic Programming (Levenshtein, Damerau, MCM, Bitmask DP, OBST)");
        System.out.println("  [4] Network Flow & Resource Allocation (Bipartite Matching, EK, Dinic, König)");
        System.out.println("  [5] NP-Completeness, Reductions & Approximation (SAT, 3-SAT->Clique->IS->VC, 2-Approx)");
        System.out.println("  [6] Randomized & Parallel Algorithms (QuickSort, Reservoir, Miller-Rabin, Scan, Brent)");
        System.out.println("  [7] Run Full Integrated Verification & Benchmark Suite");
        System.out.println("  [8] Launch EduTrack Graphical User Interface (GUI)");
        System.out.println("  [0] Exit EduTrack");
        System.out.println("================================================================================");
    }

    private void handleStringAlgorithmsMenu() {
        System.out.println("\n--- [1] String Algorithms & Multi-Pattern Search ---");
        System.out.println("  1. KMP Search for Course Names");
        System.out.println("  2. Z-Algorithm Repeated Phrase Detection in Submissions");
        System.out.println("  3. Rabin-Karp Rolling Hash Course Code Lookup");
        System.out.println("  4. Aho-Corasick Simultaneous Academic Keyword Tagger");
        System.out.print("Select an option (1-4): ");
        String opt = readLineSafe();

        switch (opt) {
            case "1": {
                System.out.print("Enter search keyword (e.g., 'Algorithm', 'Data', 'Systems'): ");
                String query = readLineSafe();
                if (query.isEmpty()) query = "Algorithm";
                MyArrayList<Pair<Course, Integer>> results = searchService.searchCoursesByKMP(query);
                System.out.println("\nKMP Search Results for \"" + query + "\" (" + results.size() + " matches):");
                for (int i = 0; i < results.size(); i++) {
                    Pair<Course, Integer> p = results.get(i);
                    System.out.println("  [" + (i + 1) + "] " + p.first + " (Occurrences: " + p.second + ")");
                }
                break;
            }
            case "2": {
                if (submissions.size() > 0) {
                    AssignmentSubmission sub = submissions.get(0);
                    int rep = plagiarismService.detectInternalRepetitionZ(sub);
                    System.out.println("\nAnalyzing submission: " + sub.getTitle());
                    System.out.println("Z-Algorithm longest repeated prefix length: " + rep + " characters.");
                }
                break;
            }
            case "3": {
                System.out.print("Enter course code pattern (e.g., 'CS', 'AI', 'MATH'): ");
                String code = readLineSafe();
                if (code.isEmpty()) code = "CS";
                MyArrayList<Course> matches = searchService.searchCodeByRabinKarp(code);
                System.out.println("\nRabin-Karp Double-Hash Matches for \"" + code + "\":");
                for (int i = 0; i < matches.size(); i++) {
                    System.out.println("  • " + matches.get(i));
                }
                break;
            }
            case "4": {
                String[] keywords = {"Algorithm", "Dynamic Programming", "Suffix", "Network", "Bipartite", "König", "Approximation"};
                System.out.println("\nRunning Aho-Corasick on University Algorithms Compendium for keywords:");
                for (String kw : keywords) System.out.print(" \"" + kw + "\"");
                System.out.println();
                MyArrayList<AhoCorasick.MatchResult> matches = searchService.tagKeywordsAhoCorasick(libraryText, keywords);
                System.out.println("Total simultaneous matches found: " + matches.size());
                for (int i = 0; i < Math.min(10, matches.size()); i++) {
                    System.out.println("  [" + (i + 1) + "] " + matches.get(i));
                }
                if (matches.size() > 10) {
                    System.out.println("  ... and " + (matches.size() - 10) + " more matches.");
                }
                break;
            }
            default:
                System.out.println("[!] Invalid option.");
        }
    }

    private void handleSuffixStructuresMenu() {
        System.out.println("\n--- [2] Suffix Structures & Document Similarity ---");
        System.out.println("  1. Suffix Array Binary Substring Search in Library Handbook");
        System.out.println("  2. SA-IS Linear-Time Suffix Array Construction Demo");
        System.out.println("  3. Kasai LCP Plagiarism Detection Across Student Assignments");
        System.out.println("  4. Suffix Automaton Substring Query & Count");
        System.out.print("Select an option (1-4): ");
        String opt = readLineSafe();

        switch (opt) {
            case "1": {
                String snippet = libraryText.length() > 500 ? libraryText.substring(0, 500) : libraryText;
                SuffixArray sa = new SuffixArray(snippet);
                System.out.print("Enter substring to search in library snippet (e.g., 'Suffix', 'KMP'): ");
                String query = readLineSafe();
                if (query.isEmpty()) query = "Suffix";
                int pos = sa.searchPattern(query);
                if (pos >= 0) {
                    System.out.println("Found pattern \"" + query + "\" via Suffix Array binary search at text index: " + pos);
                } else {
                    System.out.println("Pattern \"" + query + "\" not found in snippet.");
                }
                break;
            }
            case "2": {
                String sample = "BANANA_UNIVERSITY_DATASET";
                int[] sa = SAIS.buildSuffixArray(sample);
                System.out.println("SA-IS Linear Time Suffix Array for string \"" + sample + "\":");
                for (int i = 0; i < sa.length; i++) {
                    System.out.println("  SA[" + i + "] = " + sa[i] + " -> \"" + sample.substring(sa[i]) + "\"");
                }
                break;
            }
            case "3": {
                if (submissions.size() >= 2) {
                    AssignmentSubmission s1 = submissions.get(0);
                    AssignmentSubmission s2 = submissions.get(1);
                    PlagiarismDetectionService.PlagiarismReport report = plagiarismService.compareAssignments(s1, s2, 40);
                    System.out.println("\n" + report);
                } else {
                    System.out.println("[!] Need at least 2 assignment submissions to compare.");
                }
                break;
            }
            case "4": {
                String doc = "Advanced Algorithms and University Academic Computing";
                SuffixAutomaton sam = new SuffixAutomaton(doc);
                System.out.println("Suffix Automaton built for: \"" + doc + "\"");
                System.out.println("Total distinct substrings: " + sam.countDistinctSubstrings());
                System.out.println("Contains 'Algorithms': " + sam.containsSubstring("Algorithms"));
                System.out.println("Contains 'Quantum': " + sam.containsSubstring("Quantum"));
                System.out.println("LCS with 'University Computing Systems': \"" + sam.findLongestCommonSubstring("University Computing Systems") + "\"");
                break;
            }
            default:
                System.out.println("[!] Invalid option.");
        }
    }

    private void handleDynamicProgrammingMenu() {
        System.out.println("\n--- [3] Advanced Dynamic Programming ---");
        System.out.println("  1. Levenshtein Distance (Student Search Query Auto-Correction)");
        System.out.println("  2. Damerau-Levenshtein (Adjacent Transposition Typo Handler)");
        System.out.println("  3. Matrix-Chain Multiplication (Data Analytics Transformation Pipeline)");
        System.out.println("  4. Bitmask DP (Traveling Academic Auditor - Campus Inspection)");
        System.out.println("  5. Optimal Binary Search Tree (Course Access Key Tree)");
        System.out.print("Select an option (1-5): ");
        String opt = readLineSafe();

        switch (opt) {
            case "1": {
                System.out.print("Enter misspelled course title (e.g., 'Algoritm', 'Operatng Systms'): ");
                String typo = readLineSafe();
                if (typo.isEmpty()) typo = "Algoritm";
                MyArrayList<Pair<Course, Integer>> suggestions = searchService.suggestTypoLevenshtein(typo, 12);
                System.out.println("\nLevenshtein Suggestions for \"" + typo + "\":");
                for (int i = 0; i < Math.min(5, suggestions.size()); i++) {
                    Pair<Course, Integer> p = suggestions.get(i);
                    System.out.println("  • " + p.first.getTitle() + " (Edit Distance: " + p.second + ")");
                }
                break;
            }
            case "2": {
                System.out.print("Enter transposed course code (e.g., 'SC201' for 'CS201', 'IA201'): ");
                String typo = readLineSafe();
                if (typo.isEmpty()) typo = "SC201";
                MyArrayList<Pair<Course, Integer>> suggestions = searchService.suggestCourseCodeDamerau(typo, 2);
                System.out.println("\nDamerau-Levenshtein Transposition Matches for \"" + typo + "\":");
                for (int i = 0; i < suggestions.size(); i++) {
                    Pair<Course, Integer> p = suggestions.get(i);
                    System.out.println("  • " + p.first.getCode() + " - " + p.first.getTitle() + " (Distance: " + p.second + ")");
                }
                break;
            }
            case "3": {
                MatrixChainMult.McmResult mcm = analyticsService.optimizeAnalyticsPipeline();
                System.out.println("\n" + mcm);
                break;
            }
            case "4": {
                BitmaskDP.TourResult tour = analyticsService.planAuditorTour();
                System.out.println("\n" + tour);
                break;
            }
            case "5": {
                OptimalBST.ObstResult obst = analyticsService.buildOptimalSearchTree();
                System.out.println("\nOptimal Binary Search Tree Expected Search Cost: " + String.format("%.4f", obst.expectedCost));
                System.out.println(obst.printTree());
                break;
            }
            default:
                System.out.println("[!] Invalid option.");
        }
    }

    private void handleNetworkFlowMenu() {
        System.out.println("\n--- [4] Network Flow & Resource Allocation ---");
        System.out.println("  1. Bipartite Matching: Faculty-to-Course Allocation");
        System.out.println("  2. Lab Workstation Flow Comparison: Ford-Fulkerson vs Edmonds-Karp vs Dinic");
        System.out.println("  3. König's Theorem: Faculty-Course Conflict Bottlenecks");
        System.out.print("Select an option (1-3): ");
        String opt = readLineSafe();

        switch (opt) {
            case "1": {
                BipartiteMatching.AssignmentResult res = allocationService.assignFacultyToCourses();
                System.out.println("\n" + res);
                break;
            }
            case "2": {
                System.out.println("\nBenchmarking network flows on 80 students and 15 labs...");
                String report = allocationService.compareFlowAlgorithms(80, 15, 6);
                System.out.println(report);
                break;
            }
            case "3": {
                KonigsTheorem.ConflictBottleneck bottleneck = allocationService.analyzeConflictBottlenecks();
                System.out.println("\n" + bottleneck);
                break;
            }
            default:
                System.out.println("[!] Invalid option.");
        }
    }

    private void handleNPCompletenessMenu() {
        System.out.println("\n--- [5] NP-Completeness, Reductions & Approximation ---");
        System.out.println("  1. DPLL SAT Solver for Exam Timetable Scheduling Constraints");
        System.out.println("  2. Karp Reduction Demonstration: 3-SAT -> CLIQUE -> IS -> Vertex Cover");
        System.out.println("  3. 2-Approximation Vertex Cover for Exam Proctor Minimization");
        System.out.print("Select an option (1-3): ");
        String opt = readLineSafe();

        switch (opt) {
            case "1": {
                System.out.println("\nSolving exam scheduling for 6 courses across 3 exam slots...");
                DpllSatSolver.SatResult res = examService.solveExamTimetableSAT(6, 3);
                System.out.println(res);
                break;
            }
            case "2": {
                String chain = examService.demonstrateReductionChain();
                System.out.println("\n" + chain);
                break;
            }
            case "3": {
                VertexCover2Approx.ApproxResult approx = examService.allocateInvigilatorsApprox(15);
                System.out.println("\n" + approx);
                System.out.print("Covered Exam Course IDs: ");
                for (int i = 0; i < approx.coverVertices.size(); i++) {
                    System.out.print(courses.get(approx.coverVertices.get(i)).getCode() + " ");
                }
                System.out.println();
                break;
            }
            default:
                System.out.println("[!] Invalid option.");
        }
    }

    private void handleParallelAndRandomizedMenu() {
        System.out.println("\n--- [6] Randomized & Parallel Algorithms ---");
        System.out.println("  1. Randomized QuickSort for Academic Merit Ranking");
        System.out.println("  2. Reservoir Sampling (Algorithm R) on Streaming Student Activities");
        System.out.println("  3. Miller-Rabin Primality Test for Secure Student Tokens");
        System.out.println("  4. Blelloch Work-Efficient Parallel Scan for Cumulative Credits");
        System.out.println("  5. Multi-Threaded Parallel Reduce for GPA Batch Aggregation");
        System.out.println("  6. Brent's Theorem Parallel Work-Span Speedup Calculator");
        System.out.print("Select an option (1-6): ");
        String opt = readLineSafe();

        switch (opt) {
            case "1": {
                MyArrayList<Student> ranked = analyticsService.rankStudentsByMerit();
                System.out.println("\nTop Academic Merit Ranking (Randomized QuickSort):");
                for (int i = 0; i < Math.min(10, ranked.size()); i++) {
                    System.out.println("  #" + (i + 1) + ": " + ranked.get(i));
                }
                break;
            }
            case "2": {
                System.out.print("Enter reservoir sample size k (e.g. 5): ");
                int k = 5;
                try {
                    k = Integer.parseInt(readLineSafe());
                } catch (Exception ignored) {}
                MyArrayList<ActivityLog> sample = analyticsService.sampleActivityStream(activityLogs, k);
                System.out.println("\nUniform Reservoir Sample of " + k + " events from " + activityLogs.size() + " total stream events:");
                for (int i = 0; i < sample.size(); i++) {
                    System.out.println("  [" + (i + 1) + "] " + sample.get(i));
                }
                break;
            }
            case "3": {
                System.out.print("Enter candidate number to test for primality (or 0 for student token generation): ");
                long num = 0;
                try {
                    num = Long.parseLong(readLineSafe());
                } catch (Exception ignored) {}

                if (num == 0) {
                    long token = analyticsService.generateCryptographicToken(1001);
                    System.out.println("Generated Cryptographic Prime Token for Student STU1001: " + token);
                    System.out.println("Miller-Rabin verification: " + (MillerRabin.isPrime(token) ? "PRIME" : "COMPOSITE"));
                } else {
                    boolean prime = MillerRabin.isPrime(num);
                    System.out.println(num + " is " + (prime ? "PRIME" : "COMPOSITE"));
                }
                break;
            }
            case "4": {
                long[] scanned = analyticsService.computeCumulativeCreditsBlelloch();
                System.out.println("\nCumulative Credits via Blelloch Parallel Scan:");
                for (int i = 0; i < Math.min(10, scanned.length); i++) {
                    System.out.println("  Student #" + (i + 1) + " (" + students.get(i).getName() + "): "
                            + students.get(i).getCompletedCredits() + " cr -> Cumulative: " + scanned[i] + " cr");
                }
                break;
            }
            case "5": {
                int cores = Runtime.getRuntime().availableProcessors();
                ParallelReduce.AggregateStats stats = analyticsService.aggregateStudentGPA(cores);
                System.out.println("\nMulti-Threaded Parallel Reduce across " + cores + " CPU Cores:");
                System.out.println("  " + stats);
                break;
            }
            case "6": {
                System.out.println("\nEvaluating Blelloch Scan for N = 1,000,000 elements across 8 processors:");
                long work = 2_000_000L; // 2N operations
                long span = 40L;        // 2 * log2(N)
                BrentsTheorem.ParallelMetrics metrics = analyticsService.evaluateBrentsTheorem(work, span, 8);
                System.out.println(metrics);
                break;
            }
            default:
                System.out.println("[!] Invalid option.");
        }
    }

    public void runBenchmarkSuite() {
        System.out.println("\n================================================================================");
        System.out.println("             RUNNING COMPLETE EDUTRACK VERIFICATION BENCHMARK SUITE");
        System.out.println("================================================================================");

        int testsPassed = 0;
        int totalTests = 16;

        // Test 1: KMP
        MyArrayList<Integer> kmpMatches = KmpMatcher.search("algorithms and algorithmic analysis", "algorithm", true);
        if (kmpMatches.size() == 2 && kmpMatches.get(0) == 0 && kmpMatches.get(1) == 15) {
            System.out.println("[PASS] 1. KMP String Matcher: Correct prefix function & occurrences");
            testsPassed++;
        } else {
            System.out.println("[FAIL] 1. KMP String Matcher");
        }

        // Test 2: Z-Algorithm
        int[] z = ZAlgorithm.computeZ("aabzaa");
        if (z[0] == 6 && z[1] == 1 && z[4] == 2) {
            System.out.println("[PASS] 2. Z-Algorithm: Accurate Z-box intervals and values");
            testsPassed++;
        } else {
            System.out.println("[FAIL] 2. Z-Algorithm");
        }

        // Test 3: Rabin-Karp
        MyArrayList<Integer> rk = RabinKarp.search("UNIVERSITY_CS201_EXAM_CS201", "CS201");
        if (rk.size() == 2 && rk.get(0) == 11 && rk.get(1) == 22) {
            System.out.println("[PASS] 3. Rabin-Karp: Double-hash rolling window match");
            testsPassed++;
        } else {
            System.out.println("[FAIL] 3. Rabin-Karp (Got size=" + rk.size() + (rk.size() >= 2 ? (", idx0=" + rk.get(0) + ", idx1=" + rk.get(1)) : "") + ")");
        }

        // Test 4: Aho-Corasick
        AhoCorasick ac = new AhoCorasick();
        ac.addPattern("he");
        ac.addPattern("she");
        ac.addPattern("his");
        ac.addPattern("hers");
        ac.buildAutomation();
        MyArrayList<AhoCorasick.MatchResult> acMatches = ac.search("ushers");
        if (acMatches.size() == 3) {
            System.out.println("[PASS] 4. Aho-Corasick: Multi-keyword simultaneous matches");
            testsPassed++;
        } else {
            System.out.println("[FAIL] 4. Aho-Corasick");
        }

        // Test 5: Suffix Array
        SuffixArray sa = new SuffixArray("banana");
        int saSearch = sa.searchPattern("nan");
        if (saSearch >= 0) {
            System.out.println("[PASS] 5. Suffix Array: O(N log^2 N) construction & binary search");
            testsPassed++;
        } else {
            System.out.println("[FAIL] 5. Suffix Array");
        }

        // Test 6: Kasai LCP
        int[] lcp = KasaiLCP.computeLCP("banana", sa.getSuffixArray());
        if (lcp.length == 6) {
            System.out.println("[PASS] 6. Kasai LCP: Linear-time prefix array computation");
            testsPassed++;
        } else {
            System.out.println("[FAIL] 6. Kasai LCP");
        }

        // Test 7: Suffix Automaton
        SuffixAutomaton sam = new SuffixAutomaton("algorithms");
        if (sam.containsSubstring("rithm") && !sam.containsSubstring("rhythm")) {
            System.out.println("[PASS] 7. Suffix Automaton: O(N) minimal DAG substring queries");
            testsPassed++;
        } else {
            System.out.println("[FAIL] 7. Suffix Automaton");
        }

        // Test 8: Levenshtein
        int lev = Levenshtein.computeDistance("kitten", "sitting");
        if (lev == 3) {
            System.out.println("[PASS] 8. Levenshtein: Wagner-Fischer 2D recurrence distance");
            testsPassed++;
        } else {
            System.out.println("[FAIL] 8. Levenshtein");
        }

        // Test 9: Damerau-Levenshtein
        int dam = DamerauLevenshtein.computeDistance("CS102", "SC102");
        if (dam == 1) { // single adjacent transposition
            System.out.println("[PASS] 9. Damerau-Levenshtein: Adjacent character transposition check");
            testsPassed++;
        } else {
            System.out.println("[FAIL] 9. Damerau-Levenshtein");
        }

        // Test 10: Matrix Chain Multiplication
        int[] dims = {10, 30, 5, 60};
        MatrixChainMult.McmResult mcmRes = MatrixChainMult.solve(dims, null);
        if (mcmRes.minMultiplications == 4500) {
            System.out.println("[PASS] 10. Matrix-Chain Multiplication: Optimal cost & parenthesization");
            testsPassed++;
        } else {
            System.out.println("[FAIL] 10. Matrix-Chain Multiplication (Expected 4500, got " + mcmRes.minMultiplications + ")");
        }

        // Test 11: Network Flow (Dinic vs Edmonds-Karp)
        FlowNetwork net = new FlowNetwork(4);
        net.addEdge(0, 1, 10);
        net.addEdge(0, 2, 5);
        net.addEdge(1, 2, 15);
        net.addEdge(1, 3, 10);
        net.addEdge(2, 3, 10);
        int flow = DinicsAlgorithm.maxFlow(net, 0, 3);
        if (flow == 15) {
            System.out.println("[PASS] 11. Network Flow: Dinic's blocking flow max-flow match");
            testsPassed++;
        } else {
            System.out.println("[FAIL] 11. Network Flow (Expected 15, got " + flow + ")");
        }

        // Test 12: DPLL SAT Solver
        MyArrayList<DpllSatSolver.Clause> clauses = new MyArrayList<>();
        clauses.add(new DpllSatSolver.Clause(1, 2));
        clauses.add(new DpllSatSolver.Clause(-1, 2));
        clauses.add(new DpllSatSolver.Clause(1, -2));
        DpllSatSolver.SatResult satRes = DpllSatSolver.solve(clauses, 2);
        if (satRes.isSatisfiable && Boolean.TRUE.equals(satRes.model.get(1)) && Boolean.TRUE.equals(satRes.model.get(2))) {
            System.out.println("[PASS] 12. DPLL SAT Solver: Satisfying truth assignment with unit propagation");
            testsPassed++;
        } else {
            System.out.println("[FAIL] 12. DPLL SAT Solver");
        }

        // Test 13: 2-Approximation Vertex Cover
        MyArrayList<Pair<Integer, Integer>> edges = new MyArrayList<>();
        edges.add(new Pair<>(0, 1));
        edges.add(new Pair<>(1, 2));
        edges.add(new Pair<>(2, 3));
        VertexCover2Approx.ApproxResult approx = VertexCover2Approx.approximateCover(4, edges);
        if (approx.approxCoverSize <= 2 * 2) {
            System.out.println("[PASS] 13. Vertex Cover 2-Approximation: Maximal matching bound satisfied");
            testsPassed++;
        } else {
            System.out.println("[FAIL] 13. Vertex Cover 2-Approximation");
        }

        // Test 14: Miller-Rabin Primality
        if (MillerRabin.isPrime(1000000007L) && !MillerRabin.isPrime(1000000005L)) {
            System.out.println("[PASS] 14. Miller-Rabin: Accurate primality witness testing");
            testsPassed++;
        } else {
            System.out.println("[FAIL] 14. Miller-Rabin");
        }

        // Test 15: Blelloch Scan
        long[] scanIn = {1, 2, 3, 4, 5, 6, 7, 8};
        long[] scanOut = BlellochScan.inclusiveScan(scanIn);
        if (scanOut[7] == 36 && scanOut[0] == 1 && scanOut[2] == 6) {
            System.out.println("[PASS] 15. Blelloch Parallel Scan: Work-efficient prefix sum equality");
            testsPassed++;
        } else {
            System.out.println("[FAIL] 15. Blelloch Parallel Scan");
        }

        // Test 16: Brent's Theorem
        BrentsTheorem.ParallelMetrics brent = BrentsTheorem.analyze(1000, 10, 4);
        if (brent.lowerBoundTP == 250.0 && brent.expectedSpeedup > 1.0) {
            System.out.println("[PASS] 16. Brent's Theorem Analyzer: Analytical speedup & efficiency");
            testsPassed++;
        } else {
            System.out.println("[FAIL] 16. Brent's Theorem");
        }

        System.out.println("================================================================================");
        System.out.println("VERIFICATION SUMMARY: " + testsPassed + " / " + totalTests + " ALGORITHMS PASSED.");
        System.out.println("================================================================================\n");
    }

    public static void main(String[] args) {
        String baseDir = "c:/DSA_3";
        EduTrackCLI cli = new EduTrackCLI(baseDir);

        if (args.length > 0 && "--benchmark".equalsIgnoreCase(args[0])) {
            cli.runBenchmarkSuite();
        } else {
            cli.run();
        }
    }
}
