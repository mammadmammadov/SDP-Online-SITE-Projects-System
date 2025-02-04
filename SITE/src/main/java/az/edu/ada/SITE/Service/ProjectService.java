package az.edu.ada.SITE.Service;

import az.edu.ada.SITE.Entity.Project;
import az.edu.ada.SITE.Entity.Student;
import az.edu.ada.SITE.Repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public List<Project> getProjectsByStaffId(Long staffId) {
        return projectRepository.findProjectsByStaffId(staffId);
    }

    public List<Project> getProjectsByFilters(String category, String keywords, String supervisorName,
            String supervisorSurname) {
        return projectRepository.findByFilters(category, keywords, supervisorName, supervisorSurname);
    }

    public void addStudentToProject(Student student, Project project) {
        project.getStudents().add(student);
        saveProject(project);
    }

    public List<Project> getEligibleProjectsForStudent(Student student, String category, String keywords,
            String supervisorName, String supervisorSurname) {
        List<Project> projects = projectRepository.findByFilters(category, keywords, supervisorName, supervisorSurname);

        return projects.stream().filter(project -> {
            boolean eligible = true;
            if (project.getStudyYearRestriction() != null && !project.getStudyYearRestriction().isEmpty()) {
                eligible = eligible && project.getStudyYearRestriction().contains(student.getStudyYear());
            }
            if (project.getDegreeRestriction() != null && !project.getDegreeRestriction().isEmpty()) {
                eligible = eligible && project.getDegreeRestriction().contains(student.getDegree());
            }
            if (project.getMajorRestriction() != null && !project.getMajorRestriction().isEmpty()) {
                eligible = eligible && project.getMajorRestriction().contains(student.getMajor());
            }

            return eligible;
        }).collect(Collectors.toList());
    }

}
