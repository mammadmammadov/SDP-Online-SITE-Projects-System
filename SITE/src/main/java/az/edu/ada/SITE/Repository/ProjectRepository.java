package az.edu.ada.SITE.Repository;

import az.edu.ada.SITE.Entity.Project;
import az.edu.ada.SITE.Entity.Staff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

        @Query("SELECT p FROM Project p WHERE p.supervisor.id = :staffId")
        Page<Project> findProjectsByStaffId(@Param("staffId") Long staffId, Pageable pageable);

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

        @Query("SELECT p FROM Project p WHERE " +
                        "(:category IS NULL OR :category = '' OR :category MEMBER OF p.category) AND " +
                        "(:keywords IS NULL OR LOWER(p.objectives) LIKE LOWER(CONCAT('%', :keywords, '%'))) AND " +
                        "(:supervisorName IS NULL OR LOWER(p.supervisor.name) LIKE LOWER(CONCAT('%', :supervisorName, '%'))) AND "
                        +
                        "(:supervisorSurname IS NULL OR LOWER(p.supervisor.surname) LIKE LOWER(CONCAT('%', :supervisorSurname, '%'))) AND "
                        +
                        "p.status = 'OPEN'")
        Page<Project> findByFilters(
                        @Param("category") String category,
                        @Param("keywords") String keywords,
                        @Param("supervisorName") String supervisorName,
                        @Param("supervisorSurname") String supervisorSurname,
                        Pageable pageable);

        @Query("SELECT p FROM Project p WHERE " +
                        "(:category IS NULL OR :category = '' OR :category MEMBER OF p.category) AND " +
                        "(:keywords IS NULL OR LOWER(p.objectives) LIKE LOWER(CONCAT('%', :keywords, '%'))) AND " +
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
}
