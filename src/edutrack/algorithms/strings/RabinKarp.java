package edutrack.algorithms.strings;

import edutrack.core.MyArrayList;

/**
 * Rabin-Karp String Search with Polynomial Rolling Hash and Double-Hashing.
 * Uses two independent prime moduli (10^9 + 7 and 10^9 + 9) to eliminate
 * hash collision vulnerabilities while achieving O(n + m) expected running time.
 */
public class RabinKarp {
    private static final long BASE1 = 313;
    private static final long MOD1 = 1_000_000_007L;

    private static final long BASE2 = 317;
    private static final long MOD2 = 1_000_000_009L;

    public static MyArrayList<Integer> search(String text, String pattern) {
        MyArrayList<Integer> occurrences = new MyArrayList<>();
        if (text == null || pattern == null) return occurrences;
        int n = text.length();
        int m = pattern.length();
        if (m == 0 || n < m) return occurrences;

        long pHash1 = 0, pHash2 = 0;
        long tHash1 = 0, tHash2 = 0;
        long power1 = 1, power2 = 1;

        for (int i = 0; i < m - 1; i++) {
            power1 = (power1 * BASE1) % MOD1;
            power2 = (power2 * BASE2) % MOD2;
        }

        for (int i = 0; i < m; i++) {
            pHash1 = (pHash1 * BASE1 + pattern.charAt(i)) % MOD1;
            pHash2 = (pHash2 * BASE2 + pattern.charAt(i)) % MOD2;
            tHash1 = (tHash1 * BASE1 + text.charAt(i)) % MOD1;
            tHash2 = (tHash2 * BASE2 + text.charAt(i)) % MOD2;
        }

        for (int i = 0; i <= n - m; i++) {
            if (pHash1 == tHash1 && pHash2 == tHash2) {
                // Secondary check for absolute safety
                boolean match = true;
                for (int j = 0; j < m; j++) {
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    occurrences.add(i);
                }
            }

            if (i < n - m) {
                tHash1 = (tHash1 - (text.charAt(i) * power1) % MOD1 + MOD1) % MOD1;
                tHash1 = (tHash1 * BASE1 + text.charAt(i + m)) % MOD1;

                tHash2 = (tHash2 - (text.charAt(i) * power2) % MOD2 + MOD2) % MOD2;
                tHash2 = (tHash2 * BASE2 + text.charAt(i + m)) % MOD2;
            }
        }

        return occurrences;
    }
}
