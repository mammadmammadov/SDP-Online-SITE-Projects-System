package az.edu.ada.SITE.Service.Impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import az.edu.ada.SITE.CustomException.ResourceNotFound;
import az.edu.ada.SITE.DTO.ProjectDTO;
import az.edu.ada.SITE.DTO.StudentDTO;
import az.edu.ada.SITE.Entity.Project;
import az.edu.ada.SITE.Entity.Student;
import az.edu.ada.SITE.Mapper.ProjectMapper;
import az.edu.ada.SITE.Mapper.StudentMapper;
import az.edu.ada.SITE.Repository.StudentRepository;
import az.edu.ada.SITE.Service.StudentService;

@Service
public class StudentServiceImpl implements StudentService {

  private final StudentRepository studentRepository;
  private final StudentMapper studentMapper;
  private final ProjectMapper projectMapper;

  /**
   * Constructor for the StudentServiceImpl class.
   * 
   * @param studentRepository The repository for managing students.
   * @param studentMapper     The mapper for converting between DTOs and entities
   *                          for students.
   * @param projectMapper     The mapper for converting between DTOs and entities
   *                          for projects.
   */
  public StudentServiceImpl(StudentRepository studentRepository,
      StudentMapper studentMapper,
      ProjectMapper projectMapper) {
    this.studentRepository = studentRepository;
    this.studentMapper = studentMapper;
    this.projectMapper = projectMapper;
  }

  /**
   * Retrieves a student by their ID.
   * 
   * @param id The ID of the student to retrieve.
   * @return An Optional containing the student DTO if found, or an empty Optional
   *         if not found.
   * @throws ResourceNotFound If the student with the given ID does not exist.
   */
  @Override
  public Optional<StudentDTO> getStudentById(Long id) {
    Student student = studentRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFound("Student", "id", id));

    StudentDTO studentDTO = studentMapper.studentToStudentDTO(student);

    return Optional.of(studentDTO);
  }

  /**
   * Saves or updates a student's information.
   * 
   * @param studentDTO The student DTO containing the data to be saved or updated.
   * @return The saved or updated student as a DTO.
   * @throws ResourceNotFound If the student with the given ID does not exist.
   */
  @Override
  public StudentDTO saveStudent(StudentDTO studentDTO) {
    Student existingStudent = studentRepository.findById(studentDTO.getId())
        .orElseThrow(() -> new ResourceNotFound("Student", "id", studentDTO.getId()));

    existingStudent.setName(studentDTO.getName());
    existingStudent.setSurname(studentDTO.getSurname());
    existingStudent.setEmail(studentDTO.getEmail());
    existingStudent.setDegree(studentDTO.getDegree());
    existingStudent.setMajor(studentDTO.getMajor());
    existingStudent.setStudyYear(studentDTO.getStudyYear());
    existingStudent.setAccepted(studentDTO.isAccepted());

    Student savedStudent = studentRepository.save(existingStudent);
    return studentMapper.studentToStudentDTO(savedStudent);
  }

  /**
   * Retrieves a student by their email.
   * 
   * @param email The email of the student to retrieve.
   * @return An Optional containing the student DTO if found, or an empty Optional
   *         if not found.
   * @throws ResourceNotFound If the student with the given email does not exist.
   */
  @Override
  public Optional<StudentDTO> getStudentByEmail(String email) {
    Student student = studentRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFound("Student", "email", email));

    StudentDTO studentDTO = studentMapper.studentToStudentDTO(student);

    return Optional.of(studentDTO);
  }

  /**
   * Adds a student to a project.
   * 
   * @param studentDTO The student DTO to be added to the project.
   * @param projectDTO The project DTO to which the student will be added.
   */
  @Override
  public void addStudentToProject(StudentDTO studentDTO, ProjectDTO projectDTO) {
    Student student = studentMapper.studentDTOtoStudent(studentDTO);
    Project project = projectMapper.projectDTOtoProject(projectDTO);

    student.getProjects().add(project);

    studentRepository.save(student);
  }

  /**
   * Retrieves all students.
   * 
   * @return A list of all student DTOs.
   */
  @Override
  public List<StudentDTO> getAllStudents() {
    List<Student> students = studentRepository.findAll();
    List<StudentDTO> studentDTOs = studentMapper.studentListToStudentDTOList(students);
    return studentDTOs;
  }
}
