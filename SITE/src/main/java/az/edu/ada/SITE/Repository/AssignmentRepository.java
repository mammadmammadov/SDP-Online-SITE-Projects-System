package az.edu.ada.SITE.Repository;

import az.edu.ada.SITE.Entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
  List<Assignment> findByProjectId(Long projectId);
}
