package models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
@Data
@NoArgsConstructor
public class Board {
    @Id
    @GenericGenerator(name = "sequence_board_id", strategy = "generators.BoardIdGenerator")
    @GeneratedValue(generator = "sequence_board_id")
    private Long id;
    private String name;
    @OneToMany(
            mappedBy = "board",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<TaskList> taskLists = new ArrayList<>();

    /**
     * Constructor functions for the Board class
     * @param name name of the board
     */
    public Board(String name) {
        this.name = name;
    }

    /**
     * Other constructor functions for the Board class. Currently unused
     * @param name name of the board
     * @param taskLists List with all the tasklists
     */
    @SuppressWarnings("unused")
    public Board(String name, List<TaskList> taskLists) {
        this.name = name;
        this.taskLists = taskLists;
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
