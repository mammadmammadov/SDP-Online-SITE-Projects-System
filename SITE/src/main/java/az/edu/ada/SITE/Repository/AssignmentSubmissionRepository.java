package az.edu.ada.SITE.Repository;

import az.edu.ada.SITE.Entity.Assignment;
import az.edu.ada.SITE.Entity.AssignmentSubmission;
import az.edu.ada.SITE.Entity.Project;
import az.edu.ada.SITE.Entity.Student;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {
  List<AssignmentSubmission> findByAssignmentId(Long assignmentId);

  Optional<AssignmentSubmission> findByAssignmentIdAndStudentId(Long assignmentId, Long studentId);

  List<AssignmentSubmission> findByAssignmentAndProject(Assignment assignment, Project project);

  Optional<AssignmentSubmission> findByAssignmentAndStudent(Assignment assignment, Student student);

  List<AssignmentSubmission> findByAssignmentIdAndProjectId(Long assignmentId, Long projectId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("UPDATE AssignmentSubmission s SET s.gradeViewed = true WHERE s.student.id = :studentId")
  void markAllAsViewedByStudentId(@Param("studentId") Long studentId);

  @Query("SELECT COUNT(s) FROM AssignmentSubmission s WHERE s.student.id = :studentId AND s.grade IS NOT NULL AND s.gradeViewed = false")
  int countNewGradesForStudent(@Param("studentId") Long studentId);
}
