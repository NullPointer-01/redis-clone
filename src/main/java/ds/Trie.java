package ds;

public class Trie {
    private final TrieNode root;

    public Trie() {
        this.root = new TrieNode();
    }

    public void insert(String entryId, int index) {
        TrieNode curr = root;
        for (char c : entryId.toCharArray()) {
            if (c == '-') continue;
            
            int idx = (char) (c-'0');

            if (curr.children[idx] == null) {
                curr.children[idx] = new TrieNode();
            }

            curr = curr.children[idx];
        }

        curr.idx = index;
    }

    public int findIndex(String entryId) {
        TrieNode curr = root;
        for (char c : entryId.toCharArray()) {
            if (c == '-') continue;

            int idx = (char) (c-'0');

            if (curr.children[idx] == null) {
                curr.children[idx] = new TrieNode();
            }

            curr = curr.children[idx];
        }

        return curr.idx;
    }

    private static class TrieNode {
        TrieNode[] children;
        Integer idx;

        TrieNode() {
            this.children = new TrieNode[10];
        }
    }
}
