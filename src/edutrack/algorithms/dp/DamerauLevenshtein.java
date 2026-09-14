package edutrack.algorithms.dp;

/**
 * Damerau-Levenshtein Edit Distance (Optimal String Alignment).
 * Extends Wagner-Fischer to handle adjacent character transpositions (e.g., "CS102" vs "SC102").
 * Zero java.util.* dependencies.
 */
public class DamerauLevenshtein {

    public static int computeDistance(String s1, String s2) {
        if (s1 == null || s2 == null) return Integer.MAX_VALUE;
        int m = s1.length();
        int n = s2.length();

        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;

                dp[i][j] = Math.min(
                    dp[i - 1][j] + 1,                  // deletion
                    Math.min(
                        dp[i][j - 1] + 1,              // insertion
                        dp[i - 1][j - 1] + cost        // substitution
                    )
                );

                // Adjacent transposition check
                if (i > 1 && j > 1 &&
                    s1.charAt(i - 1) == s2.charAt(j - 2) &&
                    s1.charAt(i - 2) == s2.charAt(j - 1)) {
                    dp[i][j] = Math.min(dp[i][j], dp[i - 2][j - 2] + 1);
                }
            }
        }

        return dp[m][n];
    }
}
