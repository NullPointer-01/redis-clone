package ds;

import java.util.Objects;
import java.util.Random;

public class SkipList<T extends Comparable<? super T>> {
    private static final int MAX_LEVEL = 16;
    private final Random rand = new Random();

    private static final double NEGATIVE_INFINITY = Double.NEGATIVE_INFINITY;

    private final Node<T> head;
    private int nLevels;
    private int size;

    public SkipList() {
        this.head = new Node<>(null, NEGATIVE_INFINITY, MAX_LEVEL);
        this.nLevels = 1;
        this.size = 0;
    }

    public Node<T> insert(T value, double score) {
        Objects.requireNonNull(value);

        Node<T>[] prev = new Node[MAX_LEVEL]; // Node previous to the position we insert
        int[] rank = new int[MAX_LEVEL]; // Rank of the node at all level

        Node<T> curr = head;
        for (int i = nLevels-1; i >= 0; i--) {
            // The rank of the upper level
            rank[i] = (i == nLevels-1) ? 0 : rank[i+1];
            while (curr.forward[i] != null && (curr.forward[i].score < score
                    || (curr.forward[i].score == score && curr.forward[i].value.compareTo(value) < 0))) {

                rank[i] += curr.span[i];
                curr = curr.forward[i];
            }

            prev[i] = curr;
        }

        int level = getRandomLevel();
        if (level > nLevels) {
            for (int i = nLevels; i < level; i++) {
                rank[i] = 0;
                prev[i] = head;
            }

            nLevels = level;
        }

        Node<T> newNode = new Node<>(value, score, level);
        for (int i = 0; i < level; i++) {
            // Update pointers
            newNode.forward[i] = prev[i].forward[i];
            prev[i].forward[i] = newNode;

            // Update spans
            newNode.span[i] = prev[i].span[i] - (rank[0] - rank[i]);
            prev[i].span[i] = rank[0] - rank[i] + 1;
        }

        for (int i = level; i < nLevels; i++) {
            prev[i].span[i]++; // Increment span for new levels
        }

        size++;
        return newNode;
    }

    public boolean contains(T value, double score) {
        Objects.requireNonNull(value);
        return find(value, score) != null;
    }

    public Node<T> find(T value, double score) {
        Objects.requireNonNull(value);

        Node<T> curr = head;

        for (int i = nLevels - 1; i >= 0; i--) {
            while (curr.forward[i] != null && (curr.forward[i].score < score
                    || (curr.forward[i].score == score && curr.forward[i].value.compareTo(value) < 0))) {

                curr = curr.forward[i];
            }
        }

        curr = curr.forward[0]; // Move forward one step and check if Node already exists
        if (curr != null && curr.value.equals(value)) {
            return curr;
        }

        return null;
    }

    public Node<T> delete(T value, double score) {
        Objects.requireNonNull(value);

        Node<T>[] prev = new Node[MAX_LEVEL];
        Node<T> curr = head;

        for (int i = nLevels-1; i >= 0; i--) {
            while (curr.forward[i] != null && (curr.forward[i].score < score
                    || (curr.forward[i].score == score && curr.forward[i].value.compareTo(value) < 0))) {

                curr = curr.forward[i];
            }

            prev[i] = curr;
        }

        curr = curr.forward[0];
        if (curr == null || !curr.value.equals(value)) {
            return null;
        }

        for (int i = 0; i < nLevels; i++) {
            // Exit early if node is not present at that level. Can be done, if span calculation isn't supported
            // if (prev[i].forward[i] != curr) break;

            if (prev[i].forward[i] != curr) {
                prev[i].span[i]--;
            } else {
                prev[i].span[i] += curr.span[i] - 1;
                prev[i].forward[i] = curr.forward[i];
            }
        }

        // Delete empty levels
        while (nLevels > 1 && head.forward[nLevels - 1] == null) {
            nLevels--;
        }

        size--;
        return curr;
    }

    public Integer rank(T value, double score) {
        Node<T> curr = head;
        int rank = 0;

        for (int i = nLevels - 1; i >= 0; i--) {
            while (curr.forward[i] != null && (curr.forward[i].score < score
                    || (curr.forward[i].score == score && curr.forward[i].value.compareTo(value) < 0))) {

                rank += curr.span[i];
                curr = curr.forward[i];
            }
        }

        curr = curr.forward[0];
        if (curr != null && curr.value.equals(value)) {
            return rank + 1;
        }

        return 0;
    }

    public int size() {
        return size;
    }

    public int levels() {
        return nLevels;
    }

    private int getRandomLevel() {
        int level = 1;

        while (rand.nextBoolean() && level < MAX_LEVEL) {
            level++;
        }

        return level;
    }

    public static class Node<T extends Comparable<? super T>> {
        T value;
        double score;
        Node<T>[] forward;
        int[] span;

        Node(T value, double score, int level) {
            this.value = value;
            this.score = score;
            this.forward = new Node[level];
            this.span = new int[level];
        }

        public double score() {
            return score;
        }

        public T value() {
            return value;
        }
    }
}
