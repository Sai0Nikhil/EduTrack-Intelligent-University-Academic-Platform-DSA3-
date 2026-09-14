package edutrack.algorithms.parallel_random;

/**
 * Brent's Theorem Analyzer (Work-Span Model and Parallel Speedup Bounds).
 * Evaluates parallel performance of academic analytics pipelines given Work (T1) and Span (T_inf):
 *   T1 / P <= T_P <= (T1 - T_inf) / P + T_inf
 * Speedup S_P = T1 / T_P
 * Efficiency E_P = S_P / P
 * Zero java.util.* dependencies.
 */
public class BrentsTheorem {

    public static class ParallelMetrics {
        public long workT1;
        public long spanTinf;
        public int processorsP;
        public double lowerBoundTP;
        public double upperBoundTP;
        public double maxTheoreticalSpeedup;
        public double expectedSpeedup;
        public double parallelEfficiency;

        public ParallelMetrics(long workT1, long spanTinf, int processorsP) {
            this.workT1 = workT1;
            this.spanTinf = spanTinf;
            this.processorsP = processorsP;

            this.lowerBoundTP = (double) workT1 / processorsP;
            this.upperBoundTP = ((double) (workT1 - spanTinf) / processorsP) + spanTinf;

            // Average or conservative estimate
            double estimatedTP = upperBoundTP;
            this.expectedSpeedup = (double) workT1 / estimatedTP;
            this.maxTheoreticalSpeedup = (double) workT1 / spanTinf;
            this.parallelEfficiency = (expectedSpeedup / processorsP) * 100.0;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("=== Brent's Theorem Parallel Performance Analysis ===\n");
            sb.append(String.format("  Total Work (T1)          : %,d operations\n", workT1));
            sb.append(String.format("  Critical Span (T_inf)    : %,d steps\n", spanTinf));
            sb.append(String.format("  Processors / Cores (P)   : %d\n", processorsP));
            sb.append(String.format("  Parallel Time Bound (T_P): [%.2f, %.2f] cycles\n", lowerBoundTP, upperBoundTP));
            sb.append(String.format("  Expected Speedup (S_P)   : %.2fx (Ideal linear: %dx)\n", expectedSpeedup, processorsP));
            sb.append(String.format("  Parallel Efficiency (E_P): %.2f%%\n", parallelEfficiency));
            sb.append(String.format("  Asymptotic Speedup Ceiling (T1 / T_inf): %.2fx\n", maxTheoreticalSpeedup));
            return sb.toString();
        }
    }

    public static ParallelMetrics analyze(long work, long span, int processors) {
        return new ParallelMetrics(work, span, processors);
    }
}
