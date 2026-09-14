package edutrack.algorithms.flow;

import edutrack.core.MyArrayList;

/**
 * Ford-Fulkerson Method for Maximum Flow using DFS to find augmenting paths.
 * Useful for workstation and lab seat allocation.
 * Zero java.util.* dependencies.
 */
public class FordFulkerson {

    public static int maxFlow(FlowNetwork net, int source, int sink) {
        int totalFlow = 0;
        boolean[] visited = new boolean[net.n];

        while (true) {
            for (int i = 0; i < net.n; i++) visited[i] = false;
            int bottleneck = dfs(net, source, sink, Integer.MAX_VALUE, visited);
            if (bottleneck == 0) {
                break;
            }
            totalFlow += bottleneck;
        }

        return totalFlow;
    }

    private static int dfs(FlowNetwork net, int u, int sink, int flow, boolean[] visited) {
        if (u == sink) return flow;
        visited[u] = true;

        MyArrayList<FlowNetwork.Edge> edges = net.adj.get(u);
        for (int i = 0; i < edges.size(); i++) {
            FlowNetwork.Edge e = edges.get(i);
            if (!visited[e.to] && e.remainingCapacity() > 0) {
                int pushed = dfs(net, e.to, sink, Math.min(flow, e.remainingCapacity()), visited);
                if (pushed > 0) {
                    e.flow += pushed;
                    net.adj.get(e.to).get(e.rev).flow -= pushed;
                    return pushed;
                }
            }
        }
        return 0;
    }
}
