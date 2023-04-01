package models;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TagTest {
    // --------------------------------------------
    // Touch all Constructors                     |
    // --------------------------------------------

    @Test
    void testConstructor0params() {
        Tag tag=new Tag();
        assertNotNull(tag);
    }

    @Test
    void testConstructorNameBoard() {
        Tag tag=new Tag("tag",new Board());
        assertNotNull(tag);
    }

    @Test
    void testConstructorNameBoardTasks() {
        Tag tag=new Tag("tag",new Board(),new ArrayList<TaskCard>());
        assertNotNull(tag);
    }

    @Test
    void testConstructorNameBoardColor() {
        Tag tag=new Tag("tag",new Board(),"#000000");
        assertNotNull(tag);
    }

    @Test
    void testConstructorNameBoardTasksColor() {
        Tag tag=new Tag("tag",new Board(),new ArrayList<TaskCard>(),"#000000");
        assertNotNull(tag);
    }

    // --------------------------------------------
    // Getters and Setters                        |
    // --------------------------------------------
    /**
     * Tests if the function gets the correct name
     */
    @Test
    void getName() {
        Tag tag=new Tag();
        assertEquals(tag.getName(),"Untitled");
    }

    /***
     * Tests if the function sets the correct name
     */
    @Test
    void setName() {
        Tag tag=new Tag();
        tag.setName("tag");
        assertEquals(tag.getName(),"tag");
    }

    /***
     * Tests if the function gets the correct boards
     */
    @Test
    void getBoards() {
        Tag tag=new Tag();
        assertEquals(tag.getBoard(),null);
    }

    /***
     * Tests if the function sets the correct boards
     */
    @Test
    void setBoards() {
        Tag tag=new Tag();
        Board board=new Board("Board One");
        tag.setBoard(board);
        assertEquals(tag.getBoard(),board);
    }

    /***
     * Tests if the function gets the correct tasks
     */
    @Test
    void getTasks() {
        Tag tag=new Tag();
        assertEquals(tag.getTasks(),new ArrayList<>());
    }

    /***
     * Tests if the function sets the correct tasks
     */
    @Test
    void setTasks() {
        Tag tag=new Tag();
        List<TaskCard> tasks=new ArrayList<>();
        tasks.add(new TaskCard());
        tag.setTasks(tasks);
        assertEquals(tag.getTasks(),tasks);
    }

    /***
     * Tests if the function gets the correct color
     */
    @Test
    void getColor() {
        Tag tag=new Tag();
        assertEquals(tag.getColor(),"#FFFFFF");
    }

    /***
     * Tests if the function sets the correct color
     */
    @Test
    void setColor() {
        Tag tag=new Tag();
        tag.setColor("#000000");
        assertEquals(tag.getColor(),"#000000");
    }

    // --------------------------------------------
    // Equals, Hashcode & toString                |
    // --------------------------------------------
    /**
     * Test if 2 equal are equal
     */
    @Test
    void testEquals() {
        Tag tag1=new Tag();
        Tag tag2=new Tag();
        assertEquals(tag1,tag2);
    }

    /**
     * Test if 2 non equal ar not equal
     */
    @Test
    void testNotEquals() {
        Tag tag1=new Tag();
        Tag tag2=new Tag();
        tag2.setName("tag2");
        assertNotEquals(tag1,tag2);
    }

    /**
     * Test if 1 object is equal to itself
     */
    @Test
    void testEqualsSame() {
        Tag tag=new Tag();
        assertEquals(tag,tag);
    }

    /**
     * Test if 1 object is not equal to null object
     */
    @Test
    void testEqualsNull() {
        Tag tag=new Tag();
        assertNotEquals(tag,null);
    }

    /**
     * Test if 2 equal hashcodes are equal
     */
    @Test
    void testHashCode() {
        Tag tag1=new Tag();
        Tag tag2=new Tag();
        assertEquals(tag1.hashCode(),tag2.hashCode());
    }

    /**
     * Test if 2 non-equal hashcodes are not equal
     */
    @Test
    void testNotHashCodeEquals() {
        Tag tag1=new Tag();
        Tag tag2=new Tag();
        tag2.setName("tag2");
        assertNotEquals(tag1.hashCode(),tag2.hashCode());
    }

    /**
     * Tests if the toString method works correctly
     */
    @Test
    void testToString() {
        Tag tag=new Tag();
        var actual=tag.toString();
        assertTrue(actual.contains("board=<null>"));
        assertTrue(actual.contains("\n"));
        assertTrue(actual.contains("color=#FFFFFF"));
        assertTrue(actual.contains("id=<null>"));
        assertTrue(actual.contains("name=Untitled"));
        assertTrue(actual.contains("tasks=[]"));
    }

}