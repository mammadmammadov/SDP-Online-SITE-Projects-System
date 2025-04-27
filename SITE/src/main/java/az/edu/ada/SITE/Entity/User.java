package az.edu.ada.SITE.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Abstract class representing a user in the system.
 * This class implements the Spring Security UserDetails interface, which allows
 * for user authentication and authorization.
 * There are different types of users (ADMIN, STAFF, STUDENT), each with
 * specific roles and associated welcome pages.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User implements UserDetails {

    /** Unique identifier for the user. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The first name of the user. */
    @Column(nullable = false)
    private String name;

    /** The last name of the user. */
    @Column(nullable = false)
    private String surname;

    /** The email address of the user. It must be unique and in a valid format. */
    @Email(message = "Invalid email format")
    @Column(nullable = false, unique = true)
    private String email;

    /** The password of the user. */
    @Column(nullable = false)
    private String password;

    /** The role of the user (ADMIN, STAFF, STUDENT). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /**
     * Constructor to initialize the user with specific details.
     * 
     * @param name     The first name of the user.
     * @param surname  The last name of the user.
     * @param email    The email address of the user.
     * @param password The password for the user.
     * @param role     The role of the user (ADMIN, STAFF, STUDENT).
     */
    public User(String name, String surname, String email, String password, Role role) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    /**
     * Returns the username of the user (the email in this case).
     * 
     * @return the email address of the user.
     */
    @Override
    public String getUsername() {
        return this.email;
    }

    /**
     * Enum representing the different roles a user can have.
     * Each role is associated with a specific welcome page URL.
     */
    public enum Role {
        ADMIN("/admin/welcome"),
        STAFF("/staff/welcome"),
        STUDENT("/student/welcome");

        private final String welcomePage;

        /**
         * Constructor to initialize the role with its corresponding welcome page URL.
         * 
         * @param welcomePage The welcome page URL associated with the role.
         */
        Role(String welcomePage) {
            this.welcomePage = welcomePage;
        }

        /**
         * Gets the welcome page URL for this role.
         * 
         * @return the welcome page URL.
         */
        public String getWelcomePage() {
            return welcomePage;
        }
    }

    /**
     * Returns the authorities granted to the user, based on their role.
     * 
     * @return a collection of granted authorities for the user.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(() -> "ROLE_" + role.name());
    }

    /**
     * Checks if the account is expired.
     * In this implementation, the account is never considered expired.
     * 
     * @return true, as the account is not expired.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Checks if the account is locked.
     * In this implementation, the account is never considered locked.
     * 
     * @return true, as the account is not locked.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Checks if the credentials are expired.
     * In this implementation, the credentials are never considered expired.
     * 
     * @return true, as the credentials are not expired.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Checks if the user is enabled.
     * In this implementation, the user is always considered enabled.
     * 
     * @return true, as the user is enabled.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
