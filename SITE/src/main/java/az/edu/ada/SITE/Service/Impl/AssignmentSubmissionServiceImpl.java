package az.edu.ada.SITE.Service.Impl;

import az.edu.ada.SITE.DTO.AssignmentSubmissionDTO;
import az.edu.ada.SITE.Entity.Assignment;
import az.edu.ada.SITE.Entity.AssignmentSubmission;
import az.edu.ada.SITE.Entity.Project;
import az.edu.ada.SITE.Entity.Student;
import az.edu.ada.SITE.Mapper.AssignmentSubmissionMapper;
import az.edu.ada.SITE.Repository.AssignmentRepository;
import az.edu.ada.SITE.Repository.AssignmentSubmissionRepository;
import az.edu.ada.SITE.Repository.ProjectRepository;
import az.edu.ada.SITE.Repository.StudentRepository;
import az.edu.ada.SITE.Service.AssignmentSubmissionService;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AssignmentSubmissionServiceImpl implements AssignmentSubmissionService {

  private final AssignmentSubmissionRepository submissionRepository;
  private final AssignmentSubmissionMapper submissionMapper;
  private final AssignmentRepository assignmentRepository;
  private final StudentRepository studentRepository;
  private final ProjectRepository projectRepository;

  /**
   * Constructor for the AssignmentSubmissionServiceImpl class.
   * 
   * @param submissionRepository The repository for managing assignment
   *                             submissions.
   * @param submissionMapper     The mapper for converting between DTOs and
   *                             entities for assignment submissions.
   * @param assignmentRepository The repository for managing assignments.
   * @param studentRepository    The repository for managing students.
   * @param projectRepository    The repository for managing projects.
   */
  public AssignmentSubmissionServiceImpl(AssignmentSubmissionRepository submissionRepository,
      AssignmentSubmissionMapper submissionMapper, AssignmentRepository assignmentRepository,
      StudentRepository studentRepository,
      ProjectRepository projectRepository) {
    this.submissionRepository = submissionRepository;
    this.submissionMapper = submissionMapper;
    this.assignmentRepository = assignmentRepository;
    this.studentRepository = studentRepository;
    this.projectRepository = projectRepository;
  }

  /**
   * Saves or updates an assignment submission.
   * 
   * @param submissionDTO The DTO containing the assignment submission data to be
   *                      saved or updated.
   * @return The saved or updated assignment submission as a DTO.
   * @throws IllegalArgumentException if the feedback exceeds 300 characters.
   */
  @Override
  public AssignmentSubmissionDTO saveSubmission(AssignmentSubmissionDTO submissionDTO) {
    if (submissionDTO.getFileName() == null) {
      submissionDTO.setFileName("");
    }
    if (submissionDTO.getFilePath() == null) {
      submissionDTO.setFilePath("");
    }
    if (submissionDTO.getFeedback() != null && submissionDTO.getFeedback().length() > 300) {
      throw new IllegalArgumentException("Feedback cannot exceed 300 characters");
    }
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

  /**
   * Retrieves an assignment submission by its ID.
   * 
   * @param id The ID of the assignment submission to retrieve.
   * @return An Optional containing the assignment submission DTO if found, or an
   *         empty Optional if not found.
   */
  @Override
  public Optional<AssignmentSubmissionDTO> getSubmissionById(Long id) {
    return submissionRepository.findById(id)
        .map(submissionMapper::assignmentSubmissionToAssignmentSubmissionDTO);
  }

  /**
   * Retrieves all submissions for a specific assignment.
   * 
   * @param assignmentId The ID of the assignment whose submissions are to be
   *                     retrieved.
   * @return A list of assignment submission DTOs associated with the specified
   *         assignment.
   */
  @Override
  public List<AssignmentSubmissionDTO> getSubmissionsByAssignmentId(Long assignmentId) {
    List<AssignmentSubmission> submissions = submissionRepository.findByAssignmentId(assignmentId);
    return submissions.stream()
        .map(submissionMapper::assignmentSubmissionToAssignmentSubmissionDTO)
        .collect(Collectors.toList());
  }

  /**
   * Retrieves a specific assignment submission by its assignment ID and student
   * ID.
   * 
   * @param assignmentId The ID of the assignment.
   * @param studentId    The ID of the student.
   * @return An Optional containing the assignment submission DTO if found, or an
   *         empty Optional if not found.
   */
  @Override
  public Optional<AssignmentSubmissionDTO> getSubmissionByAssignmentAndStudent(Long assignmentId, Long studentId) {
    return submissionRepository.findByAssignmentIdAndStudentId(assignmentId, studentId)
        .map(submissionMapper::assignmentSubmissionToAssignmentSubmissionDTO);
  }

  /**
   * Deletes an assignment submission by its ID.
   * 
   * @param id The ID of the assignment submission to delete.
   */
  @Override
  public void deleteSubmission(Long id) {
    submissionRepository.deleteById(id);
  }

  /**
   * Retrieves or creates a team assignment submission for a specific assignment
   * and project.
   * 
   * @param assignmentId The ID of the assignment.
   * @param projectId    The ID of the project.
   * @return The assignment submission DTO, either retrieved or newly created.
   */
  @Override
  public AssignmentSubmissionDTO getOrCreateTeamSubmission(Long assignmentId, Long projectId) {
    Assignment assignment = assignmentRepository.findById(assignmentId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid Assignment ID"));

    Project project = projectRepository.findById(projectId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid Project ID"));

    return submissionRepository.findByAssignmentAndProject(assignment, project)
        .stream()
        .findFirst()
        .map(submissionMapper::assignmentSubmissionToAssignmentSubmissionDTO)
        .orElseGet(() -> {
          AssignmentSubmission newSubmission = new AssignmentSubmission();
          newSubmission.setAssignment(assignment);
          newSubmission.setProject(project);
          newSubmission.setFileName("team-submission");
          newSubmission.setFilePath("");
          return submissionMapper.assignmentSubmissionToAssignmentSubmissionDTO(
              submissionRepository.save(newSubmission));
        });
  }

  /**
   * Retrieves or creates an individual assignment submission for a specific
   * assignment and student.
   * 
   * @param assignmentId The ID of the assignment.
   * @param studentId    The ID of the student.
   * @return The assignment submission DTO, either retrieved or newly created.
   */
  @Override
  public AssignmentSubmissionDTO getOrCreateIndividualSubmission(Long assignmentId, Long studentId) {
    Assignment assignment = assignmentRepository.findById(assignmentId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid Assignment ID"));

    Student student = studentRepository.findById(studentId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid Student ID"));

    return submissionRepository.findByAssignmentAndStudent(assignment, student)
        .map(submissionMapper::assignmentSubmissionToAssignmentSubmissionDTO)
        .orElseGet(() -> {
          AssignmentSubmission newSubmission = new AssignmentSubmission();
          newSubmission.setAssignment(assignment);
          newSubmission.setStudent(student);
          newSubmission.setFileName("individual-submission");
          newSubmission.setFilePath("");
          return submissionMapper.assignmentSubmissionToAssignmentSubmissionDTO(
              submissionRepository.save(newSubmission));
        });
  }

  /**
   * Retrieves all submissions for a specific assignment and project.
   * 
   * @param assignmentId The ID of the assignment.
   * @param projectId    The ID of the project.
   * @return A list of assignment submission DTOs associated with the specified
   *         assignment and project.
   */
  @Override
  public List<AssignmentSubmissionDTO> getSubmissionsByAssignmentAndProject(Long assignmentId, Long projectId) {
    return submissionRepository.findByAssignmentIdAndProjectId(assignmentId, projectId)
        .stream()
        .map(submissionMapper::assignmentSubmissionToAssignmentSubmissionDTO)
        .collect(Collectors.toList());
  }
}
