package ds;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SkipListTest {
    @Test
    void testInsertAndContains() {
        SkipList<String> skipList = new SkipList<>();
        skipList.insert("apple", 1.0);
        skipList.insert("banana", 2.0);

        assertTrue(skipList.contains("apple", 1.0));
        assertTrue(skipList.contains("banana", 2.0));
        assertFalse(skipList.contains("cherry", 3.0));
        assertEquals(2, skipList.size());
    }

    @Test
    void testInsertSameValueUpdatesScore() {
        SkipList<String> skipList = new SkipList<>();
        skipList.insert("apple", 1.0);
        SkipList.Node<String> node = skipList.insert("apple", 5.0); // Update score

        assertEquals("apple", node.value());
        assertEquals(5.0, node.score());

        // Skip list size must be two, it allows duplicate insertions with same value;
        assertEquals(2, skipList.size());
    }

    @Test
    void testDeleteNode() {
        SkipList<String> skipList = new SkipList<>();
        skipList.insert("apple", 1.0);
        skipList.insert("banana", 2.0);

        SkipList.Node<String> deletedNode = skipList.delete("apple", 1.0);
        assertNotNull(deletedNode);
        assertFalse(skipList.contains("apple", 1.0));
        assertEquals(1, skipList.size());

        assertNull(skipList.delete("apple", 1.0)); // Already deleted
        assertEquals(1, skipList.size());
    }

    @Test
    void testRankFunction() {
        SkipList<String> skipList = new SkipList<>();
        skipList.insert("apple", 10.0);
        skipList.insert("banana", 20.0);
        skipList.insert("cherry", 15.0);

        // Sorted order: apple(10), cherry(15), banana(20)
        assertEquals(1, skipList.rank("apple", 10.0));
        assertEquals(2, skipList.rank("cherry", 15.0));
        assertEquals(3, skipList.rank("banana", 20.0));
        assertEquals(0, skipList.rank("date", 30.0)); // Not present
    }

    @Test
    void testSpanUpdateWithMultipleInsertsDeletes() {
        SkipList<String> skipList = new SkipList<>();

        // Insert several elements with various scores
        skipList.insert("apple", 5.0);
        skipList.insert("banana", 10.0);
        skipList.insert("cherry", 7.0);
        skipList.insert("date", 7.0);
        skipList.insert("elderberry", 10.0);

        // Check size
        assertEquals(5, skipList.size());

        // Check ranks before deletion
        assertEquals(1, skipList.rank("apple", 5.0));
        assertEquals(2, skipList.rank("cherry", 7.0));
        assertEquals(3, skipList.rank("date", 7.0));
        assertEquals(4, skipList.rank("banana", 10.0));
        assertEquals(5, skipList.rank("elderberry", 10.0));

        // Delete a middle element (to test span update)
        skipList.delete("cherry", 7.0);
        assertEquals(4, skipList.size());

        // Rank after deletion should adjust accordingly
        assertEquals(1, skipList.rank("apple", 5.0));
        assertEquals(2, skipList.rank("date", 7.0));
        assertEquals(3, skipList.rank("banana", 10.0));
        assertEquals(4, skipList.rank("elderberry", 10.0));
        assertEquals(0, skipList.rank("cherry", 7.0)); // Deleted node

        // Delete head element
        skipList.delete("apple", 5.0);
        assertEquals(3, skipList.size());

        // Rank reflects updated list
        assertEquals(1, skipList.rank("date", 7.0));
        assertEquals(2, skipList.rank("banana", 10.0));
        assertEquals(3, skipList.rank("elderberry", 10.0));

        // Insert back deleted element with updated score
        skipList.insert("cherry", 6.0);
        assertEquals(4, skipList.size());

        // Check rank after re-insertion
        assertEquals(1, skipList.rank("cherry", 6.0));
        assertEquals(2, skipList.rank("date", 7.0));
        assertEquals(3, skipList.rank("banana", 10.0));

        // Stress test multiple deletions in a row
        skipList.delete("date", 7.0);
        skipList.delete("cherry", 6.0);

        assertEquals(2, skipList.size());
        assertEquals(1, skipList.rank("banana", 10.0));
        assertEquals(2, skipList.rank("elderberry", 10.0));

        // Delete last elements
        skipList.delete("banana", 10.0);
        skipList.delete("elderberry", 10.0);

        assertEquals(0, skipList.size());
    }

    @Test
    void testInsertWithSameScores() {
        SkipList<String> skipList = new SkipList<>();
        skipList.insert("b", 2.0);
        skipList.insert("a", 2.0);
        skipList.insert("c", 2.0);
        skipList.insert("d", 2.0);
        skipList.insert("e", 2.0);
        skipList.insert("f", 2.0);
        skipList.insert("g", 2.0);

        // Check all inserted
        assertTrue(skipList.contains("a", 2.0));
        assertTrue(skipList.contains("b", 2.0));
        assertTrue(skipList.contains("c", 2.0));
        assertEquals(7, skipList.size());

        // Lexicographic order: a, b, c; ranks accordingly
        assertEquals(1, skipList.rank("a", 2.0));
        assertEquals(2, skipList.rank("b", 2.0));
        assertEquals(3, skipList.rank("c", 2.0));
        assertEquals(4, skipList.rank("d", 2.0));
        assertEquals(5, skipList.rank("e", 2.0));
        assertEquals(6, skipList.rank("f", 2.0));
        assertEquals(7, skipList.rank("g", 2.0));
    }

    @Test
    void testLevelsChange() {
        SkipList<Integer> skipList = new SkipList<>();
        for (int i = 0; i < 100; i++) {
            skipList.insert(i, i);
        }
        int initialLevels = skipList.levels();

        // Delete all
        for (int i = 0; i < 100; i++) {
            skipList.delete(i, i);
        }
        assertEquals(0, skipList.size());
        assertTrue(skipList.levels() <= initialLevels);
    }

    @Test
    void testNullValueThrowsException() {
        SkipList<String> skipList = new SkipList<>();
        assertThrows(NullPointerException.class, () -> skipList.insert(null, 1.0));
        assertThrows(NullPointerException.class, () -> skipList.contains(null, 1.0));
        assertThrows(NullPointerException.class, () -> skipList.delete(null, 1.0));
    }
}