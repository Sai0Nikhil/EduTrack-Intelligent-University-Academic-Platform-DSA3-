package edutrack.algorithms.suffix;

/**
 * Suffix Automaton (SAM) for substring queries, frequency counting,
 * and longest common substring search across academic documents.
 * Linear construction time O(N) and linear state space O(N).
 * Zero java.util.* dependencies.
 */
public class SuffixAutomaton {
    public static class State {
        public int len;
        public int link;
        public int[] next = new int[128];
        public int endposCount = 0;

        public State() {
            for (int i = 0; i < 128; i++) next[i] = -1;
            link = -1;
        }
    }

    private State[] st;
    private int sz;
    private int last;

    public SuffixAutomaton(String text) {
        int n = text.length();
        st = new State[2 * n + 1];
        st[0] = new State();
        sz = 1;
        last = 0;

        for (int i = 0; i < n; i++) {
            extend(text.charAt(i));
        }
    }

    private void extend(char c) {
        int idx = c < 128 ? c : (c % 128);
        int cur = sz++;
        st[cur] = new State();
        st[cur].len = st[last].len + 1;
        st[cur].endposCount = 1;

        int p = last;
        while (p != -1 && st[p].next[idx] == -1) {
            st[p].next[idx] = cur;
            p = st[p].link;
        }

        if (p == -1) {
            st[cur].link = 0;
        } else {
            int q = st[p].next[idx];
            if (st[p].len + 1 == st[q].len) {
                st[cur].link = q;
            } else {
                int clone = sz++;
                st[clone] = new State();
                st[clone].len = st[p].len + 1;
                for (int i = 0; i < 128; i++) {
                    st[clone].next[i] = st[q].next[i];
                }
                st[clone].link = st[q].link;
                st[clone].endposCount = 0;

                while (p != -1 && st[p].next[idx] == q) {
                    st[p].next[idx] = clone;
                    p = st[p].link;
                }
                st[q].link = st[cur].link = clone;
            }
        }
        last = cur;
    }

    /**
     * Checks whether a query string occurs as a substring in O(|query|) time.
     */
    public boolean containsSubstring(String query) {
        int curr = 0;
        for (int i = 0; i < query.length(); i++) {
            char c = query.charAt(i);
            int idx = c < 128 ? c : (c % 128);
            if (st[curr].next[idx] == -1) {
                return false;
            }
            curr = st[curr].next[idx];
        }
        return true;
    }

    /**
     * Computes the total number of distinct substrings in the indexed document.
     */
    public long countDistinctSubstrings() {
        long[] dp = new long[sz];
        for (int i = 0; i < sz; i++) dp[i] = -1;
        return countFromState(0, dp) - 1; // subtract empty string
    }

    private long countFromState(int u, long[] dp) {
        if (dp[u] != -1) return dp[u];
        long total = 1;
        for (int c = 0; c < 128; c++) {
            if (st[u].next[c] != -1) {
                total += countFromState(st[u].next[c], dp);
            }
        }
        dp[u] = total;
        return total;
    }

    /**
     * Finds the Longest Common Substring (LCS) with another string in O(|other|).
     */
    public String findLongestCommonSubstring(String other) {
        int curr = 0;
        int len = 0;
        int maxLen = 0;
        int bestEnd = 0;

        for (int i = 0; i < other.length(); i++) {
            char c = other.charAt(i);
            int idx = c < 128 ? c : (c % 128);

            while (curr > 0 && st[curr].next[idx] == -1) {
                curr = st[curr].link;
                len = st[curr].len;
            }

            if (st[curr].next[idx] != -1) {
                curr = st[curr].next[idx];
                len++;
            }

            if (len > maxLen) {
                maxLen = len;
                bestEnd = i;
            }
        }

        if (maxLen == 0) return "";
        return other.substring(bestEnd - maxLen + 1, bestEnd + 1);
    }
}
