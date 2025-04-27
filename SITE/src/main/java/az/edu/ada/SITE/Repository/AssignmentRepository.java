package az.edu.ada.SITE.Repository;

import az.edu.ada.SITE.Entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository interface for performing CRUD operations on the {@link Assignment}
 * entity.
 * This interface extends {@link JpaRepository} for easy access to database
 * operations.
 */
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

  /**
   * Finds a list of {@link Assignment} entities associated with a specific
   * project.
   * 
   * @param projectId the ID of the project for which assignments are being
   *                  retrieved
   * @return a list of {@link Assignment} entities associated with the specified
   *         project
   */
  List<Assignment> findByProjectId(Long projectId);
}
