package edutrack.algorithms.flow;

import edutrack.core.MyArrayList;
import edutrack.core.MyQueue;

/**
 * Edmonds-Karp Maximum Flow Algorithm in O(V * E^2).
 * Solves academic resource-flow problems by using BFS to find
 * the shortest augmenting path in terms of number of edges.
 * Zero java.util.* dependencies.
 */
public class EdmondsKarp {

    public static int maxFlow(FlowNetwork net, int source, int sink) {
        int totalFlow = 0;
        int[] parentNode = new int[net.n];
        int[] parentEdgeIndex = new int[net.n];

        while (true) {
            for (int i = 0; i < net.n; i++) parentNode[i] = -1;

            MyQueue<Integer> queue = new MyQueue<>();
            queue.enqueue(source);
            parentNode[source] = source;

            while (!queue.isEmpty()) {
                int u = queue.dequeue();
                if (u == sink) break;

                MyArrayList<FlowNetwork.Edge> edges = net.adj.get(u);
                for (int i = 0; i < edges.size(); i++) {
                    FlowNetwork.Edge e = edges.get(i);
                    if (parentNode[e.to] == -1 && e.remainingCapacity() > 0) {
                        parentNode[e.to] = u;
                        parentEdgeIndex[e.to] = i;
                        queue.enqueue(e.to);
                    }
                }
            }

            if (parentNode[sink] == -1) {
                break; // No more augmenting paths
            }

            // Find bottleneck capacity along path
            int push = Integer.MAX_VALUE;
            int curr = sink;
            while (curr != source) {
                int p = parentNode[curr];
                int edgeIdx = parentEdgeIndex[curr];
                FlowNetwork.Edge e = net.adj.get(p).get(edgeIdx);
                push = Math.min(push, e.remainingCapacity());
                curr = p;
            }

            // Augment flow
            curr = sink;
            while (curr != source) {
                int p = parentNode[curr];
                int edgeIdx = parentEdgeIndex[curr];
                FlowNetwork.Edge e = net.adj.get(p).get(edgeIdx);
                e.flow += push;
                net.adj.get(curr).get(e.rev).flow -= push;
                curr = p;
            }

            totalFlow += push;
        }

        return totalFlow;
    }
}
