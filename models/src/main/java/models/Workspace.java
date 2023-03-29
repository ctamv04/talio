package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
@Data
public class Workspace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String password;

    @OneToMany
    @JsonIgnore
    private List<Board> boards = new ArrayList<>();

    /**
     * Constructor functions for the Workspace class
     */
    public Workspace() {
        this.password = generatePassword();
    }

    /**
     * Checks if 2 workspaces are equal
     *
     * @param obj the object we check to see if it's equal with the Task List
     * @return returns true or false
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * Hashes a workspace
     *
     * @return returns a hashcode for a Task List
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * A function that gives a String with all the Task List information
     *
     * @return returns a string with all the workspace information
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }

    /**
     * Generates password with length in range [8, 16].
     * Password contains lowercase and uppercase english letters and digits.
     *
     * @return password
     */
    private String generatePassword() {
        int characterNumber = 62;

        Random random = new Random();
        StringBuilder buffer = new StringBuilder();
        int length = random.nextInt(8) + 8;
        for (int i = 0; i < length; i++) {
            int code = random.nextInt(characterNumber);
            if (code < 10) {
                code += 48;
            } else {
                if (code < 36) {
                    code += 65;
                } else {
                    code += 97;
                }
            }
            buffer.append((char) code);
        }

        return buffer.toString();
    }
}
