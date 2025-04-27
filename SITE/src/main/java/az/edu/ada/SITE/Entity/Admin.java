package az.edu.ada.SITE.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Entity class representing an Administrator (Admin) user.
 * Extends from the {@link User} class to inherit common user attributes.
 */
@Entity
@Table(name = "admin")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Admin extends User {

    /** Unique identifier for the administrator. */
    @Column(nullable = false, unique = true)
    private String adminId;

    /**
     * Constructor for creating an Admin entity with the specified values.
     * Inherits common properties from the User class and sets the role to ADMIN.
     *
     * @param adminId  the unique identifier for the admin
     * @param name     the first name of the admin
     * @param surname  the last name of the admin
     * @param email    the email address of the admin
     * @param password the password of the admin
     */
    public Admin(String adminId, String name, String surname, String email, String password) {
        super(name, surname, email, password, Role.ADMIN);
        this.adminId = adminId;
    }
}
