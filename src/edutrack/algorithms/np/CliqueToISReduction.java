package edutrack.algorithms.np;

/**
 * Polynomial-Time Karp Reduction: CLIQUE -> INDEPENDENT SET.
 * Constructs the graph complement G_bar = (V, E_bar).
 * Theorem: G has a Clique of size k <=> G_bar has an Independent Set of size k.
 * Zero java.util.* dependencies.
 */
public class CliqueToISReduction {

    public static class Graph {
        public int numVertices;
        public boolean[][] adj;

        public Graph(int numVertices) {
            this.numVertices = numVertices;
            this.adj = new boolean[numVertices][numVertices];
        }

        public void addEdge(int u, int v) {
            adj[u][v] = true;
            adj[v][u] = true;
        }

        public int countEdges() {
            int count = 0;
            for (int i = 0; i < numVertices; i++) {
                for (int j = i + 1; j < numVertices; j++) {
                    if (adj[i][j]) count++;
                }
            }
            return count;
        }
    }

    /**
     * Constructs the complement graph G_bar = (V, E_bar).
     */
    public static Graph reduceToComplement(Graph g) {
        int n = g.numVertices;
        Graph comp = new Graph(n);

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (!g.adj[i][j]) {
                    comp.addEdge(i, j);
                }
            }
        }

        return comp;
    }
}
