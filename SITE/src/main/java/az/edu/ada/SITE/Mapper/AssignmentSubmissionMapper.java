package az.edu.ada.SITE.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import az.edu.ada.SITE.DTO.AssignmentSubmissionDTO;
import az.edu.ada.SITE.Entity.Assignment;
import az.edu.ada.SITE.Entity.AssignmentSubmission;
import az.edu.ada.SITE.Entity.Project;
import az.edu.ada.SITE.Entity.Student;

/**
 * Mapper interface for converting between {@link AssignmentSubmissionDTO} and
 * {@link AssignmentSubmission} entities.
 * This interface uses MapStruct for automatic code generation.
 * The component model is set to "spring" for Spring integration.
 */
@Mapper(componentModel = "spring")
public interface AssignmentSubmissionMapper {

  /**
   * Converts an {@link AssignmentSubmission} entity to an
   * {@link AssignmentSubmissionDTO}.
   * 
   * @param submission the {@link AssignmentSubmission} entity to convert
   * @return the corresponding {@link AssignmentSubmissionDTO}
   */
  @Mapping(target = "assignmentId", source = "assignment.id")
  @Mapping(target = "studentId", source = "student.id")
  @Mapping(target = "projectId", source = "project.id")
  @Mapping(target = "studentName", source = "student.name")
  AssignmentSubmissionDTO assignmentSubmissionToAssignmentSubmissionDTO(AssignmentSubmission submission);

  /**
   * Converts an {@link AssignmentSubmissionDTO} to an
   * {@link AssignmentSubmission} entity.
   * 
   * @param dto the {@link AssignmentSubmissionDTO} to convert
   * @return the corresponding {@link AssignmentSubmission} entity
   */
  @Mapping(target = "assignment", source = "assignmentId")
  @Mapping(target = "student", source = "studentId")
  @Mapping(target = "project", source = "projectId")
  AssignmentSubmission assignmentSubmissionDTOtoAssignmentSubmission(AssignmentSubmissionDTO dto);

  /**
   * Updates an existing {@link AssignmentSubmission} entity from the provided
   * {@link AssignmentSubmissionDTO}.
   * The entity will be updated with the data from the DTO, but its ID,
   * assignment, student, and project will be ignored.
   * 
   * @param dto    the {@link AssignmentSubmissionDTO} containing the new data
   * @param entity the existing {@link AssignmentSubmission} entity to be updated
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "assignment", ignore = true)
  @Mapping(target = "student", ignore = true)
  @Mapping(target = "project", ignore = true)
  void updateSubmissionFromDTO(AssignmentSubmissionDTO dto, @MappingTarget AssignmentSubmission entity);

  /**
   * Converts a Long assignment ID to an {@link Assignment} entity.
   * 
   * @param assignmentId the ID of the assignment
   * @return an {@link Assignment} entity with the given ID, or null if the ID is
   *         null
   */
  default Assignment map(Long assignmentId) {
    if (assignmentId == null)
      return null;
    Assignment assignment = new Assignment();
    assignment.setId(assignmentId);
    return assignment;
  }

  /**
   * Converts a Long student ID to a {@link Student} entity.
   * 
   * @param studentId the ID of the student
   * @return a {@link Student} entity with the given ID, or null if the ID is null
   */
  default Student mapStudentId(Long studentId) {
    if (studentId == null)
      return null;
    Student student = new Student();
    student.setId(studentId);
    return student;
  }

  /**
   * Converts a Long project ID to a {@link Project} entity.
   * 
   * @param projectId the ID of the project
   * @return a {@link Project} entity with the given ID, or null if the ID is null
   */
  default Project mapProjectId(Long projectId) {
    if (projectId == null)
      return null;
    Project project = new Project();
    project.setId(projectId);
    return project;
  }
}
