package edutrack.core;

/**
 * Hand-built Disjoint Set Union (DSU / Union-Find) with path compression
 * and union-by-rank. Zero java.util.* dependencies.
 * Provides near-O(1) alpha(N) amortized find and union operations.
 */
public class DisjointSetUnion {
    private int[] parent;
    private int[] rank;
    private int count;

    public DisjointSetUnion(int n) {
        this.parent = new int[n];
        this.rank = new int[n];
        this.count = n;
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
    }

    public int find(int p) {
        if (p < 0 || p >= parent.length) {
            throw new IndexOutOfBoundsException("Index " + p + " out of bounds");
        }
        int root = p;
        while (root != parent[root]) {
            root = parent[root];
        }
        // Path compression
        int curr = p;
        while (curr != root) {
            int next = parent[curr];
            parent[curr] = root;
            curr = next;
        }
        return root;
    }

    public boolean union(int p, int q) {
        int rootP = find(p);
        int rootQ = find(q);
        if (rootP == rootQ) return false;

        if (rank[rootP] < rank[rootQ]) {
            parent[rootP] = rootQ;
        } else if (rank[rootP] > rank[rootQ]) {
            parent[rootQ] = rootP;
        } else {
            parent[rootQ] = rootP;
            rank[rootP]++;
        }
        count--;
        return true;
    }

    public boolean connected(int p, int q) {
        return find(p) == find(q);
    }

    public int getCount() {
        return count;
    }
}
