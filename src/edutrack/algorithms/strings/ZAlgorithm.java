package edutrack.algorithms.strings;

import edutrack.core.MyArrayList;

/**
 * Linear-time Z-Algorithm for string matching and repeated phrase detection.
 * Z[i] is the length of the longest substring starting from s[i]
 * which is also a prefix of s.
 */
public class ZAlgorithm {

    /**
     * Computes the Z-array for string s in O(|s|) time.
     */
    public static int[] computeZ(String s) {
        int n = s.length();
        int[] z = new int[n];
        if (n == 0) return z;
        z[0] = n;

        int l = 0, r = 0;
        for (int i = 1; i < n; i++) {
            if (i <= r) {
                z[i] = Math.min(r - i + 1, z[i - l]);
            }
            while (i + z[i] < n && s.charAt(z[i]) == s.charAt(i + z[i])) {
                z[i]++;
            }
            if (i + z[i] - 1 > r) {
                l = i;
                r = i + z[i] - 1;
            }
        }
        return z;
    }

    /**
     * Searches for pattern in text using Z-function on pattern + "$" + text.
     */
    public static MyArrayList<Integer> search(String text, String pattern) {
        MyArrayList<Integer> matches = new MyArrayList<>();
        if (text == null || pattern == null) return matches;
        int m = pattern.length();
        int n = text.length();
        if (m == 0 || n < m) return matches;

        String combined = pattern + "$" + text;
        int[] z = computeZ(combined);

        for (int i = m + 1; i < combined.length(); i++) {
            if (z[i] >= m) {
                matches.add(i - (m + 1));
            }
        }
        return matches;
    }

    /**
     * Detects the longest repeated prefix or internal repeated block in text.
     */
    public static int findLongestRepeatedPrefix(String text) {
        int[] z = computeZ(text);
        int maxLen = 0;
        for (int i = 1; i < z.length; i++) {
            if (z[i] > maxLen) {
                maxLen = z[i];
            }
        }
        return maxLen;
    }
}
