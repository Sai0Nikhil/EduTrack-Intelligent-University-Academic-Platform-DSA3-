package edutrack.algorithms.strings;

import edutrack.core.MyArrayList;
import edutrack.core.MyQueue;

/**
 * Aho-Corasick Multi-Pattern String Matching Algorithm.
 * Constructs a Trie with failure links and dictionary output links in O(total pattern length),
 * then scans text in O(text length + matches) to detect all keyword occurrences simultaneously.
 */
public class AhoCorasick {
    public static class MatchResult {
        public String keyword;
        public int position;

        public MatchResult(String keyword, int position) {
            this.keyword = keyword;
            this.position = position;
        }

        @Override
        public String toString() {
            return "\"" + keyword + "\" at index " + position;
        }
    }

    private static class Node {
        Node[] children = new Node[128]; // ASCII range
        Node fail;
        MyArrayList<String> output = new MyArrayList<>();
    }

    private final Node root;
    private final MyArrayList<String> dictionary;

    public AhoCorasick() {
        this.root = new Node();
        this.dictionary = new MyArrayList<>();
    }

    public void addPattern(String pattern) {
        if (pattern == null || pattern.isEmpty()) return;
        dictionary.add(pattern);
        Node curr = root;
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            int idx = c < 128 ? c : (c % 128);
            if (curr.children[idx] == null) {
                curr.children[idx] = new Node();
            }
            curr = curr.children[idx];
        }
        curr.output.add(pattern);
    }

    public void buildAutomation() {
        MyQueue<Node> queue = new MyQueue<>();

        // Level 1 nodes fail to root
        for (int i = 0; i < 128; i++) {
            if (root.children[i] != null) {
                root.children[i].fail = root;
                queue.enqueue(root.children[i]);
            }
        }

        // BFS to build failure links
        while (!queue.isEmpty()) {
            Node curr = queue.dequeue();

            for (int c = 0; c < 128; c++) {
                Node child = curr.children[c];
                if (child != null) {
                    Node f = curr.fail;
                    while (f != null && f.children[c] == null) {
                        f = f.fail;
                    }
                    child.fail = (f != null) ? f.children[c] : root;

                    // Merge outputs from fail node
                    if (child.fail != null) {
                        for (int k = 0; k < child.fail.output.size(); k++) {
                            child.output.add(child.fail.output.get(k));
                        }
                    }

                    queue.enqueue(child);
                }
            }
        }
    }

    public MyArrayList<MatchResult> search(String text) {
        MyArrayList<MatchResult> matches = new MyArrayList<>();
        if (text == null) return matches;

        Node curr = root;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            int idx = c < 128 ? c : (c % 128);

            while (curr != null && curr.children[idx] == null) {
                curr = curr.fail;
            }

            if (curr == null) {
                curr = root;
                continue;
            }

            curr = curr.children[idx];
            if (curr != null && !curr.output.isEmpty()) {
                for (int j = 0; j < curr.output.size(); j++) {
                    String matchedWord = curr.output.get(j);
                    matches.add(new MatchResult(matchedWord, i - matchedWord.length() + 1));
                }
            }
        }

        return matches;
    }
}
