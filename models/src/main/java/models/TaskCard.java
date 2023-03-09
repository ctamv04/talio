package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class TaskCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIgnore
    private TaskList taskList;

    /**
     * Constructor by giving an existing taskList, but no name or description.
     */
    public TaskCard(TaskList taskList) {
        this.name = "";
        this.description = "";
        this.taskList = taskList;
        taskList.add(this);
    }

    /**
     * Constructor by giving an existing taskList, but no description.
     */
    public TaskCard(String name, TaskList taskList) {
        this.name = name;
        this.description = "";
        this.taskList = taskList;
        taskList.add(this);
    }

    /**
     * Constructor by giving all parameters..
     */
    public TaskCard(String name, String description, TaskList taskList) {
        this.name = name;
        this.description = description;
        this.taskList = taskList;
        taskList.add(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}
