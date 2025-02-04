package az.edu.ada.SITE.Repository;

import az.edu.ada.SITE.Entity.Project;
import az.edu.ada.SITE.Entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
        List<Project> findBySupervisor(Staff supervisor);

        @Query("SELECT p FROM Project p WHERE p.supervisor.id = :staffId")
        List<Project> findProjectsByStaffId(@Param("staffId") Long staffId);

        @Query("SELECT p FROM Project p WHERE " +
                        "(:category IS NULL OR :category = '' OR :category MEMBER OF p.category) AND " +
                        "(:keywords IS NULL OR LOWER(p.objectives) LIKE LOWER(CONCAT('%', :keywords, '%'))) AND " +
                        "(:supervisorName IS NULL OR LOWER(p.supervisor.name) LIKE LOWER(CONCAT('%', :supervisorName, '%'))) AND "
                        +
                        "(:supervisorSurname IS NULL OR LOWER(p.supervisor.surname) LIKE LOWER(CONCAT('%', :supervisorSurname, '%'))) AND "
                        +
                        "p.status = 'OPEN'")
        List<Project> findByFilters(
                        @Param("category") String category,
                        @Param("keywords") String keywords,
                        @Param("supervisorName") String supervisorName,
                        @Param("supervisorSurname") String supervisorSurname);
}
