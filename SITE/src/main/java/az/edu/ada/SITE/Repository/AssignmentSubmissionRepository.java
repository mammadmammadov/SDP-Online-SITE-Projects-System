package az.edu.ada.SITE.Repository;

import az.edu.ada.SITE.Entity.Assignment;
import az.edu.ada.SITE.Entity.AssignmentSubmission;
import az.edu.ada.SITE.Entity.Project;
import az.edu.ada.SITE.Entity.Student;

import org.springframework.data.jpa.repository.JpaRepository;
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
}
