package ds;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
            membersVsScores.remove(value);
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

    public Integer zCard() {
        return membersVsScores.size();
    }

    public Double zScore(T value) {
        if (!membersVsScores.containsKey(value)) {
            return null;
        }

        return membersVsScores.get(value);
    }

    public List<T> zRange(int startIdx, int endIdx) {
        if (startIdx > zCard() || startIdx > endIdx) return Collections.emptyList();
        endIdx = Math.min(endIdx, zCard());

        return skipList.range(startIdx, endIdx);
    }
}
