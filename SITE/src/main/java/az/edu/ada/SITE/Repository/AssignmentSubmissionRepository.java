package az.edu.ada.SITE.Repository;

import az.edu.ada.SITE.Entity.Assignment;
import az.edu.ada.SITE.Entity.AssignmentSubmission;
import az.edu.ada.SITE.Entity.Project;
import az.edu.ada.SITE.Entity.Student;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for performing CRUD operations on the
 * {@link AssignmentSubmission} entity.
 * This interface extends {@link JpaRepository} for easy access to database
 * operations related to assignment submissions.
 */
@Repository
public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {

  /**
   * Finds all {@link AssignmentSubmission} entities associated with a specific
   * assignment.
   * 
   * @param assignmentId the ID of the assignment for which submissions are being
   *                     retrieved
   * @return a list of {@link AssignmentSubmission} entities associated with the
   *         specified assignment
   */
  List<AssignmentSubmission> findByAssignmentId(Long assignmentId);

  /**
   * Finds an {@link AssignmentSubmission} by assignment ID and student ID.
   * 
   * @param assignmentId the ID of the assignment
   * @param studentId    the ID of the student
   * @return an Optional containing the {@link AssignmentSubmission} if found, or
   *         an empty Optional if not
   */
  Optional<AssignmentSubmission> findByAssignmentIdAndStudentId(Long assignmentId, Long studentId);

  /**
   * Finds all {@link AssignmentSubmission} entities associated with a specific
   * assignment and project.
   * 
   * @param assignment the {@link Assignment} for which submissions are being
   *                   retrieved
   * @param project    the {@link Project} for which submissions are being
   *                   retrieved
   * @return a list of {@link AssignmentSubmission} entities associated with the
   *         specified assignment and project
   */
  List<AssignmentSubmission> findByAssignmentAndProject(Assignment assignment, Project project);

  /**
   * Finds an {@link AssignmentSubmission} by assignment and student.
   * 
   * @param assignment the {@link Assignment}
   * @param student    the {@link Student}
   * @return an Optional containing the {@link AssignmentSubmission} if found, or
   *         an empty Optional if not
   */
  Optional<AssignmentSubmission> findByAssignmentAndStudent(Assignment assignment, Student student);

  /**
   * Finds all {@link AssignmentSubmission} entities associated with a specific
   * assignment and project ID.
   * 
   * @param assignmentId the ID of the assignment
   * @param projectId    the ID of the project
   * @return a list of {@link AssignmentSubmission} entities associated with the
   *         specified assignment and project ID
   */
  List<AssignmentSubmission> findByAssignmentIdAndProjectId(Long assignmentId, Long projectId);
}
