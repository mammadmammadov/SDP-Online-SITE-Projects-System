package az.edu.ada.SITE.Service;

import az.edu.ada.SITE.DTO.AssignmentDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing assignments related to projects.
 */
public interface AssignmentService {

  /**
   * Saves a new or updates an existing assignment.
   *
   * @param assignmentDTO the assignment data transfer object to be saved or
   *                      updated
   * @return the saved or updated assignment as a DTO
   */
  AssignmentDTO saveAssignment(AssignmentDTO assignmentDTO);

  /**
   * Retrieves an assignment by its ID.
   *
   * @param id the ID of the assignment to retrieve
   * @return an Optional containing the assignment DTO if found, or an empty
   *         Optional if not found
   */
  Optional<AssignmentDTO> getAssignmentById(Long id);

  /**
   * Deletes an assignment by its ID.
   *
   * @param id the ID of the assignment to delete
   */
  void deleteAssignment(Long id);

  /**
   * Retrieves all assignments associated with a given project ID.
   *
   * @param projectId the ID of the project for which assignments are to be
   *                  retrieved
   * @return a list of assignment DTOs for the specified project
   */
  List<AssignmentDTO> getAssignmentsByProjectId(Long projectId);
}
