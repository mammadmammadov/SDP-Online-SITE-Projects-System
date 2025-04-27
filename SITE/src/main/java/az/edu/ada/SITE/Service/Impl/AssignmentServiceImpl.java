package az.edu.ada.SITE.Service.Impl;

import az.edu.ada.SITE.DTO.AssignmentDTO;
import az.edu.ada.SITE.Entity.Assignment;
import az.edu.ada.SITE.Entity.Project;
import az.edu.ada.SITE.Mapper.AssignmentMapper;
import az.edu.ada.SITE.Repository.AssignmentRepository;
import az.edu.ada.SITE.Repository.ProjectRepository;
import az.edu.ada.SITE.Service.AssignmentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AssignmentServiceImpl implements AssignmentService {

  private final AssignmentRepository assignmentRepository;
  private final AssignmentMapper assignmentMapper;
  private final ProjectRepository projectRepository;

  /**
   * Constructor for the AssignmentServiceImpl class.
   * 
   * @param assignmentRepository The repository for managing assignments.
   * @param assignmentMapper     The mapper for converting between DTOs and
   *                             entities.
   * @param projectRepository    The repository for managing projects.
   */
  public AssignmentServiceImpl(AssignmentRepository assignmentRepository,
      AssignmentMapper assignmentMapper,
      ProjectRepository projectRepository) {
    this.assignmentRepository = assignmentRepository;
    this.assignmentMapper = assignmentMapper;
    this.projectRepository = projectRepository;
  }

  /**
   * Saves a new assignment and associates it with a project.
   * 
   * @param assignmentDTO The DTO containing assignment data to be saved.
   * @return The saved assignment as a DTO.
   * @throws IllegalArgumentException if the project ID is not provided or if the
   *                                  project cannot be found.
   */
  @Override
  public AssignmentDTO saveAssignment(AssignmentDTO assignmentDTO) {
    Assignment assignment = assignmentMapper.assignmentDTOtoAssignment(assignmentDTO);
    if (assignmentDTO.getProjectId() != null) {
      Project project = projectRepository.findById(assignmentDTO.getProjectId())
          .orElseThrow(() -> new IllegalArgumentException("Invalid Project ID"));
      assignment.setProject(project);
    } else {
      throw new IllegalArgumentException("Project ID is required for an assignment");
    }
    assignment = assignmentRepository.save(assignment);
    return assignmentMapper.assignmentToAssignmentDTO(assignment);
  }

  /**
   * Retrieves an assignment by its ID.
   * 
   * @param id The ID of the assignment to retrieve.
   * @return An Optional containing the assignment DTO if found, or an empty
   *         Optional if not found.
   */
  @Override
  public Optional<AssignmentDTO> getAssignmentById(Long id) {
    return assignmentRepository.findById(id)
        .map(assignmentMapper::assignmentToAssignmentDTO);
  }

  /**
   * Deletes an assignment by its ID.
   * 
   * @param id The ID of the assignment to delete.
   * @throws IllegalArgumentException if the assignment cannot be found.
   */
  @Override
  public void deleteAssignment(Long id) {
    Assignment assignment = assignmentRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

    Project project = assignment.getProject();
    if (project != null) {
      project.getAssignments().remove(assignment);
    }

    assignmentRepository.delete(assignment);
  }

  /**
   * Retrieves all assignments associated with a specific project.
   * 
   * @param projectId The ID of the project whose assignments are to be retrieved.
   * @return A list of assignment DTOs associated with the specified project.
   */
  @Override
  public List<AssignmentDTO> getAssignmentsByProjectId(Long projectId) {
    List<Assignment> assignments = assignmentRepository.findByProjectId(projectId);
    return assignments.stream()
        .map(assignmentMapper::assignmentToAssignmentDTO)
        .collect(Collectors.toList());
  }
}
