package az.edu.ada.SITE.Service.Impl;

import az.edu.ada.SITE.DTO.AssignmentDTO;
import az.edu.ada.SITE.Entity.Assignment;
import az.edu.ada.SITE.Entity.Project;
import az.edu.ada.SITE.Mapper.AssignmentMapper;
import az.edu.ada.SITE.Repository.AssignmentRepository;
import az.edu.ada.SITE.Repository.ProjectRepository;
import az.edu.ada.SITE.Service.AssignmentService;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AssignmentServiceImpl implements AssignmentService {
  private final AssignmentRepository assignmentRepository;
  private final AssignmentMapper assignmentMapper;
  private final ProjectRepository projectRepository;

  public AssignmentServiceImpl(AssignmentRepository assignmentRepository,
      AssignmentMapper assignmentMapper,
      ProjectRepository projectRepository) {
    this.assignmentRepository = assignmentRepository;
    this.assignmentMapper = assignmentMapper;
    this.projectRepository = projectRepository;
  }

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

  @Override
  public Optional<AssignmentDTO> getAssignmentById(Long id) {
    return assignmentRepository.findById(id)
        .map(assignmentMapper::assignmentToAssignmentDTO);
  }

  @Override
  public void deleteAssignment(Long id) {
    assignmentRepository.deleteById(id);
  }
}
