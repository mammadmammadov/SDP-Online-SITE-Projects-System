package az.edu.ada.SITE.Service;

import az.edu.ada.SITE.Entity.Project;
import az.edu.ada.SITE.Repository.ProjectRepository;
import org.springframework.stereotype.Service;

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
}
