package az.edu.ada.SITE.Repository;

import az.edu.ada.SITE.Entity.Rubric;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RubricRepository extends JpaRepository<Rubric, Long> {
    List<Rubric> findByProjectId(Long projectId);
}