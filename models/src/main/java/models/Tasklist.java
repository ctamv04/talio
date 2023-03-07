package models;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.Objects;

//Temporary model class
@Entity
public class Tasklist {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    public String name;
//    @OneToMany
    public long boardid;

    public Tasklist(long id, String name, long boardid) {
        this.id = id;
        this.name = name;
        this.boardid = boardid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tasklist tasklist = (Tasklist) o;

        if (id != tasklist.id) return false;
        if (boardid != tasklist.boardid) return false;
        return Objects.equals(name, tasklist.name);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (int) (boardid ^ (boardid >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Tasklist{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", boardid=" + boardid +
                '}';
    }
}
