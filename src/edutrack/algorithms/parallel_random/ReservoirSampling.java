package edutrack.algorithms.parallel_random;

import edutrack.core.MyArrayList;

/**
 * Reservoir Sampling (Algorithm R) for continuous, high-velocity student-activity data.
 * Guarantees that in a single pass over a stream of arbitrary or unbounded size N,
 * every item has an exactly equal probability (k / N) of being included in the sample.
 * Zero java.util.* dependencies.
 */
public class ReservoirSampling<T> {

    private final int k;
    private final MyArrayList<T> reservoir;
    private int streamCount;
    private long rngState;

    public ReservoirSampling(int sampleSize) {
        if (sampleSize <= 0) throw new IllegalArgumentException("Sample size k must be positive");
        this.k = sampleSize;
        this.reservoir = new MyArrayList<>(sampleSize);
        this.streamCount = 0;
        this.rngState = 12345678910111213L;
    }

    private int nextRandomInt(int bound) {
        if (bound <= 0) return 0;
        rngState ^= (rngState << 13);
        rngState ^= (rngState >>> 7);
        rngState ^= (rngState << 17);
        long pos = rngState < 0 ? -rngState : rngState;
        return (int) (pos % bound);
    }

    /**
     * Processes the next arriving event from the academic activity stream.
     */
    public void processNext(T item) {
        streamCount++;
        if (reservoir.size() < k) {
            reservoir.add(item);
        } else {
            // Pick random index between 0 and streamCount - 1
            int j = nextRandomInt(streamCount);
            if (j < k) {
                reservoir.set(j, item);
            }
        }
    }

    public MyArrayList<T> getSample() {
        return reservoir;
    }

    public int getStreamCount() {
        return streamCount;
    }

    public int getSampleSize() {
        return k;
    }
}
