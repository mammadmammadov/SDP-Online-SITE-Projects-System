package az.edu.ada.SITE.Entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Collection;
import java.util.Collections;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public enum Role {
        ADMIN("/admin/welcome"),
        STAFF("/staff/welcome"),
        STUDENT("/student/welcome");

        private final String welcomePage;

        Role(String welcomePage) {
            this.welcomePage = welcomePage;
        }

        public String getWelcomePage() {
            return welcomePage;
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Return a collection of authorities/roles. In your case, you could use role names.
        return Collections.singletonList(() -> "ROLE_" + role.name());
    }
}
