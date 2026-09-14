package edutrack.algorithms.suffix;

import edutrack.core.MyArrayList;

/**
 * Kasai's Algorithm for computing Longest Common Prefix (LCP) array in linear time O(N).
 * Provides document similarity and plagiarism detection across student assignments.
 * Zero java.util.* dependencies.
 */
public class KasaiLCP {

    public static class SharedExcerpt {
        public String text;
        public int length;
        public int posDoc1;
        public int posDoc2;

        public SharedExcerpt(String text, int length, int posDoc1, int posDoc2) {
            this.text = text;
            this.length = length;
            this.posDoc1 = posDoc1;
            this.posDoc2 = posDoc2;
        }

        @Override
        public String toString() {
            return "Shared [" + length + " chars]: \"" + text + "\"";
        }
    }

    /**
     * Computes the LCP array in O(n) time using Kasai's algorithm.
     * lcp[i] is the length of the LCP between suffix sa[i] and sa[i-1].
     * lcp[0] = 0.
     */
    public static int[] computeLCP(String text, int[] sa) {
        int n = text.length();
        int[] lcp = new int[n];
        int[] rank = new int[n];

        for (int i = 0; i < n; i++) {
            rank[sa[i]] = i;
        }

        int h = 0; // current lcp length
        for (int i = 0; i < n; i++) {
            if (rank[i] > 0) {
                int k = sa[rank[i] - 1]; // predecessor in suffix array
                while (i + h < n && k + h < n && text.charAt(i + h) == text.charAt(k + h)) {
                    h++;
                }
                lcp[rank[i]] = h;
                if (h > 0) {
                    h--;
                }
            }
        }
        return lcp;
    }

    /**
     * Detects shared passages between two academic submissions / documents
     * using Suffix Array + Kasai LCP on the concatenated string (doc1 + "#" + doc2).
     */
    public static MyArrayList<SharedExcerpt> findSharedExcerpts(String doc1, String doc2, int minLengthThreshold) {
        MyArrayList<SharedExcerpt> results = new MyArrayList<>();
        if (doc1 == null || doc2 == null) return results;

        int len1 = doc1.length();
        String combined = doc1 + "#" + doc2;
        int n = combined.length();

        SuffixArray saObj = new SuffixArray(combined);
        int[] sa = saObj.getSuffixArray();
        int[] lcp = computeLCP(combined, sa);

        for (int i = 1; i < n; i++) {
            int len = lcp[i];
            if (len >= minLengthThreshold) {
                int p1 = sa[i - 1];
                int p2 = sa[i];

                // Check if one suffix starts in doc1 (< len1) and one in doc2 (> len1)
                boolean p1InDoc1 = p1 < len1;
                boolean p2InDoc1 = p2 < len1;

                if (p1InDoc1 != p2InDoc1) {
                    int pos1 = p1InDoc1 ? p1 : p2;
                    int pos2 = p1InDoc1 ? (p2 - len1 - 1) : (p1 - len1 - 1);

                    // Ensure substring does not include the separator '#'
                    String shared = combined.substring(Math.min(p1, p2), Math.min(p1, p2) + len);
                    if (!shared.contains("#")) {
                        results.add(new SharedExcerpt(shared, len, pos1, pos2));
                    }
                }
            }
        }

        return results;
    }
}
