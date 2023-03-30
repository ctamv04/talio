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
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name="Untitled";
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIgnore
    private Board board;
    @ManyToMany(cascade = {CascadeType.ALL})
    @JsonIgnore
    private List<TaskCard> tasks = new ArrayList<>();

    private String color="#FFFFFF";

    /**
     * Other constructor functions for the Tag class which takes a taskList
     * @param name name of the Tag
     * @param board the Board of the object
     */
    public Tag(String name, Board board) {
        this.name = name;
        this.board = board;
    }

    /**
     * Constructor Method
     * @param name The name of the object
     * @param board The Board of the object
     * @param color The Color of the object
     */
    public Tag(String name, Board board, String color) {
        this.name = name;
        this.board = board;
        this.color = color;
    }

    /**
     *
     * @param name
     * @param board
     * @param tasks
     * @param color
     */
    public Tag(String name, Board board, List<TaskCard> tasks, String color) {
        this.name = name;
        this.board = board;
        this.tasks = tasks;
        this.color = color;
    }

    /**
     *
     * @param name
     * @param board
     * @param tasks
     */
    public Tag(String name, Board board, List<TaskCard> tasks) {
        this.name = name;
        this.board = board;
        this.tasks = tasks;
    }

    /**
     * Checks if 2 Tags are equal
     * @param obj the object we check to see if it's equal with the Tag
     * @return returns true or false
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * Hashes a Tag
     * @return returns a hashcode for a Tag
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * A function that gives a String with all the Tag information
     * @return returns a string with all the Tag information
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}
