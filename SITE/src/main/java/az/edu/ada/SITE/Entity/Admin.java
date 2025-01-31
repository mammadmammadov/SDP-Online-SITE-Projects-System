package az.edu.ada.SITE.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "admin")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Admin extends User {

    @Column(nullable = false, unique = true)
    private String adminId;

    public Admin(String adminId, String name, String surname, String email, String password) {
        super(name, surname, email, password, Role.ADMIN);
        this.adminId = adminId;
    }
}
