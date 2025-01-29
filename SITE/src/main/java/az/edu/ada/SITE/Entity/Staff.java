package az.edu.ada.SITE.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "staff")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Staff extends User {

    @Column(nullable = false, unique = true)
    private String staffId;  // Unique Staff Identifier

    @Column(nullable = false)
    private String position;  // Example: "Professor", "Lecturer"

    @Column(nullable = false)
    private String department;  // Example: "Computer Science", "Engineering"

    @OneToMany(mappedBy = "supervisor", cascade = CascadeType.ALL)
    private List<Project> projects = new ArrayList<>();

    public Staff(String email, String password, String staffId, String position, String department) {
        super(email, password, Role.STAFF);
        this.staffId = staffId;
        this.position = position;
        this.department = department;
    }
}
