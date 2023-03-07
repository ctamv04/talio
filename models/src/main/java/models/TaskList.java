package models;

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
    private String name;
    @OneToMany(cascade = CascadeType.MERGE)
    private List<TaskCard> taskCards;

    public TaskList(String name) {
        this.name = name;
        this.taskCards=new ArrayList<>();
    }
    public TaskList(String name, List<TaskCard> taskCards) {
        this.name = name;
        this.taskCards = taskCards;
    }
    @Override
    public boolean equals(Object obj) {return EqualsBuilder.reflectionEquals(this, obj);}

    @Override
    public int hashCode() {return HashCodeBuilder.reflectionHashCode(this);}

    @Override
    public String toString() {return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);}
}
