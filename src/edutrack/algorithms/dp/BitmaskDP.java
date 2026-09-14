package edutrack.algorithms.dp;

import edutrack.core.MyArrayList;

/**
 * Bitmask Dynamic Programming for optimal tour scheduling / resource selection in O(2^N * N^2).
 * Solves the Traveling Academic Auditor problem: visiting all university departments/examination
 * centers with minimum transit time.
 * Zero java.util.* dependencies.
 */
public class BitmaskDP {

    public static class TourResult {
        public int minCost;
        public MyArrayList<Integer> path;

        public TourResult(int minCost, MyArrayList<Integer> path) {
            this.minCost = minCost;
            this.path = path;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("Min Transit Cost: " + minCost + " | Tour: ");
            for (int i = 0; i < path.size(); i++) {
                sb.append(path.get(i));
                if (i + 1 < path.size()) sb.append(" -> ");
            }
            return sb.toString();
        }
    }

    private static final int INF = 1_000_000_000;

    /**
     * Solves TSP starting and ending at node 0 across n departments.
     * costMatrix[i][j] is the distance/transit time from department i to j.
     */
    public static TourResult solveTSP(int[][] dist, String[] departmentNames) {
        int n = dist.length;
        if (n <= 1) {
            MyArrayList<Integer> single = new MyArrayList<>();
            single.add(0);
            return new TourResult(0, single);
        }

        int limit = 1 << n;
        int[][] dp = new int[limit][n];
        int[][] parent = new int[limit][n];

        for (int mask = 0; mask < limit; mask++) {
            for (int i = 0; i < n; i++) {
                dp[mask][i] = INF;
                parent[mask][i] = -1;
            }
        }

        dp[1][0] = 0; // start at node 0

        for (int mask = 1; mask < limit; mask++) {
            for (int u = 0; u < n; u++) {
                if ((mask & (1 << u)) == 0 || dp[mask][u] == INF) continue;

                for (int v = 0; v < n; v++) {
                    if ((mask & (1 << v)) == 0) {
                        int nextMask = mask | (1 << v);
                        int newCost = dp[mask][u] + dist[u][v];
                        if (newCost < dp[nextMask][v]) {
                            dp[nextMask][v] = newCost;
                            parent[nextMask][v] = u;
                        }
                    }
                }
            }
        }

        // Return to starting department 0
        int allVisited = limit - 1;
        int minTotalCost = INF;
        int lastNode = -1;

        for (int i = 1; i < n; i++) {
            if (dp[allVisited][i] != INF) {
                int cost = dp[allVisited][i] + dist[i][0];
                if (cost < minTotalCost) {
                    minTotalCost = cost;
                    lastNode = i;
                }
            }
        }

        // Reconstruct path
        MyArrayList<Integer> path = new MyArrayList<>();
        int currMask = allVisited;
        int currNode = lastNode;

        while (currNode != -1) {
            path.add(currNode);
            int prevNode = parent[currMask][currNode];
            currMask ^= (1 << currNode);
            currNode = prevNode;
        }

        // Reverse path to order from start
        MyArrayList<Integer> orderedPath = new MyArrayList<>();
        orderedPath.add(0);
        for (int i = path.size() - 1; i >= 0; i--) {
            orderedPath.add(path.get(i));
        }
        orderedPath.add(0); // return to start

        return new TourResult(minTotalCost, orderedPath);
    }
}
