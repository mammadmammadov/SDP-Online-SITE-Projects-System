package az.edu.ada.SITE.Repository;

import az.edu.ada.SITE.DTO.StaffDTO;
import az.edu.ada.SITE.Entity.Project;
import az.edu.ada.SITE.Entity.Staff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

        List<Project> findBySupervisor(StaffDTO supervisor);

        @Query("SELECT p FROM Project p WHERE p.supervisor.id = :staffId")
        Page<Project> findProjectsByStaffId(@Param("staffId") Long staffId, Pageable pageable);

        @EntityGraph(attributePaths = { "deliverables" })
        @Query("SELECT DISTINCT p FROM Project p LEFT JOIN FETCH p.deliverables WHERE " +
                "(:category IS NULL OR :category = '' OR :category MEMBER OF p.category) AND " +
                "(:keywords IS NULL OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keywords, '%')) " +
                " OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keywords, '%')) " +
                " OR EXISTS (SELECT o FROM p.objectives o WHERE LOWER(o) LIKE LOWER(CONCAT('%', :keywords, '%')))) AND " +
                "(:supervisorName IS NULL OR LOWER(p.supervisor.name) LIKE LOWER(CONCAT('%', :supervisorName, '%'))) AND " +
                "(:supervisorSurname IS NULL OR LOWER(p.supervisor.surname) LIKE LOWER(CONCAT('%', :supervisorSurname, '%'))) AND " +
                "p.status = 'OPEN' AND " +
                "(p.studyYearRestriction IS EMPTY OR :studyYear MEMBER OF p.studyYearRestriction) AND " +
                "(p.degreeRestriction IS EMPTY OR :degree MEMBER OF p.degreeRestriction) AND " +
                "(p.majorRestriction IS EMPTY OR :major MEMBER OF p.majorRestriction)")
        Page<Project> findEligibleProjectsForStudent(
                        @Param("category") String category,
                        @Param("keywords") String keywords,
                        @Param("supervisorName") String supervisorName,
                        @Param("supervisorSurname") String supervisorSurname,
                        @Param("studyYear") String studyYear,
                        @Param("degree") String degree,
                        @Param("major") String major,
                        Pageable pageable);

        @Query("SELECT p FROM Project p LEFT JOIN p.coSupervisors cosup WHERE p.supervisor = :staff OR cosup = :staff")
        Page<Project> findProjectsBySupervisorOrCoSupervisor(@Param("staff") Staff staff, Pageable pageable);

}
