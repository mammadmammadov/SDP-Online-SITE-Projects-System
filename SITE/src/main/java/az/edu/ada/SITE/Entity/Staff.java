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
    private String staffId;

    @Column(nullable = false)
    private String position;

    @Column(nullable = false)
    private String department;

    @OneToMany(mappedBy = "supervisor", cascade = CascadeType.ALL)
    private List<Project> projects = new ArrayList<>();

    public Staff(String staffId, String name, String surname, String email, String password,
            String position,
            String department) {
        super(name, surname, email, password, Role.STAFF);
        this.staffId = staffId;
        this.position = position;
        this.department = department;
    }
}
