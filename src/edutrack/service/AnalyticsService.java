package edutrack.service;

import edutrack.algorithms.dp.BitmaskDP;
import edutrack.algorithms.dp.MatrixChainMult;
import edutrack.algorithms.dp.OptimalBST;
import edutrack.algorithms.parallel_random.BlellochScan;
import edutrack.algorithms.parallel_random.BrentsTheorem;
import edutrack.algorithms.parallel_random.MillerRabin;
import edutrack.algorithms.parallel_random.ParallelReduce;
import edutrack.algorithms.parallel_random.RandomizedQuickSort;
import edutrack.algorithms.parallel_random.ReservoirSampling;
import edutrack.core.MyArrayList;
import edutrack.model.ActivityLog;
import edutrack.model.Course;
import edutrack.model.Student;

/**
 * Service for DP optimizations, parallel processing, and randomized algorithms.
 * Zero java.util.* dependencies.
 */
public class AnalyticsService {
    private final MyArrayList<Student> students;
    private final MyArrayList<Course> courses;

    public AnalyticsService(MyArrayList<Student> students, MyArrayList<Course> courses) {
        this.students = students;
        this.courses = courses;
    }

    /**
     * Optimizes a pipeline of student analytics matrix transformations via MCM.
     */
    public MatrixChainMult.McmResult optimizeAnalyticsPipeline() {
        int n = Math.min(5, courses.size());
        int[] p = new int[n + 1];
        String[] names = new String[n];

        p[0] = courses.get(0).getMatrixDimensionRows();
        for (int i = 0; i < n; i++) {
            Course c = courses.get(i);
            names[i] = "M_" + c.getCode();
            p[i + 1] = c.getMatrixDimensionCols();
        }

        return MatrixChainMult.solve(p, names);
    }

    /**
     * Finds the minimum transit tour for the Traveling Academic Auditor across campus departments.
     */
    public BitmaskDP.TourResult planAuditorTour() {
        String[] departments = {
            "Computer Science", "Artificial Intelligence", "Data Science",
            "Mathematics", "Electronics", "Cybersecurity"
        };
        int n = departments.length;
        int[][] dist = {
            {0, 10, 15, 20, 25, 30},
            {10, 0, 35, 25, 18, 22},
            {15, 35, 0, 30, 28, 14},
            {20, 25, 30, 0, 12, 16},
            {25, 18, 28, 12, 0, 24},
            {30, 22, 14, 16, 24, 0}
        };

        return BitmaskDP.solveTSP(dist, departments);
    }

    /**
     * Builds an Optimal Binary Search Tree for frequently searched academic keys.
     */
    public OptimalBST.ObstResult buildOptimalSearchTree() {
        String[] keys = {"AI", "Algorithms", "DataStructures", "OperatingSystems", "Security"};
        double[] p = {0.0, 0.25, 0.20, 0.05, 0.20, 0.10}; // 1-indexed
        double[] q = {0.05, 0.05, 0.03, 0.02, 0.03, 0.02}; // 0-indexed

        return OptimalBST.solve(p, q, keys);
    }

    /**
     * Ranks students by academic merit using Randomized QuickSort.
     */
    public MyArrayList<Student> rankStudentsByMerit() {
        MyArrayList<Student> ranked = new MyArrayList<>(students.size());
        for (int i = 0; i < students.size(); i++) {
            ranked.add(students.get(i));
        }
        RandomizedQuickSort.sort(ranked);
        return ranked;
    }

    /**
     * Uniformly samples k continuous activity events using Reservoir Sampling.
     */
    public MyArrayList<ActivityLog> sampleActivityStream(MyArrayList<ActivityLog> stream, int k) {
        ReservoirSampling<ActivityLog> sampler = new ReservoirSampling<>(k);
        for (int i = 0; i < stream.size(); i++) {
            sampler.processNext(stream.get(i));
        }
        return sampler.getSample();
    }

    /**
     * Generates and verifies a secure cryptographic student authentication token via Miller-Rabin.
     */
    public long generateCryptographicToken(long baseId) {
        long candidate = Math.abs(baseId) * 100003L + 7L;
        return MillerRabin.nextPrime(candidate);
    }

    /**
     * Computes cumulative academic credits using Blelloch Work-Efficient Parallel Scan.
     */
    public long[] computeCumulativeCreditsBlelloch() {
        int n = students.size();
        long[] credits = new long[n];
        for (int i = 0; i < n; i++) {
            credits[i] = students.get(i).getCompletedCredits();
        }
        return BlellochScan.inclusiveScan(credits);
    }

    /**
     * Computes multi-threaded parallel aggregation for student GPAs.
     */
    public ParallelReduce.AggregateStats aggregateStudentGPA(int numThreads) {
        int n = students.size();
        double[] gpas = new double[n];
        for (int i = 0; i < n; i++) {
            gpas[i] = students.get(i).getGpa();
        }
        return ParallelReduce.reduce(gpas, numThreads);
    }

    /**
     * Analyzes parallel runtime speedup ceiling using Brent's Theorem.
     */
    public BrentsTheorem.ParallelMetrics evaluateBrentsTheorem(long workT1, long spanTinf, int processors) {
        return BrentsTheorem.analyze(workT1, spanTinf, processors);
    }
}
