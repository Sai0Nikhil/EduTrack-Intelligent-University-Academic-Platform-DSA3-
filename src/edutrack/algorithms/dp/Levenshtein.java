package edutrack.algorithms.dp;

import edutrack.core.MyArrayList;
import edutrack.core.Pair;

/**
 * Wagner-Fischer Dynamic Programming algorithm for Levenshtein Edit Distance.
 * Computes minimum edit operations (Insert, Delete, Substitute) and exposes
 * the full 2D DP matrix and reconstructed alignment path for visual rendering.
 * Zero java.util.* dependencies.
 */
public class Levenshtein {

    public static class DpTraceResult {
        public final int distance;
        public final int[][] dpTable;
        public final String source;
        public final String target;
        public final MyArrayList<Pair<Integer, Integer>> backtracePath;
        public final MyArrayList<String> operations;

        public DpTraceResult(int distance, int[][] dpTable, String source, String target,
                             MyArrayList<Pair<Integer, Integer>> backtracePath, MyArrayList<String> operations) {
            this.distance = distance;
            this.dpTable = dpTable;
            this.source = source;
            this.target = target;
            this.backtracePath = backtracePath;
            this.operations = operations;
        }

        public String printMatrix() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%-6s", ""));
            sb.append(String.format("%-4s", "ε"));
            for (int j = 0; j < target.length(); j++) {
                sb.append(String.format("%-4s", target.charAt(j)));
            }
            sb.append("\n");

            for (int i = 0; i <= source.length(); i++) {
                sb.append(String.format("%-6s", i == 0 ? "ε" : String.valueOf(source.charAt(i - 1))));
                for (int j = 0; j <= target.length(); j++) {
                    sb.append(String.format("%-4d", dpTable[i][j]));
                }
                sb.append("\n");
            }
            return sb.toString();
        }

        public String printAlignment() {
            StringBuilder sb = new StringBuilder();
            for (int i = operations.size() - 1; i >= 0; i--) {
                sb.append(operations.get(i));
                if (i > 0) sb.append(" -> ");
            }
            return sb.toString();
        }
    }

    /**
     * Computes the Levenshtein distance between source and target.
     */
    public static int computeDistance(String s1, String s2) {
        if (s1 == null || s2 == null) return Integer.MAX_VALUE;
        return computeWithTrace(s1, s2).distance;
    }

    /**
     * Computes distance and constructs the full 2D matrix and backtrace path.
     */
    public static DpTraceResult computeWithTrace(String s1, String s2) {
        if (s1 == null) s1 = "";
        if (s2 == null) s2 = "";

        int m = s1.length();
        int n = s2.length();

        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;

        for (int i = 1; i <= m; i++) {
            char c1 = s1.charAt(i - 1);
            for (int j = 1; j <= n; j++) {
                char c2 = s2.charAt(j - 1);
                if (c1 == c2) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    int insertCost = dp[i][j - 1] + 1;
                    int deleteCost = dp[i - 1][j] + 1;
                    int replaceCost = dp[i - 1][j - 1] + 1;
                    dp[i][j] = Math.min(insertCost, Math.min(deleteCost, replaceCost));
                }
            }
        }

        // Backtrack path from (m, n) to (0, 0)
        MyArrayList<Pair<Integer, Integer>> path = new MyArrayList<>();
        MyArrayList<String> ops = new MyArrayList<>();

        int currI = m;
        int currJ = n;
        path.add(new Pair<>(currI, currJ));

        while (currI > 0 || currJ > 0) {
            if (currI > 0 && currJ > 0) {
                char c1 = s1.charAt(currI - 1);
                char c2 = s2.charAt(currJ - 1);
                int cost = (c1 == c2) ? 0 : 1;
                if (dp[currI][currJ] == dp[currI - 1][currJ - 1] + cost) {
                    if (cost == 0) {
                        ops.add("MATCH '" + c1 + "'");
                    } else {
                        ops.add("SUBSTITUTE '" + c1 + "' -> '" + c2 + "'");
                    }
                    currI--;
                    currJ--;
                    path.add(new Pair<>(currI, currJ));
                    continue;
                }
            }

            if (currI > 0 && dp[currI][currJ] == dp[currI - 1][currJ] + 1) {
                ops.add("DELETE '" + s1.charAt(currI - 1) + "'");
                currI--;
                path.add(new Pair<>(currI, currJ));
            } else if (currJ > 0 && dp[currI][currJ] == dp[currI][currJ - 1] + 1) {
                ops.add("INSERT '" + s2.charAt(currJ - 1) + "'");
                currJ--;
                path.add(new Pair<>(currI, currJ));
            } else {
                currI--;
                currJ--;
                path.add(new Pair<>(currI, currJ));
            }
        }

        return new DpTraceResult(dp[m][n], dp, s1, s2, path, ops);
    }
}
