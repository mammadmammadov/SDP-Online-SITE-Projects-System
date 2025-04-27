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

        /**
         * Finds projects by the supervisor's details using a StaffDTO object.
         * 
         * @param supervisor The StaffDTO object containing the supervisor's
         *                   information.
         * @return A list of projects that are supervised by the given supervisor.
         */
        List<Project> findBySupervisor(StaffDTO supervisor);

        /**
         * Finds projects assigned to a specific staff member by their staff ID.
         * 
         * @param staffId  The ID of the staff member.
         * @param pageable The pagination information for the query.
         * @return A page of projects assigned to the specified staff member.
         */
        @Query("SELECT p FROM Project p WHERE p.supervisor.id = :staffId")
        Page<Project> findProjectsByStaffId(@Param("staffId") Long staffId, Pageable pageable);

        /**
         * Retrieves a list of projects along with their deliverables, filtered by
         * various criteria.
         * 
         * @param category          The category of the project (optional).
         * @param keywords          The keywords to search for in the project title,
         *                          description, or objectives (optional).
         * @param supervisorName    The name of the supervisor (optional).
         * @param supervisorSurname The surname of the supervisor (optional).
         * @param studyYear         The study year restriction (optional).
         * @param degree            The degree restriction (optional).
         * @param major             The major restriction (optional).
         * @param pageable          The pagination information for the query.
         * @return A page of projects that match the given filtering criteria.
         */
        @EntityGraph(attributePaths = { "deliverables" })
        @Query("SELECT DISTINCT p FROM Project p LEFT JOIN FETCH p.deliverables WHERE " +
                        "(:category IS NULL OR :category = '' OR :category MEMBER OF p.category) AND " +
                        "(:keywords IS NULL OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keywords, '%')) " +
                        " OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keywords, '%')) " +
                        " OR EXISTS (SELECT o FROM p.objectives o WHERE LOWER(o) LIKE LOWER(CONCAT('%', :keywords, '%')))) AND "
                        +
                        "(:supervisorName IS NULL OR LOWER(p.supervisor.name) LIKE LOWER(CONCAT('%', :supervisorName, '%'))) AND "
                        +
                        "(:supervisorSurname IS NULL OR LOWER(p.supervisor.surname) LIKE LOWER(CONCAT('%', :supervisorSurname, '%'))) AND "
                        +
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

        /**
         * Finds projects assigned to a staff member either as a supervisor or as a
         * co-supervisor.
         * 
         * @param staff    The staff member (supervisor or co-supervisor) to search for.
         * @param pageable The pagination information for the query.
         * @return A page of projects where the specified staff member is either a
         *         supervisor or a co-supervisor.
         */
        @Query("SELECT p FROM Project p LEFT JOIN p.coSupervisors cosup WHERE p.supervisor = :staff OR cosup = :staff")
        Page<Project> findProjectsBySupervisorOrCoSupervisor(@Param("staff") Staff staff, Pageable pageable);
}
