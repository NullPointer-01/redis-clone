package ds;

import java.util.Objects;
import java.util.Stack;

public class Trie {
    private final TrieNode root;

    public Trie() {
        this.root = new TrieNode();
    }

    public void insert(String entryId, Integer value) {
        Objects.requireNonNull(value);

        TrieNode curr = root;
        for (char c : entryId.toCharArray()) {
            int idx = c -'0';

            if (curr.children[idx] == null) {
                curr.children[idx] = new TrieNode();
            }

            curr = curr.children[idx];
        }

        curr.value = value;
    }

    public Integer search(String entryId) {
        TrieNode curr = root;

        for (char c : entryId.toCharArray()) {
            int idx = c -'0';

            if (curr.children[idx] == null) {
                return null;
            }
            curr = curr.children[idx];
        }

        return curr.value;
    }

    public Integer searchCeil(String entryId) {
        TrieNode curr = root;
        Stack<Pair<TrieNode, Integer>> stack = new Stack<>(); // Pair of TrieNode and visited index

        for (char c : entryId.toCharArray()) {
            int idx = c-'0';
            stack.add(new Pair<>(curr, idx));

            if (curr.children[idx] != null) {
                curr = curr.children[idx];
            } else {
                TrieNode nextEntry = findNextEntry(stack);
                return nextEntry == null ? null : nextEntry.value;
            }
        }

        return curr.value;
    }

    private TrieNode findNextEntry(Stack<Pair<TrieNode, Integer>> stack) {
        TrieNode curr = null;

        while (!stack.isEmpty() || curr != null) {
            if (curr != null && curr.value != null) return curr;

            int nextIdx = 0;
            if (curr == null) {
                Pair<TrieNode, Integer> pair = stack.pop();
                curr = pair.getKey();
                nextIdx = pair.getValue() + 1;
            }

            TrieNode child = null;
            while (nextIdx <= 9) {
                if (curr.children[nextIdx] != null) {
                    child = curr.children[nextIdx];
                    break;
                }
                nextIdx++;
            }

            curr = child;
        }

        return null;
    }

    private static class TrieNode {
        TrieNode[] children;
        Integer value;

        TrieNode() {
            this.children = new TrieNode[10];
        }
    }
}
