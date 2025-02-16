package az.edu.ada.SITE.Service;

import az.edu.ada.SITE.DTO.ProjectDTO;
import az.edu.ada.SITE.DTO.StudentDTO;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public interface StudentService {
  Optional<StudentDTO> getStudentById(Long id);

  StudentDTO saveStudent(StudentDTO studentDTO);

  Optional<StudentDTO> getStudentByEmail(String email);

  void addStudentToProject(StudentDTO studentDTO, ProjectDTO project);

  List<StudentDTO> getAllStudents();
}
