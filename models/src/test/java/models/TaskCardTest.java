package models;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TaskCardTest {
	// --------------------------------------------
	// Touch all Constructors                     |
	// --------------------------------------------
	@Test
	public void checkConstructorOnlyTaskList() {
		var t = new TaskCard(new TaskList());
		assertEquals("Untitled", t.getName());
		assertEquals("Fill the description", t.getDescription());
	}

	@Test
	public void checkConstructorNameAndTaskList() {
		var t = new TaskCard("name", new TaskList());
		assertEquals("name", t.getName());
		assertEquals("Fill the description", t.getDescription());
	}

	@Test
	public void checkConstructorNameDescriptionAndTaskList() {
		var t = new TaskCard("name", "description", new TaskList());
		assertEquals("name", t.getName());
		assertEquals("description", t.getDescription());
	}

	@Test
	public void checkConstructorNameDescriptionTasklistAndPosition() {
		var t=new TaskCard("name","description",new TaskList(),1);
		assertEquals("name",t.getName());
		assertEquals("description",t.getDescription());
		assertEquals(1,t.getPosition());
	}

	@Test
	public void checkConstructorPosition() {
		var t=new TaskCard(1);
		assertEquals(1,t.getPosition());
	}

	@Test
	public void checkConstructorNameTasklistAndPosition() {
		var t=new TaskCard("name",new TaskList(),1);
		assertEquals("name",t.getName());
		assertEquals(1,t.getPosition());
	}

	// --------------------------------------------
	// Getters and Setters                        |
	// --------------------------------------------
	@Test
	public void getSetName() {
		var t = new TaskCard();
		t.setName("namename");
		assertEquals("namename", t.getName());
	}

	@Test
	public void getSetDescription() {
		var t = new TaskCard();
		t.setDescription("descdesc");
		assertEquals("descdesc", t.getDescription());
	}

	@Test
	public void getSetTaskList() {
		var t = new TaskCard();
		var l = new TaskList("name", new ArrayList<>(), new Board());

		t.setTaskList(l);
		assertEquals(l, t.getTaskList());
	}

	@Test
	public void getSetTags() {
		var t=new TaskCard();
		var l=new ArrayList<Tag>();
		l.add(new Tag("tag",new ArrayList<>(),new ArrayList<>(),"#000000"));
		t.setTags(l);
		assertEquals(l,t.getTags());
	}

	@Test
	public void getSetPosition() {
		var t=new TaskCard();
		t.setPosition(1);
		assertEquals(1,t.getPosition());
	}

	@Test
	public void getSetSubs() {
		var t=new TaskCard();
		var l=new HashMap<String, Boolean>();
		l.put("sub",true);
		t.setSubs((Map<String, Boolean>) l);
		assertEquals(l,t.getSubs());
	}

	@Test
	public void getSetFontID() {
		var t=new TaskCard();
		t.setFontID("fontID");
		assertEquals("fontID",t.getFontID());
	}
	/**
	 *  Tests the setId function
	 */
	@Test
	void  setID() {
		TaskCard taskCard = new TaskCard();
		taskCard.setId(1L);
		assertEquals(1L, taskCard.getId());
	}
	@Test
	public void getSetBackID() {
		var t=new TaskCard();
		t.setBackID("backID");
		assertEquals("backID",t.getBackID());
	}

	// --------------------------------------------
	// Equals, HashCode and ToString              |
	// --------------------------------------------

	@Test
	public void equalsHashCode() {
		var a = new TaskCard("a", new TaskList());
		var b = new TaskCard("a", new TaskList());
		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
	}

	@Test
	public void notEqualsHashCode() {
		var a = new TaskCard("a", new TaskList());
		var b = new TaskCard("b", new TaskList());
		assertNotEquals(a, b);
		assertNotEquals(a.hashCode(), b.hashCode());
	}

	@Test
	public void hasToString() {
		var actual = new TaskCard("a", new TaskList()).toString();
		assertTrue(actual.contains(TaskCard.class.getSimpleName()));
		assertTrue(actual.contains("\n"));
		assertTrue(actual.contains("name"));
	}
}
