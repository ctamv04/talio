package models;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    /**
     * Test if 2 equal are equal
     */
    @Test
    void testEquals() {
        Board board1 = new Board("board");
        Board board2 = new Board("board");
        assertEquals(board1, board2);
    }

    /**
     * Test if 2 non equal ar not equal
     */
    @Test
    void testNotEquals() {
        Board board1 = new Board("board");
        Board board2 = new Board("board3");
        assertNotEquals(board1, board2);
    }

    /**
     * Test if 1 object is equal to itself
     */
    @Test
    void testEqualsSame() {
        Board board1 = new Board("board");
        assertEquals(board1, board1);
    }

    /**
     * Test if 1 object is not equal to null object
     */
    @Test
    void testEqualsNull() {
        Board board1 = new Board("board");
        Board board2 = null;
        assertNotEquals(board1, board2);
    }

    /**
     * Test if 2 equal hashcodes are equal
     */
    @Test
    void testHashCode() {
        Board board1 = new Board("board");
        Board board2 = new Board("board");
        assertEquals(board1.hashCode(), board2.hashCode());
    }

    /**
     * Test if 2 non-equal hashcodes are not equal
     */
    @Test
    void testNotHashCodeEquals() {
        Board board1 = new Board("board");
        Board board2 = new Board("board2");
        assertNotEquals(board1.hashCode(), board2.hashCode());
    }

    /**
     * Tests if the toString method works correctly
     */
    @Test
    void testToString() {
        Board board1 = new Board("board");
        var actual = board1.toString();
        assertTrue(actual.contains("name=board"));
        assertTrue(actual.contains("taskLists=[]"));
        assertTrue(actual.contains("\n"));
    }

    /**
     * Tests if the function gets the correct name
     */
    @Test
    void getName() {
        Board board1 = new Board("board");
        assertEquals(board1.getName(), "board");
    }
    /**
     * Tests if the function gets the correct TaskList list
     */
    @Test
    void getTaskLists() {
        List<TaskList> tasklistList = new ArrayList<>();
        TaskList taskList1 = new TaskList("Name", null);
        tasklistList.add(taskList1);
        Board board1 = new Board("board", tasklistList);
        assertEquals(tasklistList, board1.getTaskLists());
    }

    /**
     * Tests if the function sets the correct TaskList list
     */
    @Test
    void setTaskLists() {
        Board board1 = new Board("board");
        List<TaskList> tasklistList = new ArrayList<>();
        TaskList taskList1 = new TaskList("Name", null);
        tasklistList.add(taskList1);
        board1.setTaskLists(tasklistList);
        assertEquals(tasklistList, board1.getTaskLists());
    }

    /**
     * Tests if the function sets the correct name
     */
    @Test
    void setName() {
        Board board1 = new Board("board");
        board1.setName("DifferentName");
        assertEquals(board1.getName(), "DifferentName");
    }

}