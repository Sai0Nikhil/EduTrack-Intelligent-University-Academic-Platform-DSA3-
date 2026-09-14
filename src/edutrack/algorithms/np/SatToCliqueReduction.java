package edutrack.algorithms.np;

import edutrack.core.MyArrayList;

/**
 * Polynomial-Time Karp Reduction: 3-SAT -> CLIQUE.
 * Demonstrates how university exam and scheduling constraints in 3-CNF form
 * are transformed in polynomial time into the Maximum Clique graph problem.
 * Zero java.util.* dependencies.
 */
public class SatToCliqueReduction {

    public static class NodeLiteral {
        public int clauseIndex;
        public int literal;
        public int vertexId;

        public NodeLiteral(int clauseIndex, int literal, int vertexId) {
            this.clauseIndex = clauseIndex;
            this.literal = literal;
            this.vertexId = vertexId;
        }

        @Override
        public String toString() {
            return "v" + vertexId + "[C" + clauseIndex + ": " + (literal > 0 ? "x" : "-x") + Math.abs(literal) + "]";
        }
    }

    public static class ReducedGraph {
        public int numVertices;
        public boolean[][] adj;
        public MyArrayList<NodeLiteral> nodes;
        public int targetCliqueSize;

        public ReducedGraph(int numVertices, int targetCliqueSize) {
            this.numVertices = numVertices;
            this.targetCliqueSize = targetCliqueSize;
            this.adj = new boolean[numVertices][numVertices];
            this.nodes = new MyArrayList<>(numVertices);
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
     * Transforms 3-SAT formula into a Graph where satisfiable <=> Clique of size m exists.
     */
    public static ReducedGraph reduce(MyArrayList<DpllSatSolver.Clause> clauses) {
        int m = clauses.size();
        int totalVertices = m * 3;
        ReducedGraph graph = new ReducedGraph(totalVertices, m);

        int vId = 0;
        for (int i = 0; i < m; i++) {
            DpllSatSolver.Clause c = clauses.get(i);
            for (int j = 0; j < 3; j++) {
                int lit = (j < c.literals.size()) ? c.literals.get(j) : c.literals.get(0);
                graph.nodes.add(new NodeLiteral(i, lit, vId++));
            }
        }

        // Connect nodes from different clauses that are non-contradictory
        for (int i = 0; i < totalVertices; i++) {
            NodeLiteral u = graph.nodes.get(i);
            for (int j = i + 1; j < totalVertices; j++) {
                NodeLiteral v = graph.nodes.get(j);

                // Condition 1: different clauses
                // Condition 2: literals do not contradict (u.lit != -v.lit)
                if (u.clauseIndex != v.clauseIndex && u.literal != -v.literal) {
                    graph.addEdge(i, j);
                }
            }
        }

        return graph;
    }
}
