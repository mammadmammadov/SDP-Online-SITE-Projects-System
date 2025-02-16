package az.edu.ada.SITE.Mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import az.edu.ada.SITE.DTO.StudentDTO;
import az.edu.ada.SITE.Entity.Student;

@Mapper(componentModel = "spring")
public interface StudentMapper {
  StudentMapper INSTANCE = Mappers.getMapper(StudentMapper.class);

  @Mapping(source = "id", target = "id")
  StudentDTO studentToStudentDTO(Student student);

  @Mapping(source = "id", target = "id")
  Student studentDTOtoStudent(StudentDTO studentDTO);

  default List<StudentDTO> studentListToStudentDTOList(List<Student> studentList) {
    return studentList.stream()
        .map(this::studentToStudentDTO)
        .collect(Collectors.toList());
  }
}
