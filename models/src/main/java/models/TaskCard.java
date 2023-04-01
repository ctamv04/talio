package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import javax.persistence.*;

import java.util.*;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
@Data
@NoArgsConstructor
public class TaskCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name="Untitled";
    private String description="Fill the description";
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIgnore
    private TaskList taskList;

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    private int position=0;
    @ElementCollection
    private Map<String, Boolean> subs = new HashMap<>();
    private String fontID = "#000000";
    private String backID = "#FFFFFF";

    /**
     * Constructor function for the Task Card object with an empty name & description
     * @param taskList the task list where the card is located
     */
    public TaskCard(TaskList taskList) {
        this.taskList = taskList;
    }

    /**
     * Constructor function for the Task Card object with an empty description
     * @param name name of the Task Card
     * @param taskList the task list where the card is located
     */
    public TaskCard(String name, TaskList taskList) {
        this.name = name;
        this.taskList = taskList;
    }

    /**
     * Constructor function for the Task Card object
     * @param name name of the Task Card
     * @param description description of the Task Card
     * @param taskList the task list where the card is located
     */
    public TaskCard(String name, String description, TaskList taskList) {
        this.name = name;
        this.description = description;
        this.taskList = taskList;
    }

    /**
     * Constructor Method
     * @param position The position of the object
     */
    public TaskCard(int position) {
        this.position = position;
    }

    /**
     * Constructor Method
     * @param name The name of the object
     * @param description The description of the object
     * @param taskList The taskList of the object
     * @param position The position of the object
     */
    public TaskCard(String name, String description, TaskList taskList, int position) {
        this.name = name;
        this.description = description;
        this.taskList = taskList;
        this.position = position;
    }

    /**
     * Constructor Method
     * @param name The name of the object
     * @param taskList The taskList of the object
     * @param position The position of the object
     */
    public TaskCard(String name, TaskList taskList, int position) {
        this.name = name;
        this.taskList = taskList;
        this.position = position;
    }

    public Map<String, Boolean> getSubs() {
        return subs;
    }

    public void setSubs(Map<String, Boolean> subs) {
        this.subs = subs;
    }

    public String getFontID() {
        return fontID;
    }

    public void setFontID(String fontID) {
        this.fontID = fontID;
    }

    public String getBackID() {
        return backID;
    }

    public void setBackID(String backID) {
        this.backID = backID;
    }

    /**
     * Checks if 2 Task Cards are equal
     * @param obj the object we check to see if it's equal with the Task Card
     * @return returns true or false
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * Hashes a board
     * @return returns a hashcode for a Task Card
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * A function that gives a String with all the Task Card information
     * @return returns a string with all the Task Card information
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }

}
