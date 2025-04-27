package az.edu.ada.SITE.DTO;

import az.edu.ada.SITE.Entity.User.Role;
import lombok.Data;

/**
 * Data Transfer Object (DTO) representing basic user information.
 * This serves as a base class for other user types (e.g., Student, Staff,
 * Admin).
 */
@Data
public class UserDTO {

    /** Unique identifier for the user. */
    private Long id;

    /** First name of the user. */
    private String name;

    /** Last name of the user. */
    private String surname;

    /** Email address of the user. */
    private String email;

    /** Role of the user (e.g., Admin, Staff, Student). */
    private Role role;

    /**
     * Default no-argument constructor.
     * This is required for frameworks like Spring to instantiate the DTO.
     */
    public UserDTO() {
    }

    /**
     * All-arguments constructor for initializing a UserDTO with provided values.
     *
     * @param id      the unique identifier of the user
     * @param name    the first name of the user
     * @param surname the last name of the user
     * @param email   the email address of the user
     * @param role    the role assigned to the user (e.g., Admin, Staff, Student)
     */
    public UserDTO(Long id, String name, String surname, String email, Role role) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.role = role;
    }
}
