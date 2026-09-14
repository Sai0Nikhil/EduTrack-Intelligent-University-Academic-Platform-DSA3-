package edutrack.algorithms.parallel_random;

import edutrack.core.MyArrayList;

/**
 * Randomized QuickSort for academic merit ranking and sorting large student records.
 * Uses uniform random pivot selection to guarantee expected O(N log N) time complexity
 * and immunity to adversarial inputs.
 * Zero java.util.* dependencies (custom linear congruential generator for PRNG).
 */
public class RandomizedQuickSort {

    private static long seed = 88172645463325252L;

    private static synchronized int nextRandomInt(int bound) {
        if (bound <= 0) return 0;
        seed ^= (seed << 21);
        seed ^= (seed >>> 35);
        seed ^= (seed << 4);
        int val = (int) (seed & 0x7fffffff) % bound;
        return val < 0 ? -val : val;
    }

    public static <T extends Comparable<T>> void sort(MyArrayList<T> list) {
        if (list == null || list.size() <= 1) return;
        quickSort(list, 0, list.size() - 1);
    }

    private static <T extends Comparable<T>> void quickSort(MyArrayList<T> list, int low, int high) {
        if (low < high) {
            int pivotIndex = randomizedPartition(list, low, high);
            quickSort(list, low, pivotIndex - 1);
            quickSort(list, pivotIndex + 1, high);
        }
    }

    private static <T extends Comparable<T>> int randomizedPartition(MyArrayList<T> list, int low, int high) {
        int randomIndex = low + nextRandomInt(high - low + 1);
        swap(list, randomIndex, high);

        T pivot = list.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (list.get(j).compareTo(pivot) <= 0) {
                i++;
                swap(list, i, j);
            }
        }
        swap(list, i + 1, high);
        return i + 1;
    }

    private static <T> void swap(MyArrayList<T> list, int i, int j) {
        T temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }
}
