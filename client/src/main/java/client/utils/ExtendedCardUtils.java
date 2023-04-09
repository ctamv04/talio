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
     * @param title New title from appropriate JavaFX component
     * @param description New description from appropriate JavaFX component
     * @param tempSubs New SubTasks from appropriate JavaFX component
     * @param taskTags New Tags from appropriate JavaFX component
     * @param card Card object
     */
    public void save(String title, String description, Map<String, Boolean> tempSubs, Set<Tag> taskTags, TaskCard card){
        if(!title.isBlank()) {
            card.setName(title);
        } else{
            card.setName("Untitled");
        }

        if(!description.equals(card.getDescription()))
            card.setDescription(description);

        card.setSubs(tempSubs);
        card.setTags(taskTags);

        serverUtils.updateTaskCard(card.getId(), card);
    }

    /**
     * Method for converting Java Color classes to CSS compatible hex colour codes
     *
     * @param color input Color object
     * @return String containing hex colour code
     */
    public String colorConverter(Color color){

        //credit: http://www.java2s.com/example/java/javafx/javafx-color-to-css-color.html

        return "rgba(" + Math.round(255 * color.getRed()) + ","
                + Math.round(255 * color.getGreen()) + ","
                + Math.round(255 * color.getBlue()) + ","
                + color.getOpacity() + ")";
    }
}
