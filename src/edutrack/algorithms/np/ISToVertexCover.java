package edutrack.algorithms.np;

import edutrack.core.MyArrayList;

/**
 * Polynomial-Time Reduction: INDEPENDENT SET -> VERTEX COVER.
 * Theorem: In any graph G = (V, E), S is an Independent Set <=> V \ S is a Vertex Cover.
 * Therefore, G has an Independent Set of size k <=> G has a Vertex Cover of size |V| - k.
 * Zero java.util.* dependencies.
 */
public class ISToVertexCover {

    public static class DualityResult {
        public int numVertices;
        public int independentSetSize;
        public int vertexCoverSize;
        public MyArrayList<Integer> independentSet;
        public MyArrayList<Integer> vertexCover;

        public DualityResult(int numVertices, MyArrayList<Integer> independentSet, MyArrayList<Integer> vertexCover) {
            this.numVertices = numVertices;
            this.independentSet = independentSet;
            this.vertexCover = vertexCover;
            this.independentSetSize = independentSet.size();
            this.vertexCoverSize = vertexCover.size();
        }

        @Override
        public String toString() {
            return "|V|=" + numVertices + " | Independent Set: " + independentSetSize +
                   " | Dual Vertex Cover: " + vertexCoverSize + " (Sum = " + (independentSetSize + vertexCoverSize) + ")";
        }
    }

    /**
     * Given an Independent Set S, constructs the complementary Vertex Cover V \ S.
     */
    public static MyArrayList<Integer> getDualVertexCover(int numVertices, MyArrayList<Integer> independentSet) {
        boolean[] inIS = new boolean[numVertices];
        for (int i = 0; i < independentSet.size(); i++) {
            inIS[independentSet.get(i)] = true;
        }

        MyArrayList<Integer> vc = new MyArrayList<>();
        for (int i = 0; i < numVertices; i++) {
            if (!inIS[i]) {
                vc.add(i);
            }
        }
        return vc;
    }
}
