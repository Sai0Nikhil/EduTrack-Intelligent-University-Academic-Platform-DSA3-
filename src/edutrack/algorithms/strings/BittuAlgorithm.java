package edutrack.algorithms.strings;

import edutrack.core.MyArrayList;

/**
 * ============================================================================
 * ✨ BITTU'S ALGORITHM (INVENTION / INNOVATION ALGORITHM)
 * ============================================================================
 * Fast Rolling Bigram Cosine-Vector Filter & Exact String Matching Engine.
 *
 * Invented & Formulated as a Geometric Vector Space Approach to Exact & 
 * High-Speed Candidate Screening in String Matching.
 *
 * Mathematical Foundations:
 * 1. Bigram Frequency Embedding:
 *    Encodes any text into a 65,536-dimensional frequency space:
 *    V(S) in R^{256 x 256}, where key(c1, c2) = (c1 << 8) | c2.
 *
 * 2. Normalized Cosine Angle Screening:
 *    cos(theta) = (W . P) / (||W|| * ||P||)
 *    where P is the static pattern vector and W is the sliding window vector.
 *    For an exact match (and anagram matches), cos(theta) = 1.000000.
 *
 * 3. O(1) Rolling Algebra per Shift:
 *    - Leaving Bigram (L):
 *        dot <- dot - P[L]
 *        ||W||^2 <- ||W||^2 - (2 * count[L] - 1)
 *        count[L] <- count[L] - 1
 *    - Entering Bigram (E):
 *        dot <- dot + P[E]
 *        ||W||^2 <- ||W||^2 + (2 * count[E] + 1)
 *        count[E] <- count[E] + 1
 *
 * 4. Zero Memory Allocation during Scanning:
 *    Eliminates object allocation, hashing, and substring copying via flat
 *    primitive int[] lookup arrays.
 * ============================================================================
 */
public class BittuAlgorithm {

    private static final int ALPHABET = 256;              // Extended ASCII
    private static final int TABLE_SIZE = ALPHABET * ALPHABET; // 65,536
    private static final double COSINE_THRESHOLD = 0.999999999;

    public static class SearchResult {
        public final MyArrayList<Integer> matchPositions;
        public final int screenedInWindows;
        public final int totalWindowsExamined;
        public final double patternMagnitude;
        public final long executionNanos;

        public SearchResult(MyArrayList<Integer> matchPositions, int screenedInWindows,
                            int totalWindowsExamined, double patternMagnitude, long executionNanos) {
            this.matchPositions = matchPositions;
            this.screenedInWindows = screenedInWindows;
            this.totalWindowsExamined = totalWindowsExamined;
            this.patternMagnitude = patternMagnitude;
            this.executionNanos = executionNanos;
        }

        @Override
        public String toString() {
            return String.format("BittuAlgorithm Result: %d match(es), %d candidate windows screened in out of %d (%.2f µs)",
                    matchPositions.size(), screenedInWindows, totalWindowsExamined, executionNanos / 1000.0);
        }
    }

    /** Encode two consecutive characters into a unique 16-bit integer index (0..65535). */
    public static int key(char c1, char c2) {
        return ((c1 & 0xFF) << 8) | (c2 & 0xFF);
    }

    /** Compute flat bigram frequency table for a string. */
    public static int[] computeBigramCounts(String s) {
        int[] counts = new int[TABLE_SIZE];
        for (int i = 0; i + 1 < s.length(); i++) {
            counts[key(s.charAt(i), s.charAt(i + 1))]++;
        }
        return counts;
    }

    /** Check if string contains only ASCII characters <= 255. */
    public static boolean isAscii(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) > 255) return false;
        }
        return true;
    }

    /** Exact character verification on candidate window. */
    private static boolean verifyExact(String text, int start, String pattern) {
        for (int i = 0; i < pattern.length(); i++) {
            if (text.charAt(start + i) != pattern.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Primary Search Method for Bittu's Algorithm.
     * Returns matching starting positions in text.
     */
    public static MyArrayList<Integer> search(String text, String pattern) {
        return searchWithTelemetry(text, pattern).matchPositions;
    }

    /**
     * Search with full mathematical and performance telemetry.
     */
    public static SearchResult searchWithTelemetry(String text, String pattern) {
        long startNanos = System.nanoTime();
        MyArrayList<Integer> matches = new MyArrayList<>();

        if (text == null || pattern == null) {
            return new SearchResult(matches, 0, 0, 0.0, 0);
        }

        int n = text.length();
        int m = pattern.length();

        if (m < 2 || m > n) {
            if (m == 1 && n >= 1) {
                // Single-character base case fallback
                char target = pattern.charAt(0);
                for (int i = 0; i < n; i++) {
                    if (text.charAt(i) == target) matches.add(i);
                }
            }
            return new SearchResult(matches, matches.size(), Math.max(0, n - m + 1), 1.0, System.nanoTime() - startNanos);
        }

        // 1. Build pattern bigram vector & compute ||P||
        int[] patternCounts = computeBigramCounts(pattern);
        long patternMagSq = 0;
        for (int k = 0; k < TABLE_SIZE; k++) {
            int v = patternCounts[k];
            if (v > 0) {
                patternMagSq += (long) v * v;
            }
        }
        double patternMag = Math.sqrt((double) patternMagSq);

        // 2. Initialize first sliding window (0..m-1)
        int[] windowCounts = computeBigramCounts(text.substring(0, m));
        long dot = 0;
        long windowMagSq = 0;

        for (int k = 0; k < TABLE_SIZE; k++) {
            int wv = windowCounts[k];
            if (wv > 0) {
                dot += (long) wv * patternCounts[k];
                windowMagSq += (long) wv * wv;
            }
        }

        int screenedIn = 0;
        int totalWindows = n - m + 1;

        // 3. Slide window across text with O(1) delta updates
        for (int start = 0; start <= n - m; start++) {
            double windowMag = Math.sqrt((double) windowMagSq);
            double cosine = (windowMag == 0 || patternMag == 0) ? 0.0 : ((double) dot) / (windowMag * patternMag);

            // High-precision cosine screening filter
            if (cosine >= COSINE_THRESHOLD) {
                screenedIn++;
                if (verifyExact(text, start, pattern)) {
                    matches.add(start);
                }
            }

            // O(1) rolling update for next window position
            if (start < n - m) {
                int leavingKey = key(text.charAt(start), text.charAt(start + 1));
                int enteringKey = key(text.charAt(start + m - 1), text.charAt(start + m));

                // Remove leaving bigram
                int oldLeave = windowCounts[leavingKey];
                dot -= patternCounts[leavingKey];
                windowMagSq -= (2L * oldLeave - 1);
                windowCounts[leavingKey] = oldLeave - 1;

                // Add entering bigram
                int oldEnter = windowCounts[enteringKey];
                dot += patternCounts[enteringKey];
                windowMagSq += (2L * oldEnter + 1);
                windowCounts[enteringKey] = oldEnter + 1;
            }
        }

        long elapsedNanos = System.nanoTime() - startNanos;
        return new SearchResult(matches, screenedIn, totalWindows, patternMag, elapsedNanos);
    }
}
