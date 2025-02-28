package az.edu.ada.SITE.DTO;

import az.edu.ada.SITE.Entity.User.Role;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private Role role;

    public UserDTO() {

    }

    public UserDTO(Long id, String name, String surname, String email, Role role) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.role = role;
    }
}
