package edutrack.algorithms.suffix;

/**
 * Suffix Array implementation constructed in O(N log^2 N) using the prefix-doubling algorithm.
 * Enables O(m log n) binary search for arbitrary substrings across textbooks and notes.
 * Handcrafted with zero java.util.* dependencies.
 */
public class SuffixArray {
    private final String text;
    private final int n;
    private final int[] sa;
    private final int[] rank;

    private static class Suffix {
        int index;
        int rank1;
        int rank2;
    }

    public SuffixArray(String text) {
        this.text = text;
        this.n = text.length();
        this.sa = new int[n];
        this.rank = new int[n];
        build();
    }

    private void build() {
        Suffix[] suffixes = new Suffix[n];
        for (int i = 0; i < n; i++) {
            suffixes[i] = new Suffix();
            suffixes[i].index = i;
            suffixes[i].rank1 = text.charAt(i);
            suffixes[i].rank2 = (i + 1 < n) ? text.charAt(i + 1) : -1;
        }

        customSort(suffixes);

        int[] ind = new int[n];
        for (int k = 4; k < 2 * n; k *= 2) {
            int rankVal = 0;
            int prevRank1 = suffixes[0].rank1;
            suffixes[0].rank1 = rankVal;
            ind[suffixes[0].index] = 0;

            for (int i = 1; i < n; i++) {
                if (suffixes[i].rank1 == prevRank1 && suffixes[i].rank2 == suffixes[i - 1].rank2) {
                    suffixes[i].rank1 = rankVal;
                } else {
                    prevRank1 = suffixes[i].rank1;
                    suffixes[i].rank1 = ++rankVal;
                }
                ind[suffixes[i].index] = i;
            }

            for (int i = 0; i < n; i++) {
                int nextIndex = suffixes[i].index + k / 2;
                suffixes[i].rank2 = (nextIndex < n) ? suffixes[ind[nextIndex]].rank1 : -1;
            }

            customSort(suffixes);
        }

        for (int i = 0; i < n; i++) {
            sa[i] = suffixes[i].index;
            rank[sa[i]] = i;
        }
    }

    // Handcrafted Merge Sort for Suffix array without java.util.Arrays.sort
    private void customSort(Suffix[] arr) {
        Suffix[] aux = new Suffix[arr.length];
        mergeSort(arr, aux, 0, arr.length - 1);
    }

    private void mergeSort(Suffix[] arr, Suffix[] aux, int low, int high) {
        if (low >= high) return;
        int mid = low + (high - low) / 2;
        mergeSort(arr, aux, low, mid);
        mergeSort(arr, aux, mid + 1, high);
        merge(arr, aux, low, mid, high);
    }

    private void merge(Suffix[] arr, Suffix[] aux, int low, int mid, int high) {
        for (int k = low; k <= high; k++) {
            aux[k] = arr[k];
        }
        int i = low, j = mid + 1;
        for (int k = low; k <= high; k++) {
            if (i > mid) {
                arr[k] = aux[j++];
            } else if (j > high) {
                arr[k] = aux[i++];
            } else if (compare(aux[i], aux[j]) <= 0) {
                arr[k] = aux[i++];
            } else {
                arr[k] = aux[j++];
            }
        }
    }

    private int compare(Suffix a, Suffix b) {
        if (a.rank1 != b.rank1) return Integer.compare(a.rank1, b.rank1);
        return Integer.compare(a.rank2, b.rank2);
    }

    public int[] getSuffixArray() {
        return sa;
    }

    public int[] getRankArray() {
        return rank;
    }

    public String getText() {
        return text;
    }

    /**
     * Binary search to find if pattern exists in text.
     * Returns the index in SA if found, or -1 if absent.
     */
    public int searchPattern(String pattern) {
        int m = pattern.length();
        int low = 0, high = n - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int suffixStart = sa[mid];
            int cmp = comparePatternWithSuffix(pattern, suffixStart);

            if (cmp == 0) {
                return suffixStart;
            } else if (cmp < 0) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return -1;
    }

    private int comparePatternWithSuffix(String pattern, int suffixStart) {
        int m = pattern.length();
        for (int i = 0; i < m; i++) {
            if (suffixStart + i >= n) {
                return 1; // pattern is longer than remaining suffix
            }
            char pc = pattern.charAt(i);
            char sc = text.charAt(suffixStart + i);
            if (pc != sc) {
                return Character.compare(pc, sc);
            }
        }
        return 0; // exact prefix match
    }
}
