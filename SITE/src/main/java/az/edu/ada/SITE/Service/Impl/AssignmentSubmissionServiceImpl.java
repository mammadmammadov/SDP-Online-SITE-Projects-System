package az.edu.ada.SITE.Service.Impl;

import az.edu.ada.SITE.DTO.AssignmentSubmissionDTO;
import az.edu.ada.SITE.Entity.AssignmentSubmission;
import az.edu.ada.SITE.Mapper.AssignmentSubmissionMapper;
import az.edu.ada.SITE.Repository.AssignmentSubmissionRepository;
import az.edu.ada.SITE.Service.AssignmentSubmissionService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AssignmentSubmissionServiceImpl implements AssignmentSubmissionService {

  private final AssignmentSubmissionRepository submissionRepository;
  private final AssignmentSubmissionMapper submissionMapper;

  public AssignmentSubmissionServiceImpl(AssignmentSubmissionRepository submissionRepository,
      AssignmentSubmissionMapper submissionMapper) {
    this.submissionRepository = submissionRepository;
    this.submissionMapper = submissionMapper;
  }

  @Override
  public AssignmentSubmissionDTO saveSubmission(AssignmentSubmissionDTO submissionDTO) {
    if (submissionDTO.getId() != null) {
      AssignmentSubmission existing = submissionRepository.findById(submissionDTO.getId())
          .orElseThrow(() -> new IllegalArgumentException("Submission not found"));
      submissionMapper.updateSubmissionFromDTO(submissionDTO, existing);
      existing = submissionRepository.save(existing);
      return submissionMapper.assignmentSubmissionToAssignmentSubmissionDTO(existing);
    } else {
      AssignmentSubmission submission = submissionMapper.assignmentSubmissionDTOtoAssignmentSubmission(submissionDTO);
      submission = submissionRepository.save(submission);
      return submissionMapper.assignmentSubmissionToAssignmentSubmissionDTO(submission);
    }
  }

  @Override
  public Optional<AssignmentSubmissionDTO> getSubmissionById(Long id) {
    return submissionRepository.findById(id)
        .map(submissionMapper::assignmentSubmissionToAssignmentSubmissionDTO);
  }

  @Override
  public List<AssignmentSubmissionDTO> getSubmissionsByAssignmentId(Long assignmentId) {
    List<AssignmentSubmission> submissions = submissionRepository.findByAssignmentId(assignmentId);
    return submissions.stream()
        .map(submissionMapper::assignmentSubmissionToAssignmentSubmissionDTO)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<AssignmentSubmissionDTO> getSubmissionByAssignmentAndStudent(Long assignmentId, Long studentId) {
    return submissionRepository.findByAssignmentIdAndStudentId(assignmentId, studentId)
        .map(submissionMapper::assignmentSubmissionToAssignmentSubmissionDTO);
  }

  @Override
  public void deleteSubmission(Long id) {
    submissionRepository.deleteById(id);
  }
}
