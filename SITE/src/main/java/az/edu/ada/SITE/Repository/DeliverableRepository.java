package az.edu.ada.SITE.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import az.edu.ada.SITE.Entity.Deliverable;

/**
 * Repository interface for performing CRUD operations on the
 * {@link Deliverable} entity.
 * This interface extends {@link JpaRepository} to provide standard database
 * operations for the Deliverable entity.
 */
public interface DeliverableRepository extends JpaRepository<Deliverable, Long> {
  // No custom queries are needed, as JpaRepository provides standard CRUD
  // operations
}
