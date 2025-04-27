package az.edu.ada.SITE.Mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import az.edu.ada.SITE.DTO.StudentDTO;
import az.edu.ada.SITE.Entity.Student;

/**
 * Mapper interface for converting between {@link StudentDTO} and
 * {@link Student} entities.
 * This interface uses MapStruct for automatic code generation and Spring
 * integration.
 */
@Mapper(componentModel = "spring")
public interface StudentMapper {

  /**
   * Converts a {@link Student} entity to a {@link StudentDTO}.
   * 
   * @param student the {@link Student} entity to convert
   * @return the corresponding {@link StudentDTO}
   */
  @Mapping(source = "id", target = "id")
  StudentDTO studentToStudentDTO(Student student);

  /**
   * Converts a {@link StudentDTO} to a {@link Student} entity.
   * 
   * @param studentDTO the {@link StudentDTO} to convert
   * @return the corresponding {@link Student} entity
   */
  @Mapping(source = "id", target = "id")
  Student studentDTOtoStudent(StudentDTO studentDTO);

  /**
   * Converts a list of {@link Student} entities to a list of {@link StudentDTO}
   * objects.
   * 
   * @param studentList the list of {@link Student} entities to convert
   * @return the corresponding list of {@link StudentDTO} objects
   */
  default List<StudentDTO> studentListToStudentDTOList(List<Student> studentList) {
    return studentList.stream()
        .map(this::studentToStudentDTO)
        .collect(Collectors.toList());
  }
}
