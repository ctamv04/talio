package models;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskListTest {
    // --------------------------------------------
    // Touch all Constructors                     |
    // --------------------------------------------

    /**
     * Test if the no params constructor works
     */
    @Test
    void testConstructor0params() {
        TaskList taskList = new TaskList();
        assertNotNull(taskList);
    }

    /***
     * Test if the constructor with one param works
     */
    @Test
    void testConstructor2params() {
        Board board = new Board("board");
        TaskList taskList = new TaskList("Name", board);
        assertNotNull(taskList);
    }

    /***
     * Test if the constructor with three params works
     */
    @Test
    void testConstructor3params() {
        TaskCard taskCard = new TaskCard("Name", "Description", new TaskList(), 2);
        Board board = new Board("board");
        TaskList taskList = new TaskList("Name", new ArrayList<TaskCard>(), board);
        assertNotNull(taskList);
    }

    // --------------------------------------------
    // Getters and Setters                        |
    // --------------------------------------------

    /***
     * Test if the function gets the correct task cards
     */
    @Test
    void testGetTaskCards() {
        TaskCard taskCard=new TaskCard("Name","Description",new TaskList(),2);
        Board board=new Board("board");
        TaskList taskList=new TaskList("Name",new ArrayList<TaskCard>(),board);
        assertEquals(taskList.getTaskCards(),new ArrayList<TaskCard>());
    }

    /***
     * Test if the function sets the correct task cards
     */
    @Test
    void testSetTaskCards() {
        TaskCard taskCard=new TaskCard("Name","Description",new TaskList(),2);
        Board board=new Board("board");
        TaskList taskList=new TaskList("Name",new ArrayList<TaskCard>(),board);
        List<TaskCard> taskCards=new ArrayList<>();
        taskCards.add(taskCard);
        taskList.setTaskCards(taskCards);
        assertEquals(taskList.getTaskCards(),taskCards);
    }

    // Please paste your getters and setters here, if I do it I steal your code :(

    // --------------------------------------------
    // HashCode, Equals and ToString              |
    // --------------------------------------------

    /**
     * Test if 2 equal are equal
     */
    @Test
    void testEquals() {
        Board board = new Board("board");
        TaskList taskList1 = new TaskList("Name", board);
        TaskList taskList2 = new TaskList("Name", board);
        assertEquals(taskList1, taskList2);
    }

    /**
     * Test if 2 tasklists with non-equal names are not equal
     */
    @Test
    void testNotEqualsName() {
        Board board = new Board("board");
        TaskList taskList1 = new TaskList("Name2", board);
        TaskList taskList2 = new TaskList("Name", board);
        assertNotEquals(taskList1, taskList2);
    }

    /**
     * Test if 2 tasklists with non-equal boards are not equal
     */
    @Test
    void testNotEqualsBoard() {
        Board board1 = new Board("board");
        Board board2 = new Board("board2");
        TaskList taskList1 = new TaskList("Name", board1);
        TaskList taskList2 = new TaskList("Name", board2);
        assertNotEquals(taskList1, taskList2);
    }

    /**
     * Test if 1 object is equal to itself
     */
    @Test
    void testEqualsSame() {
        Board board1 = new Board("board");
        TaskList taskList1 = new TaskList("Name", board1);
        TaskList taskList2 = taskList1;
        assertEquals(taskList1, taskList2);
        assertEquals(taskList1, taskList1);
    }

    /**
     * Test if 1 object is not equal to null object
     */
    @Test
    void testEqualsNull() {
        Board board1 = new Board("board");
        TaskList taskList1 = new TaskList("Name", board1);
        TaskList taskList2 = null;
        assertNotEquals(taskList1, taskList2);
    }

    /**
     * Test if 2 equal hashcodes are equal
     */
    @Test
    void testHashCode() {
        Board board1 = new Board("board");
        TaskList taskList1 = new TaskList("Name", board1);
        TaskList taskList2 = new TaskList("Name", board1);
        assertEquals(taskList1.hashCode(), taskList2.hashCode());
    }

    /**
     * Test if 2 tasklists hashes with non-equal names are not equal
     */
    @Test
    void testNotHashCodeEqualsName() {
        Board board1 = new Board("board");
        TaskList taskList1 = new TaskList("Name", board1);
        TaskList taskList2 = new TaskList("Name2", board1);
        assertNotEquals(taskList1.hashCode(), taskList2.hashCode());
    }

    /**
     * Test if 2 tasklists hashes with non-equal boards are not equal
     */
    @Test
    void testNotHashCodeEqualsBoard() {
        Board board1 = new Board("board");
        Board board2 = new Board("board2");
        TaskList taskList1 = new TaskList("Name", board1);
        TaskList taskList2 = new TaskList("Name", board2);
        assertNotEquals(taskList1.hashCode(), taskList2.hashCode());
    }

    /**
     * Tests if the toString method works correctly
     */
    @Test
    void testToString() {
        Board board = new Board("board");
        TaskList taskList = new TaskList("Name", board);
        var actual = taskList.toString();
        assertTrue(actual.contains("name=board"));
        assertTrue(actual.contains("name=Name"));
        assertTrue(actual.contains("taskLists=[]"));
        assertTrue(actual.contains("taskCards=[]"));
        assertTrue(actual.contains("\n"));
    }

    /**
     * Tests if the function gets the correct name
     */
    @Test
    void getName() {
        Board board = new Board("board");
        TaskList taskList = new TaskList("Name", board);
        assertEquals("Name", taskList.getName());
    }

    /**
     * Tests if the function gets the correct board
     */
    @Test
    void getBoard() {
        Board board = new Board("Board");
        TaskList tasklist = new TaskList("Tasklist", board);
        assertEquals(board, tasklist.getBoard());
    }

    /**
     * Tests if the function sets the correct board
     */
    @Test
    void setBoard() {
        TaskList tasklist = new TaskList("Tasklist", null);
        Board board = new Board("Board");
        tasklist.setBoard(board);
        assertEquals(tasklist.getBoard(), board);
    }

    /**
     * Tests if the function sets the correct name
     */
    @Test
    void setName() {
        Board board = new Board("board");
        TaskList taskList = new TaskList("Name", board);
        taskList.setName("NotAName");
        assertEquals("NotAName", taskList.getName());
    }

}