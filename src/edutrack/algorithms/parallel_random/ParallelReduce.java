package edutrack.algorithms.parallel_random;

/**
 * Multi-Threaded Parallel Tree Reduction for large student-performance batch aggregation.
 * Divides array across worker threads to compute sum, min, max, and averages.
 * Demonstrates parallel work T1 = O(N) and span T_infinity = O(log N).
 * Zero java.util.* dependencies.
 */
public class ParallelReduce {

    public static class AggregateStats {
        public long count;
        public double sum;
        public double min;
        public double max;
        public double average;

        public AggregateStats(long count, double sum, double min, double max) {
            this.count = count;
            this.sum = sum;
            this.min = min;
            this.max = max;
            this.average = (count > 0) ? (sum / count) : 0.0;
        }

        @Override
        public String toString() {
            return "Count: " + count +
                   " | Sum: " + String.format("%.2f", sum) +
                   " | Avg: " + String.format("%.2f", average) +
                   " | Min: " + String.format("%.2f", min) +
                   " | Max: " + String.format("%.2f", max);
        }
    }

    private static class WorkerThread extends Thread {
        private final double[] data;
        private final int start;
        private final int end;
        double localSum;
        double localMin = Double.MAX_VALUE;
        double localMax = -Double.MAX_VALUE;

        WorkerThread(double[] data, int start, int end) {
            this.data = data;
            this.start = start;
            this.end = end;
        }

        @Override
        public void run() {
            double s = 0;
            double mn = Double.MAX_VALUE;
            double mx = -Double.MAX_VALUE;
            for (int i = start; i < end; i++) {
                double val = data[i];
                s += val;
                if (val < mn) mn = val;
                if (val > mx) mx = val;
            }
            localSum = s;
            localMin = mn;
            localMax = mx;
        }
    }

    /**
     * Executes parallel reduction across available CPU cores.
     */
    public static AggregateStats reduce(double[] data, int numThreads) {
        int n = data.length;
        if (n == 0) return new AggregateStats(0, 0, 0, 0);

        int threadsToUse = Math.max(1, Math.min(numThreads, n));
        WorkerThread[] workers = new WorkerThread[threadsToUse];

        int chunkSize = (n + threadsToUse - 1) / threadsToUse;
        for (int t = 0; t < threadsToUse; t++) {
            int start = t * chunkSize;
            int end = Math.min(start + chunkSize, n);
            workers[t] = new WorkerThread(data, start, end);
            workers[t].start();
        }

        double totalSum = 0;
        double totalMin = Double.MAX_VALUE;
        double totalMax = -Double.MAX_VALUE;

        for (int t = 0; t < threadsToUse; t++) {
            try {
                workers[t].join();
                totalSum += workers[t].localSum;
                if (workers[t].localMin < totalMin) totalMin = workers[t].localMin;
                if (workers[t].localMax > totalMax) totalMax = workers[t].localMax;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return new AggregateStats(n, totalSum, totalMin, totalMax);
    }
}
