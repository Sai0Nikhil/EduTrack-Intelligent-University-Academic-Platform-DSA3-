package edutrack.algorithms.parallel_random;

/**
 * Miller-Rabin Probabilistic and Deterministic Primality Test.
 * Used in EduTrack for generating and verifying cryptographic student tokens,
 * secure diploma hash certificates, and prime moduli.
 * Zero java.util.* dependencies.
 */
public class MillerRabin {

    private static final long[] DETERMINISTIC_BASES = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37};

    /**
     * Tests whether n is prime using the Miller-Rabin algorithm.
     */
    public static boolean isPrime(long n) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;

        // Factor out powers of 2: n - 1 = 2^s * d
        long d = n - 1;
        int s = 0;
        while ((d & 1) == 0) {
            d >>= 1;
            s++;
        }

        for (long a : DETERMINISTIC_BASES) {
            if (n <= a) break;
            if (!checkComposite(n, a, d, s)) {
                return false; // Definitely composite
            }
        }

        return true; // Prime
    }

    private static boolean checkComposite(long n, long a, long d, int s) {
        long x = modPow(a, d, n);
        if (x == 1 || x == n - 1) {
            return true;
        }

        for (int r = 1; r < s; r++) {
            x = mulMod(x, x, n);
            if (x == n - 1) {
                return true;
            }
        }

        return false;
    }

    /**
     * Computes (base^exp) % mod using binary exponentiation.
     */
    public static long modPow(long base, long exp, long mod) {
        long res = 1;
        base = base % mod;
        while (exp > 0) {
            if ((exp & 1) == 1) {
                res = mulMod(res, base, mod);
            }
            base = mulMod(base, base, mod);
            exp >>= 1;
        }
        return res;
    }

    /**
     * Safely computes (a * b) % mod avoiding 64-bit overflow.
     */
    public static long mulMod(long a, long b, long mod) {
        long res = 0;
        a %= mod;
        b %= mod;
        while (b > 0) {
            if ((b & 1) == 1) {
                res = (res + a) % mod;
            }
            a = (a * 2) % mod;
            b >>= 1;
        }
        return res;
    }

    /**
     * Finds the next prime >= start.
     */
    public static long nextPrime(long start) {
        long p = (start <= 2) ? 2 : (start % 2 == 0 ? start + 1 : start);
        while (!isPrime(p)) {
            p += (p == 2) ? 1 : 2;
        }
        return p;
    }
}
