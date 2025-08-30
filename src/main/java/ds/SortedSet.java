package ds;

import java.util.HashMap;
import java.util.Map;

public class SortedSet<T extends Comparable<? super T>> {
    private final SkipList<T> skipList;
    private final Map<T, Double> membersVsScores;

    public SortedSet() {
        this.skipList = new SkipList<>();
        this.membersVsScores = new HashMap<>();
    }

    public boolean zAdd(T value, double score) {
        if (membersVsScores.containsKey(value)) {
            double currScore = membersVsScores.get(value);
            SkipList.Node<T> node = skipList.find(value, currScore);

            node.score = score;
            return false;
        }

        membersVsScores.put(value, score);
        skipList.insert(value, score);
        return true;
    }

    public boolean zRem(T value) {
        if (membersVsScores.containsKey(value)) {
            double score = membersVsScores.get(value);
            skipList.delete(value, score);
            return true;
        }

        return false;
    }

    public Integer zRank(T value) {
        if (!membersVsScores.containsKey(value)) {
            return 0;
        }
        return skipList.rank(value, membersVsScores.get(value));
    }
}
