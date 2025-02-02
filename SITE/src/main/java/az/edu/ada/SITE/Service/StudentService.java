package az.edu.ada.SITE.Service;

import az.edu.ada.SITE.Entity.Project;
import az.edu.ada.SITE.Entity.Student;
import az.edu.ada.SITE.Repository.StudentRepository;

import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class StudentService {
  private final StudentRepository studentRepository;
  private final ProjectService projectService;

  public StudentService(StudentRepository studentRepository, ProjectService projectService) {
    this.studentRepository = studentRepository;
    this.projectService = projectService;
  }

  public Optional<Student> getStudentById(Long id) {
    return studentRepository.findById(id);
  }

  public Student saveStudent(Student student) {
    return studentRepository.save(student);
  }

  public Optional<Student> getStudentByEmail(String email) {
    return studentRepository.findByEmail(email);
  }

  public void addStudentToProject(Student student, Project project) {
    project.addStudent(student);
    projectService.saveProject(project);
  }
}
