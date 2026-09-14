package edutrack.algorithms.np;

import edutrack.core.MyArrayList;
import edutrack.core.Pair;

/**
 * 2-Approximation Algorithm for Minimum Vertex Cover via Maximal Matching.
 * Solves the Exam Invigilator / Proctor Minimization problem:
 * Assigns proctors to courses such that every exam conflict edge is covered
 * with a guaranteed approximation ratio of at most 2 * OPT.
 * Zero java.util.* dependencies.
 */
public class VertexCover2Approx {

    public static class ApproxResult {
        public int approxCoverSize;
        public MyArrayList<Integer> coverVertices;
        public MyArrayList<Pair<Integer, Integer>> maximalMatching;

        public ApproxResult(MyArrayList<Integer> coverVertices, MyArrayList<Pair<Integer, Integer>> maximalMatching) {
            this.coverVertices = coverVertices;
            this.approxCoverSize = coverVertices.size();
            this.maximalMatching = maximalMatching;
        }

        @Override
        public String toString() {
            return "2-Approx Vertex Cover Size: " + approxCoverSize +
                   " (from Maximal Matching of size " + maximalMatching.size() + ")";
        }
    }

    /**
     * Computes a 2-approximation for Vertex Cover on an undirected graph.
     * numVertices: number of course conflict vertices
     * edges: list of conflict pairs (u, v)
     */
    public static ApproxResult approximateCover(int numVertices, MyArrayList<Pair<Integer, Integer>> edges) {
        boolean[] inCover = new boolean[numVertices];
        MyArrayList<Pair<Integer, Integer>> matching = new MyArrayList<>();

        for (int i = 0; i < edges.size(); i++) {
            Pair<Integer, Integer> edge = edges.get(i);
            int u = edge.first;
            int v = edge.second;

            // If neither u nor v is already in the vertex cover
            if (!inCover[u] && !inCover[v]) {
                inCover[u] = true;
                inCover[v] = true;
                matching.add(edge);
            }
        }

        MyArrayList<Integer> cover = new MyArrayList<>();
        for (int i = 0; i < numVertices; i++) {
            if (inCover[i]) {
                cover.add(i);
            }
        }

        return new ApproxResult(cover, matching);
    }
}
