package az.edu.ada.SITE.Service;

import az.edu.ada.SITE.DTO.AssignmentSubmissionDTO;
import java.util.List;
import java.util.Optional;

public interface AssignmentSubmissionService {
  AssignmentSubmissionDTO saveSubmission(AssignmentSubmissionDTO submissionDTO);

  Optional<AssignmentSubmissionDTO> getSubmissionById(Long id);

  List<AssignmentSubmissionDTO> getSubmissionsByAssignmentId(Long assignmentId);

  void deleteSubmission(Long id);

  Optional<AssignmentSubmissionDTO> getSubmissionByAssignmentAndStudent(Long assignmentId, Long studentId);

  AssignmentSubmissionDTO getOrCreateTeamSubmission(Long assignmentId, Long projectId);

  AssignmentSubmissionDTO getOrCreateIndividualSubmission(Long assignmentId, Long studentId);

  List<AssignmentSubmissionDTO> getSubmissionsByAssignmentAndProject(Long assignmentId, Long projectId);
}
