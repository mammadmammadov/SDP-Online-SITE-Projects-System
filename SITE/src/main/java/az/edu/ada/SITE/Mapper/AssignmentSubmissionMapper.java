package az.edu.ada.SITE.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import az.edu.ada.SITE.DTO.AssignmentSubmissionDTO;
import az.edu.ada.SITE.Entity.Assignment;
import az.edu.ada.SITE.Entity.AssignmentSubmission;
import az.edu.ada.SITE.Entity.Student;

@Mapper(componentModel = "spring")
public interface AssignmentSubmissionMapper {

  @Mapping(target = "assignment", source = "assignmentId")
  @Mapping(target = "student", source = "studentId")
  AssignmentSubmission assignmentSubmissionDTOtoAssignmentSubmission(AssignmentSubmissionDTO dto);

  @Mapping(target = "assignmentId", source = "assignment.id")
  @Mapping(target = "studentId", source = "student.id")
  AssignmentSubmissionDTO assignmentSubmissionToAssignmentSubmissionDTO(AssignmentSubmission submission);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "assignment", ignore = true)
  @Mapping(target = "student", ignore = true)
  void updateSubmissionFromDTO(AssignmentSubmissionDTO dto, @MappingTarget AssignmentSubmission entity);

  default Assignment mapAssignmentId(Long assignmentId) {
    if (assignmentId == null)
      return null;
    Assignment assignment = new Assignment();
    assignment.setId(assignmentId);
    return assignment;
  }

  default Student mapStudentId(Long studentId) {
    if (studentId == null)
      return null;
    Student student = new Student();
    student.setId(studentId);
    return student;
  }
}
