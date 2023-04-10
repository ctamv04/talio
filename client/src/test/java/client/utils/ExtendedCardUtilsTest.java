package client.utils;

import client.controllers.MainCtrl;
import javafx.scene.paint.Color;
import models.Tag;
import models.TaskCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ExtendedCardUtilsTest {

    @Mock
    private ServerUtils serverUtils;
    @Mock
    private MainCtrl mainCtrl;
    @InjectMocks
    private ExtendedCardUtils sut;

    Map<String, Boolean> subtasks;
    Set<Tag> tags;
    TaskCard card;

    @BeforeEach
    void setup(){

        serverUtils = Mockito.mock(ServerUtils.class);
        mainCtrl = Mockito.mock(MainCtrl.class);

        this.sut = new ExtendedCardUtils(serverUtils, mainCtrl);

        card = new TaskCard("title", "description", null);
        subtasks = new HashMap<>();
        subtasks.put("1", false);
        subtasks.put("2", true);

        tags = new HashSet<>();
        tags.add(new Tag());
    }

    @Test
    void save() {

        sut.save("title2", "description2", subtasks, tags, card);

        assertEquals(card.getDescription(), "description2");
        assertEquals(card.getTags(), tags);
        assertEquals(card.getName(), "title2");
        assertEquals(card.getSubs(), subtasks);
    }

    @Test
    void saveNoTitle() {

        sut.save("", "description2", subtasks, tags, card);

        assertEquals(card.getName(), "Untitled");

    }

    @Test
    void colorTest1() {

        Color color = Color.BLUE;

        assertEquals(sut.colorConverter(color), "rgba(0,0,255,1.0)");
    }

    @Test
    void colorTest2() {

        Color color = Color.PURPLE;

        assertEquals(sut.colorConverter(color), "rgba(128,0,128,1.0)");
    }
}