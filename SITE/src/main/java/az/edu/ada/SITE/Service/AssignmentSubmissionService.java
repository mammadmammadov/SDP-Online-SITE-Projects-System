package az.edu.ada.SITE.Service;

import az.edu.ada.SITE.DTO.AssignmentSubmissionDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing assignment submissions.
 */
public interface AssignmentSubmissionService {

  /**
   * Saves a new or updates an existing assignment submission.
   *
   * @param submissionDTO the assignment submission data transfer object to be
   *                      saved or updated
   * @return the saved or updated assignment submission as a DTO
   */
  AssignmentSubmissionDTO saveSubmission(AssignmentSubmissionDTO submissionDTO);

  /**
   * Retrieves an assignment submission by its ID.
   *
   * @param id the ID of the assignment submission to retrieve
   * @return an Optional containing the assignment submission DTO if found, or an
   *         empty Optional if not found
   */
  Optional<AssignmentSubmissionDTO> getSubmissionById(Long id);

  /**
   * Retrieves all submissions associated with a given assignment ID.
   *
   * @param assignmentId the ID of the assignment for which submissions are to be
   *                     retrieved
   * @return a list of assignment submission DTOs for the specified assignment
   */
  List<AssignmentSubmissionDTO> getSubmissionsByAssignmentId(Long assignmentId);

  /**
   * Deletes an assignment submission by its ID.
   *
   * @param id the ID of the assignment submission to delete
   */
  void deleteSubmission(Long id);

  /**
   * Retrieves an assignment submission by its assignment and student IDs.
   *
   * @param assignmentId the ID of the assignment
   * @param studentId    the ID of the student
   * @return an Optional containing the assignment submission DTO if found, or an
   *         empty Optional if not found
   */
  Optional<AssignmentSubmissionDTO> getSubmissionByAssignmentAndStudent(Long assignmentId, Long studentId);

  /**
   * Retrieves or creates a team assignment submission for a given assignment and
   * project.
   *
   * @param assignmentId the ID of the assignment
   * @param projectId    the ID of the project
   * @return the team assignment submission DTO
   */
  AssignmentSubmissionDTO getOrCreateTeamSubmission(Long assignmentId, Long projectId);

  /**
   * Retrieves or creates an individual assignment submission for a given
   * assignment and student.
   *
   * @param assignmentId the ID of the assignment
   * @param studentId    the ID of the student
   * @return the individual assignment submission DTO
   */
  AssignmentSubmissionDTO getOrCreateIndividualSubmission(Long assignmentId, Long studentId);

  /**
   * Retrieves all assignment submissions by assignment and project IDs.
   *
   * @param assignmentId the ID of the assignment
   * @param projectId    the ID of the project
   * @return a list of assignment submission DTOs for the specified assignment and
   *         project
   */
  List<AssignmentSubmissionDTO> getSubmissionsByAssignmentAndProject(Long assignmentId, Long projectId);
}
