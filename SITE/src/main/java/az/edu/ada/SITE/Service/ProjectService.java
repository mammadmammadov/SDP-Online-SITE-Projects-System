package az.edu.ada.SITE.Service;

import az.edu.ada.SITE.Entity.Project;
import az.edu.ada.SITE.Entity.Student;
import az.edu.ada.SITE.Repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }

    public Project saveProject(Project project) {
        return projectRepository.save(project);
    }

    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    public void toggleProjectStatus(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        project.toggleStatus();
        projectRepository.save(project);
    }

    public Page<Project> getProjectsByStaffId(Long staffId, Pageable pageable) {
        return projectRepository.findProjectsByStaffId(staffId, pageable);
    }

    public void addStudentToProject(Student student, Project project) {
        project.getStudents().add(student);
        saveProject(project);
    }

    public Page<Project> getEligibleProjectsForStudent(Student student, String category, String keywords,
            String supervisorName, String supervisorSurname, Pageable pageable) {
        return projectRepository.findEligibleProjectsForStudent(
                category,
                keywords,
                supervisorName,
                supervisorSurname,
                student.getStudyYear(),
                student.getDegree(),
                student.getMajor(),
                pageable);
    }
}
