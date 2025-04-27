package az.edu.ada.SITE.Repository;

import az.edu.ada.SITE.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address.
     * 
     * @param email The email address of the user to search for.
     * @return An Optional containing the user if found, or an empty Optional if not
     *         found.
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a user by their email address and password.
     * 
     * @param email    The email address of the user to search for.
     * @param password The password of the user to match.
     * @return An Optional containing the user if found, or an empty Optional if not
     *         found.
     */
    Optional<User> findByEmailAndPassword(String email, String password);
}
