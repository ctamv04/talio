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

//    @OneToMany(
//            mappedBy = "tag",
//            cascade = CascadeType.ALL,
//            orphanRemoval = true
//    )
    @ManyToMany
    @JsonIgnore
    private List<Board> boards = new ArrayList<>();

//    @OneToMany(
//            mappedBy = "tag",
//            cascade = CascadeType.ALL,
//            orphanRemoval = true
//    )
    @ManyToMany
    @JsonIgnore
    private List<TaskCard> tasks = new ArrayList<>();

    private String color="#FFFFFF";

    /**
     * Other constructor functions for the Tag class which takes a taskList
     * @param name name of the Tag
     * @param boards the Board List of the object
     */
    public Tag(String name, List<Board> boards) {
        this.name = name;
        this.boards = boards;
    }

    /**
     * Constructor Method
     * @param name The name of the object
     * @param boards The Board List of the object
     * @param color The Color of the object
     */
    public Tag(String name, List<Board> boards, String color) {
        this.name = name;
        this.boards = boards;
        this.color = color;
    }

    /**
     *
     * @param name
     * @param boards
     * @param tasks
     * @param color
     */
    public Tag(String name, List<Board> boards, List<TaskCard> tasks, String color) {
        this.name = name;
        this.boards = boards;
        this.tasks = tasks;
        this.color = color;
    }

    /**
     *
     * @param name
     * @param boards
     * @param tasks
     */
    public Tag(String name, List<Board> boards, List<TaskCard> tasks) {
        this.name = name;
        this.boards = boards;
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
