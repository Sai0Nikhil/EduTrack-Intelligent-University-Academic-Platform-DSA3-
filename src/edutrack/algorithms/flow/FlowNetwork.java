package edutrack.algorithms.flow;

import edutrack.core.MyArrayList;

/**
 * Residual Flow Network representation for network flow algorithms.
 * Supports forward and backward residual edges with zero java.util.* dependencies.
 */
public class FlowNetwork {
    public static class Edge {
        public int to;
        public int rev; // index of reverse edge in network.adj.get(to)
        public int cap;
        public int flow;

        public Edge(int to, int rev, int cap) {
            this.to = to;
            this.rev = rev;
            this.cap = cap;
            this.flow = 0;
        }

        public int remainingCapacity() {
            return cap - flow;
        }
    }

    public final int n;
    public final MyArrayList<MyArrayList<Edge>> adj;

    public FlowNetwork(int n) {
        this.n = n;
        this.adj = new MyArrayList<>(n);
        for (int i = 0; i < n; i++) {
            this.adj.add(new MyArrayList<>());
        }
    }

    public void addEdge(int from, int to, int capacity) {
        MyArrayList<Edge> fromList = adj.get(from);
        MyArrayList<Edge> toList = adj.get(to);

        int fromIndex = fromList.size();
        int toIndex = toList.size();

        Edge forward = new Edge(to, toIndex, capacity);
        Edge backward = new Edge(from, fromIndex, 0);

        fromList.add(forward);
        toList.add(backward);
    }

    public void resetFlows() {
        for (int i = 0; i < n; i++) {
            MyArrayList<Edge> edges = adj.get(i);
            for (int j = 0; j < edges.size(); j++) {
                edges.get(j).flow = 0;
            }
        }
    }
}
