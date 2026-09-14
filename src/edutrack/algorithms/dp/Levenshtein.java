package edutrack.algorithms.dp;

/**
 * Wagner-Fischer Dynamic Programming algorithm for Levenshtein Edit Distance.
 * Used in EduTrack to correct misspelled student search queries against course catalogs.
 * Zero java.util.* dependencies.
 */
public class Levenshtein {

    /**
     * Computes the Levenshtein distance between source and target.
     * Operations: Insertion (+1), Deletion (+1), Substitution (+1).
     */
    public static int computeDistance(String s1, String s2) {
        if (s1 == null || s2 == null) return Integer.MAX_VALUE;
        int m = s1.length();
        int n = s2.length();

        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j;
        }

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

        return dp[m][n];
    }
}
