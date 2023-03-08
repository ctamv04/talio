package models;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import javax.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;

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
    private List<TaskList> taskLists=new ArrayList<>();

    public Board(String name) {
        this.name=name;
    }

    @SuppressWarnings("unused")
    public Board(String name, List<TaskList> taskLists) {
        this.name = name;
        this.taskLists = taskLists;
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
