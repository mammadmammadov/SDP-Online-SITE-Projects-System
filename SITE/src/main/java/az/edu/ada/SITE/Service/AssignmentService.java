package az.edu.ada.SITE.Service;

import az.edu.ada.SITE.DTO.AssignmentDTO;
import java.util.Optional;

public interface AssignmentService {
  AssignmentDTO saveAssignment(AssignmentDTO assignmentDTO);

  Optional<AssignmentDTO> getAssignmentById(Long id);

  void deleteAssignment(Long id);
}
