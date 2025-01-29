package az.edu.ada.SITE.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "admin")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Admin extends User {

    @Column(nullable = false, unique = true)
    private String adminId;

    @Column(nullable = false, unique = true)
    private String username;

    public Admin(String adminId, String name, String surname, String username, String email, String password) {
        super(name, surname, username, email, password, Role.ADMIN);
        this.adminId = adminId;
    }

}
