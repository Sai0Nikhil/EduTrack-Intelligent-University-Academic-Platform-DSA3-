package edutrack.algorithms.dp;

/**
 * Optimal Binary Search Tree (OBST) Dynamic Programming in O(N^3).
 * Organizes frequently accessed course and department search keys
 * to minimize the expected search time based on query frequencies.
 * Zero java.util.* dependencies.
 */
public class OptimalBST {

    public static class ObstResult {
        public double expectedCost;
        public int[][] root;
        public String[] keys;

        public ObstResult(double expectedCost, int[][] root, String[] keys) {
            this.expectedCost = expectedCost;
            this.root = root;
            this.keys = keys;
        }

        public String printTree() {
            StringBuilder sb = new StringBuilder();
            buildTreeString(root, 1, keys.length, 0, "Root", sb);
            return sb.toString();
        }

        private void buildTreeString(int[][] root, int i, int j, int parent, String side, StringBuilder sb) {
            if (i > j) return;
            int r = root[i][j];
            String keyName = (keys != null && r - 1 < keys.length) ? keys[r - 1] : ("k" + r);
            sb.append("  ").append(side).append(": ").append(keyName);
            if (parent != 0) {
                sb.append(" (Child of ").append(keys[parent - 1]).append(")");
            }
            sb.append("\n");
            buildTreeString(root, i, r - 1, r, "Left", sb);
            buildTreeString(root, r + 1, j, r, "Right", sb);
        }
    }

    /**
     * Solves OBST for keys 1..n.
     * p[1..n]: probability of searching key i.
     * q[0..n]: probability of searching dummy keys (misses).
     */
    public static ObstResult solve(double[] p, double[] q, String[] keys) {
        int n = p.length - 1;
        double[][] e = new double[n + 2][n + 2];
        double[][] w = new double[n + 2][n + 2];
        int[][] root = new int[n + 1][n + 1];

        for (int i = 1; i <= n + 1; i++) {
            e[i][i - 1] = q[i - 1];
            w[i][i - 1] = q[i - 1];
        }

        for (int l = 1; l <= n; l++) { // chain length
            for (int i = 1; i <= n - l + 1; i++) {
                int j = i + l - 1;
                e[i][j] = Double.MAX_VALUE;
                w[i][j] = w[i][j - 1] + p[j] + q[j];

                for (int r = i; r <= j; r++) {
                    double t = e[i][r - 1] + e[r + 1][j] + w[i][j];
                    if (t < e[i][j]) {
                        e[i][j] = t;
                        root[i][j] = r;
                    }
                }
            }
        }

        return new ObstResult(e[1][n], root, keys);
    }
}
