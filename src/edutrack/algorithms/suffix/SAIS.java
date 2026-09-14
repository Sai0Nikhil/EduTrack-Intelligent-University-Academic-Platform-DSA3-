package edutrack.algorithms.suffix;

/**
 * SA-IS (Suffix Array Induced Sorting) Algorithm.
 * Computes the Suffix Array in linear time O(N) using:
 * 1. S/L type character classification.
 * 2. Left-Most S-type (LMS) character detection.
 * 3. Two-phase induced sorting (L-type then S-type) to achieve O(N) worst-case time.
 * Zero java.util.* dependencies.
 */
public class SAIS {

    private static final boolean S_TYPE = false;
    private static final boolean L_TYPE = true;

    /**
     * Public entry point: computes suffix array for a standard string in O(N).
     */
    public static int[] buildSuffixArray(String text) {
        int n = text.length();
        if (n == 0) return new int[0];
        if (n == 1) return new int[]{0};

        // Represent text as integer array with sentinel 0 at the end
        int[] s = new int[n + 1];
        for (int i = 0; i < n; i++) {
            s[i] = text.charAt(i) + 1; // shift so positive
        }
        s[n] = 0; // sentinel

        int maxChar = 0;
        for (int val : s) {
            if (val > maxChar) maxChar = val;
        }

        int[] sa = new int[n + 1];
        sais(s, sa, n + 1, maxChar + 1);

        // Remove sentinel at sa[0]
        int[] result = new int[n];
        System.arraycopy(sa, 1, result, 0, n);
        return result;
    }

    private static void sais(int[] s, int[] sa, int n, int alphabetSize) {
        boolean[] t = new boolean[n]; // types
        t[n - 1] = S_TYPE;

        for (int i = n - 2; i >= 0; i--) {
            if (s[i] < s[i + 1]) {
                t[i] = S_TYPE;
            } else if (s[i] > s[i + 1]) {
                t[i] = L_TYPE;
            } else {
                t[i] = t[i + 1];
            }
        }

        int[] buckets = new int[alphabetSize];
        for (int i = 0; i < n; i++) {
            buckets[s[i]]++;
        }

        // Find LMS characters
        int[] lms = new int[n];
        int lmsCount = 0;
        for (int i = 1; i < n; i++) {
            if (t[i] == S_TYPE && t[i - 1] == L_TYPE) {
                lms[lmsCount++] = i;
            }
        }

        for (int i = 0; i < n; i++) sa[i] = -1;

        // Place LMS into bucket tails
        int[] bucketTails = getBucketTails(buckets, alphabetSize);
        for (int i = 0; i < lmsCount; i++) {
            int pos = lms[i];
            int b = s[pos];
            sa[bucketTails[b]--] = pos;
        }

        // Induce L and S
        induceSort(s, sa, t, buckets, alphabetSize, n);

        // Compact LMS substrings to see if recursion is needed
        int[] lmsNames = new int[n];
        for (int i = 0; i < n; i++) lmsNames[i] = -1;

        int name = 0;
        int prevLms = -1;
        for (int i = 0; i < n; i++) {
            int pos = sa[i];
            if (pos > 0 && t[pos] == S_TYPE && t[pos - 1] == L_TYPE) {
                if (prevLms != -1 && !isLmsEqual(s, t, prevLms, pos)) {
                    name++;
                }
                lmsNames[pos] = name;
                prevLms = pos;
            }
        }

        int[] reducedS = new int[lmsCount];
        int j = 0;
        for (int i = 0; i < n; i++) {
            if (lmsNames[i] >= 0) {
                reducedS[j++] = lmsNames[i];
            }
        }

        int[] reducedSa = new int[lmsCount];
        if (name + 1 < lmsCount) {
            // Recursive step
            sais(reducedS, reducedSa, lmsCount, name + 1);
        } else {
            // Unique LMS substrings: direct bucket
            for (int i = 0; i < lmsCount; i++) {
                reducedSa[reducedS[i]] = i;
            }
        }

        // Reconstruct order of LMS suffixes
        for (int i = 0; i < n; i++) sa[i] = -1;
        bucketTails = getBucketTails(buckets, alphabetSize);
        for (int i = lmsCount - 1; i >= 0; i--) {
            int lmsIndex = lms[reducedSa[i]];
            int b = s[lmsIndex];
            sa[bucketTails[b]--] = lmsIndex;
        }

        // Final induce sort
        induceSort(s, sa, t, buckets, alphabetSize, n);
    }

    private static void induceSort(int[] s, int[] sa, boolean[] t, int[] buckets, int alphabetSize, int n) {
        int[] bucketHeads = getBucketHeads(buckets, alphabetSize);
        for (int i = 0; i < n; i++) {
            int j = sa[i] - 1;
            if (j >= 0 && t[j] == L_TYPE) {
                int b = s[j];
                sa[bucketHeads[b]++] = j;
            }
        }

        int[] bucketTails = getBucketTails(buckets, alphabetSize);
        for (int i = n - 1; i >= 0; i--) {
            int j = sa[i] - 1;
            if (j >= 0 && t[j] == S_TYPE) {
                int b = s[j];
                sa[bucketTails[b]--] = j;
            }
        }
    }

    private static boolean isLmsEqual(int[] s, boolean[] t, int p, int q) {
        while (true) {
            if (s[p] != s[q] || t[p] != t[q]) return false;
            p++;
            q++;
            if ((t[p] == S_TYPE && t[p - 1] == L_TYPE) || (t[q] == S_TYPE && t[q - 1] == L_TYPE)) {
                return (t[p] == S_TYPE && t[p - 1] == L_TYPE) && (t[q] == S_TYPE && t[q - 1] == L_TYPE);
            }
        }
    }

    private static int[] getBucketHeads(int[] buckets, int alphabetSize) {
        int[] heads = new int[alphabetSize];
        int sum = 0;
        for (int i = 0; i < alphabetSize; i++) {
            heads[i] = sum;
            sum += buckets[i];
        }
        return heads;
    }

    private static int[] getBucketTails(int[] buckets, int alphabetSize) {
        int[] tails = new int[alphabetSize];
        int sum = 0;
        for (int i = 0; i < alphabetSize; i++) {
            sum += buckets[i];
            tails[i] = sum - 1;
        }
        return tails;
    }
}
