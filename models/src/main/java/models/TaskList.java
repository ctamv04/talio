package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
@Data
@NoArgsConstructor
public class TaskList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name="Untitled";
    @OneToMany(
            mappedBy = "taskList",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonIgnore
    private List<TaskCard> taskCards = new ArrayList<>();
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIgnore
    private Board board;


    /**
     * Constructor functions for the TaskList class
     * @param name name of the TaskList
     * @param board the board where the tasklist is located
     */
    public TaskList(String name, Board board) {
        this.name = name;
        this.board = board;
    }

    /**
     * Constructor functions for the TaskList class
     * @param name name of the TaskList
     * @param taskCards Task Cards in the Task List
     * @param board the board where the tasklist is located
     */
    @SuppressWarnings("unused")
    public TaskList(String name, List<TaskCard> taskCards, Board board) {
        this.name = name;
        this.taskCards = taskCards;
        this.board = board;
    }

    /**
     * Checks if 2 Task Lists are equal
     * @param obj the object we check to see if it's equal with the Task List
     * @return returns true or false
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * Hashes a board
     * @return returns a hashcode for a Task List
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * A function that gives a String with all the Task List information
     * @return returns a string with all the Task List information
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}
