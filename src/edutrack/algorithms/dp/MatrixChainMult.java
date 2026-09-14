package edutrack.algorithms.dp;

/**
 * Matrix-Chain Multiplication (MCM) using Dynamic Programming in O(N^3).
 * Optimizes the sequence of academic data transformation and projection matrices
 * to minimize total scalar multiplications.
 * Zero java.util.* dependencies.
 */
public class MatrixChainMult {

    public static class McmResult {
        public long minMultiplications;
        public String optimalOrder;

        public McmResult(long minMultiplications, String optimalOrder) {
            this.minMultiplications = minMultiplications;
            this.optimalOrder = optimalOrder;
        }

        @Override
        public String toString() {
            return "Min Multiplications: " + minMultiplications + " | Optimal Order: " + optimalOrder;
        }
    }

    /**
     * Solves MCM for matrices where matrix i has dimension p[i-1] x p[i].
     * Length of p is n + 1 where n is the number of matrices.
     */
    public static McmResult solve(int[] p, String[] matrixNames) {
        int n = p.length - 1;
        long[][] m = new long[n + 1][n + 1];
        int[][] s = new int[n + 1][n + 1];

        for (int i = 1; i <= n; i++) {
            m[i][i] = 0;
        }

        for (int l = 2; l <= n; l++) { // l is chain length
            for (int i = 1; i <= n - l + 1; i++) {
                int j = i + l - 1;
                m[i][j] = Long.MAX_VALUE;

                for (int k = i; k <= j - 1; k++) {
                    long cost = m[i][k] + m[k + 1][j] + (long) p[i - 1] * p[k] * p[j];
                    if (cost < m[i][j]) {
                        m[i][j] = cost;
                        s[i][j] = k;
                    }
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        buildOptimalString(s, 1, n, matrixNames, sb);

        return new McmResult(m[1][n], sb.toString());
    }

    private static void buildOptimalString(int[][] s, int i, int j, String[] names, StringBuilder sb) {
        if (i == j) {
            String name = (names != null && i - 1 < names.length) ? names[i - 1] : ("M" + i);
            sb.append(name);
        } else {
            sb.append("(");
            buildOptimalString(s, i, s[i][j], names, sb);
            sb.append(" x ");
            buildOptimalString(s, s[i][j] + 1, j, names, sb);
            sb.append(")");
        }
    }
}
