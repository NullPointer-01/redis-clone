package ds;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SortedSetTest {

    @Test
    void testZAddNewMember() {
        SortedSet<String> sortedSet = new SortedSet<>();
        boolean added = sortedSet.zAdd("apple", 1.0);
        assertTrue(added);
        assertEquals(1, sortedSet.zRank("apple"));
    }

    @Test
    void testZAddUpdateExistingMember() {
        SortedSet<String> sortedSet = new SortedSet<>();
        sortedSet.zAdd("apple", 1.0);
        boolean addedAgain = sortedSet.zAdd("apple", 5.0); // Should update score, not add new
        assertFalse(addedAgain);
        assertEquals(1, sortedSet.zRank("apple"));
    }

    @Test
    void testZRemoveMember() {
        SortedSet<String> sortedSet = new SortedSet<>();
        sortedSet.zAdd("apple", 1.0);
        boolean removed = sortedSet.zRem("apple");
        assertTrue(removed);
        assertEquals(0, sortedSet.zRank("apple"));
    }

    @Test
    void testZRemoveNonexistentMember() {
        SortedSet<String> sortedSet = new SortedSet<>();
        boolean removed = sortedSet.zRem("banana");
        assertFalse(removed);
    }

    @Test
    void testZRankOrdering() {
        SortedSet<String> sortedSet = new SortedSet<>();
        sortedSet.zAdd("apple", 2.0);
        sortedSet.zAdd("banana", 1.0);
        sortedSet.zAdd("cherry", 3.0);

        // banana should be rank 1 (lowest score)
        assertEquals(1, sortedSet.zRank("banana"));
        assertEquals(2, sortedSet.zRank("apple"));
        assertEquals(3, sortedSet.zRank("cherry"));
    }

    @Test
    void testZRankNonexistentMember() {
        SortedSet<String> sortedSet = new SortedSet<>();
        assertEquals(0, sortedSet.zRank("unknown"));
    }
}
