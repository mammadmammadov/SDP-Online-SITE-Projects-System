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

  public StudentServiceImpl(StudentRepository studentRepository,
      StudentMapper studentMapper,
      ProjectMapper projectMapper) {
    this.studentRepository = studentRepository;
    this.studentMapper = studentMapper;
    this.projectMapper = projectMapper;
  }

  @Override
  public Optional<StudentDTO> getStudentById(Long id) {
    Student student = studentRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFound("Student", "id", id));

    StudentDTO studentDTO = studentMapper.studentToStudentDTO(student);

    return Optional.of(studentDTO);
  }

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

  @Override
  public Optional<StudentDTO> getStudentByEmail(String email) {
    Student student = studentRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFound("Student", "email", email));

    StudentDTO studentDTO = studentMapper.studentToStudentDTO(student);

    return Optional.of(studentDTO);
  }

  @Override
  public void addStudentToProject(StudentDTO studentDTO, ProjectDTO projectDTO) {
    Student student = studentMapper.studentDTOtoStudent(studentDTO);

    Project project = projectMapper.projectDTOtoProject(projectDTO);

    student.getProjects().add(project);

    studentRepository.save(student);
  }

  @Override
  public List<StudentDTO> getAllStudents() {
    List<Student> students = studentRepository.findAll();
    List<StudentDTO> studentDTOs = studentMapper.studentListToStudentDTOList(students);
    return studentDTOs;
  }
}
