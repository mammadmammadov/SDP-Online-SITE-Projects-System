package az.edu.ada.SITE.Service;

import az.edu.ada.SITE.DTO.ProjectDTO;
import az.edu.ada.SITE.DTO.StudentDTO;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

/**
 * Service interface for managing students.
 */
@Service
public interface StudentService {

  /**
   * Retrieves a student by its ID.
   *
   * @param id the ID of the student to retrieve
   * @return an Optional containing the student DTO if found, or an empty Optional
   *         if not found
   */
  Optional<StudentDTO> getStudentById(Long id);

  /**
   * Saves a new or updates an existing student.
   *
   * @param studentDTO the student data transfer object to be saved or updated
   * @return the saved or updated student DTO
   */
  StudentDTO saveStudent(StudentDTO studentDTO);

  /**
   * Retrieves a student by their email address.
   *
   * @param email the email address of the student to retrieve
   * @return an Optional containing the student DTO if found, or an empty Optional
   *         if not found
   */
  Optional<StudentDTO> getStudentByEmail(String email);

  /**
   * Adds a student to a project.
   *
   * @param studentDTO the student DTO to be added
   * @param project    the project DTO to which the student is to be added
   */
  void addStudentToProject(StudentDTO studentDTO, ProjectDTO project);

  /**
   * Retrieves all students.
   *
   * @return a list of all student DTOs
   */
  List<StudentDTO> getAllStudents();
}
