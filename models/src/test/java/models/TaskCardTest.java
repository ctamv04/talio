package models;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class TaskCardTest {
	@Test
	public void checkConstructorOnlyTaskList() {
		var t = new TaskCard(new TaskList());
		assertEquals("", t.getName());
		assertEquals("", t.getDescription());
	}

	@Test
	public void checkConstructorNameAndTaskList() {
		var t = new TaskCard("name", new TaskList());
		assertEquals("name", t.getName());
		assertEquals("", t.getDescription());
	}

	@Test
	public void checkConstructorNameDescriptionAndTaskList() {
		var t = new TaskCard("name", "description", new TaskList());
		assertEquals("name", t.getName());
		assertEquals("description", t.getDescription());
	}

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
