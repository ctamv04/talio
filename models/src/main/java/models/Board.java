package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
@Data
@NoArgsConstructor
public class Board {
    @Id
    @GenericGenerator(name = "sequence_board_id", strategy = "generators.BoardIdGenerator")
    @GeneratedValue(generator = "sequence_board_id")
    private Long id;
    private String name="Untitled";
    @OneToMany(
            mappedBy = "board",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonIgnore
    private List<TaskList> taskLists = new ArrayList<>();

//    @OneToMany(
//            mappedBy = "board",
//            cascade = CascadeType.ALL,
//            orphanRemoval = true
//    )
    @ManyToMany(cascade = {CascadeType.ALL})
    @JsonIgnore
    private List<Tag> tags = new ArrayList<>();
    private String backgroundColor="#FFFFFF";
    private String fontColor="#000000";

    /**
     * Constructor functions for the Board class
     * @param name name of the board
     */
    public Board(String name) {
        this.name = name;
    }

    /**
     * Other constructor functions for the Board class which takes a taskList
     * @param name name of the board
     * @param taskLists List with all the tasklists
     */
    public Board(String name, List<TaskList> taskLists) {
        this.name = name;
        this.taskLists = taskLists;
    }

    /**
     * Constructor Method
     * @param name The name of the object
     * @param taskLists The taskLists of the object
     * @param backgroundColor The backgroundColor of the object
     * @param fontColor The fontColor of the object
     */
    public Board(String name, List<TaskList> taskLists, String backgroundColor, String fontColor) {
        this.name = name;
        this.taskLists = taskLists;
        this.backgroundColor = backgroundColor;
        this.fontColor = fontColor;
    }

    /**
     * Checks if 2 boards are equal
     * @param obj the object we check to see if it's equal with the board
     * @return returns true or false
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * Hashes a board
     * @return returns a hashcode for a board
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * A function that gives a String with all the board information
     * @return returns a string with all the board information
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}
