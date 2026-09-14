package edutrack.algorithms.strings;

import edutrack.core.MyArrayList;

/**
 * Knuth-Morris-Pratt (KMP) String Matching Algorithm.
 * Computes prefix failure function (pi) in O(m) time and performs
 * text search in O(n) time with zero backtracking in the text.
 */
public class KmpMatcher {

    /**
     * Computes the failure function (longest proper prefix that is also suffix).
     */
    public static int[] computePi(String pattern) {
        int m = pattern.length();
        int[] pi = new int[m];
        int k = 0;
        for (int q = 1; q < m; q++) {
            while (k > 0 && pattern.charAt(k) != pattern.charAt(q)) {
                k = pi[k - 1];
            }
            if (pattern.charAt(k) == pattern.charAt(q)) {
                k++;
            }
            pi[q] = k;
        }
        return pi;
    }

    /**
     * Finds all occurrence indices of pattern in text.
     * Case-insensitive matching optional.
     */
    public static MyArrayList<Integer> search(String text, String pattern, boolean ignoreCase) {
        MyArrayList<Integer> occurrences = new MyArrayList<>();
        if (text == null || pattern == null) return occurrences;
        int n = text.length();
        int m = pattern.length();
        if (m == 0 || n < m) return occurrences;

        String t = ignoreCase ? text.toLowerCase() : text;
        String p = ignoreCase ? pattern.toLowerCase() : pattern;

        int[] pi = computePi(p);
        int q = 0; // characters matched

        for (int i = 0; i < n; i++) {
            while (q > 0 && p.charAt(q) != t.charAt(i)) {
                q = pi[q - 1];
            }
            if (p.charAt(q) == t.charAt(i)) {
                q++;
            }
            if (q == m) {
                occurrences.add(i - m + 1);
                q = pi[q - 1];
            }
        }
        return occurrences;
    }

    public static MyArrayList<Integer> search(String text, String pattern) {
        return search(text, pattern, false);
    }
}
