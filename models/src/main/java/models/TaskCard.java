package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIgnore
    private TaskList taskList;

    /**
     * Constructor function for the Task Card object with an empty name & description
     * @param taskList the task list where the card is located
     */
    public TaskCard(TaskList taskList) {
        this.name = "";
        this.description = "";
        this.taskList = taskList;
        taskList.add(this);
    }

    /**
     * Constructor function for the Task Card object with an empty description
     * @param name name of the Task Card
     * @param taskList the task list where the card is located
     */
    public TaskCard(String name, TaskList taskList) {
        this.name = name;
        this.description = "";
        this.taskList = taskList;
        taskList.add(this);
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
        taskList.add(this);
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
