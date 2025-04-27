package az.edu.ada.SITE.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a staff member in the system. A staff member is
 * associated with a department, has a position,
 * and is responsible for supervising projects.
 */
@Entity
@Table(name = "staff")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Staff extends User {

    /** Unique identifier for the staff member. */
    @Column(nullable = false, unique = true)
    private String staffId;

    /** The position held by the staff member (e.g., Professor, Lecturer). */
    @Column(nullable = false)
    private String position;

    /** The department to which the staff member belongs. */
    @Column(nullable = false)
    private String department;

    /**
     * List of projects supervised by the staff member.
     * This establishes a one-to-many relationship between staff and projects.
     */
    @OneToMany(mappedBy = "supervisor", cascade = CascadeType.ALL)
    private List<Project> projects = new ArrayList<>();

    /**
     * Constructor to initialize a staff member with specific details.
     * 
     * @param staffId    The unique ID of the staff member.
     * @param name       The name of the staff member.
     * @param surname    The surname of the staff member.
     * @param email      The email address of the staff member.
     * @param password   The password for the staff member.
     * @param position   The position of the staff member.
     * @param department The department to which the staff member belongs.
     */
    public Staff(String staffId, String name, String surname, String email, String password,
            String position, String department) {
        super(name, surname, email, password, Role.STAFF);
        this.staffId = staffId;
        this.position = position;
        this.department = department;
    }
}
