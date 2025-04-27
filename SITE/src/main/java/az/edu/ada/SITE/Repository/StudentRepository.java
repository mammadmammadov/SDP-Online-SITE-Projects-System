package az.edu.ada.SITE.Repository;

import az.edu.ada.SITE.Entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

  /**
   * Finds a student by their email address.
   * 
   * @param email The email address of the student to search for.
   * @return An Optional containing the student if found, or an empty Optional if
   *         not found.
   */
  Optional<Student> findByEmail(String email);
}
