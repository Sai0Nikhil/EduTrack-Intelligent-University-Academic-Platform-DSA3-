package edutrack.algorithms.flow;

import edutrack.core.MyArrayList;
import edutrack.core.MyQueue;

/**
 * Dinic's Algorithm for Maximum Network Flow in O(V^2 * E).
 * Scales to large university-wide resource allocation networks by constructing
 * BFS level graphs and pushing multiple blocking flows simultaneously via DFS.
 * Zero java.util.* dependencies.
 */
public class DinicsAlgorithm {

    public static int maxFlow(FlowNetwork net, int source, int sink) {
        int totalFlow = 0;
        int[] level = new int[net.n];
        int[] ptr = new int[net.n];

        while (bfs(net, source, sink, level)) {
            for (int i = 0; i < net.n; i++) ptr[i] = 0;

            while (true) {
                int pushed = dfs(net, source, sink, Integer.MAX_VALUE, level, ptr);
                if (pushed <= 0) break;
                totalFlow += pushed;
            }
        }

        return totalFlow;
    }

    private static boolean bfs(FlowNetwork net, int source, int sink, int[] level) {
        for (int i = 0; i < net.n; i++) level[i] = -1;
        level[source] = 0;

        MyQueue<Integer> q = new MyQueue<>();
        q.enqueue(source);

        while (!q.isEmpty()) {
            int u = q.dequeue();
            MyArrayList<FlowNetwork.Edge> edges = net.adj.get(u);

            for (int i = 0; i < edges.size(); i++) {
                FlowNetwork.Edge e = edges.get(i);
                if (level[e.to] == -1 && e.remainingCapacity() > 0) {
                    level[e.to] = level[u] + 1;
                    q.enqueue(e.to);
                }
            }
        }

        return level[sink] != -1;
    }

    private static int dfs(FlowNetwork net, int u, int sink, int pushed, int[] level, int[] ptr) {
        if (pushed == 0 || u == sink) return pushed;

        MyArrayList<FlowNetwork.Edge> edges = net.adj.get(u);
        for (; ptr[u] < edges.size(); ptr[u]++) {
            FlowNetwork.Edge e = edges.get(ptr[u]);
            if (level[e.to] == level[u] + 1 && e.remainingCapacity() > 0) {
                int tr = dfs(net, e.to, sink, Math.min(pushed, e.remainingCapacity()), level, ptr);
                if (tr > 0) {
                    e.flow += tr;
                    net.adj.get(e.to).get(e.rev).flow -= tr;
                    return tr;
                }
            }
        }

        return 0;
    }
}
