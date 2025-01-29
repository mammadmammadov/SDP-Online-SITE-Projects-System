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
    private String adminId;  // Unique Staff Identifier


    public Admin(String email, String password, String adminId) {
        super(email, password, Role.ADMIN);
        this.adminId = adminId;
    }


}
