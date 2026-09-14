package edutrack.core;

/**
 * Hand-built Binary Min-Heap with zero java.util.* dependencies.
 * Provides O(log N) insert and extractMin operations.
 */
public class MyMinHeap<T extends Comparable<T>> {
    private Object[] heap;
    private int size;
    private static final int DEFAULT_CAPACITY = 16;

    public MyMinHeap() {
        this(DEFAULT_CAPACITY);
    }

    public MyMinHeap(int capacity) {
        this.heap = new Object[Math.max(capacity, 1)];
        this.size = 0;
    }

    public void insert(T item) {
        if (size == heap.length) {
            resize();
        }
        heap[size] = item;
        swim(size);
        size++;
    }

    @SuppressWarnings("unchecked")
    public T peekMin() {
        if (isEmpty()) return null;
        return (T) heap[0];
    }

    @SuppressWarnings("unchecked")
    public T extractMin() {
        if (isEmpty()) return null;
        T min = (T) heap[0];
        heap[0] = heap[size - 1];
        heap[size - 1] = null;
        size--;
        if (size > 0) {
            sink(0);
        }
        return min;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @SuppressWarnings("unchecked")
    private void swim(int k) {
        while (k > 0) {
            int parent = (k - 1) / 2;
            Comparable<T> current = (Comparable<T>) heap[k];
            if (current.compareTo((T) heap[parent]) < 0) {
                swap(k, parent);
                k = parent;
            } else {
                break;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void sink(int k) {
        while (2 * k + 1 < size) {
            int left = 2 * k + 1;
            int right = left + 1;
            int smallest = left;

            if (right < size && ((Comparable<T>) heap[right]).compareTo((T) heap[left]) < 0) {
                smallest = right;
            }

            if (((Comparable<T>) heap[smallest]).compareTo((T) heap[k]) < 0) {
                swap(k, smallest);
                k = smallest;
            } else {
                break;
            }
        }
    }

    private void swap(int i, int j) {
        Object tmp = heap[i];
        heap[i] = heap[j];
        heap[j] = tmp;
    }

    private void resize() {
        Object[] newHeap = new Object[heap.length * 2];
        System.arraycopy(heap, 0, newHeap, 0, heap.length);
        heap = newHeap;
    }
}
