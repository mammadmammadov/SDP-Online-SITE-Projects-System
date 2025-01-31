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

}
