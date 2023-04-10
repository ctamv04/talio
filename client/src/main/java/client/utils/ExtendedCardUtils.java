package client.utils;

import client.controllers.MainCtrl;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.scene.paint.Color;
import models.Tag;
import models.TaskCard;

import java.util.*;

public class ExtendedCardUtils {

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private TaskCard card;
    private Map<String, Boolean> tempSubs = new HashMap<>();
    private List<Tag> taskTags = new ArrayList<>();
    private List<Tag> boardTags = new ArrayList<>();
    private boolean editFlag = false;

    /**
     * Instantiation of ExtendedCardUtils using Dependency Injection
     *
     * @param serverUtils ServerUtils DI
     * @param mainCtrl MainCtrl DI
     */
    @Inject
    public ExtendedCardUtils(ServerUtils serverUtils, MainCtrl mainCtrl) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Saves new information to TaskCard
     *
     * @param newTitle New title from appropriate JavaFX component
     * @param newDescription New description from appropriate JavaFX component
     * @param newSubs New SubTasks from appropriate JavaFX component
     * @param newTags New Tags from appropriate JavaFX component
     * @param card Card object
     */
    public void save(String newTitle, String newDescription, Map<String, Boolean> newSubs, Set<Tag> newTags, TaskCard card){
        if(!newTitle.isBlank()) {
            card.setName(newTitle);
        } else{
            card.setName("Untitled");
        }

        if(!newDescription.equals(card.getDescription()))
            card.setDescription(newDescription);

        card.setSubs(newSubs);
        card.setTags(newTags);

        serverUtils.updateTaskCard(card.getId(), card);
    }

    /**
     * Method for converting Java Color classes to RGB values
     *
     * @param color input Color object
     * @return String containing hex colour code
     */
    public String colorConverter(Color color){

        return "rgba(" + Math.round(255 * color.getRed()) + "," + Math.round(255 * color.getGreen()) + "," + Math.round(255 * color.getBlue()) +
                "," + color.getOpacity() + ")";
    }
}
