package az.edu.ada.SITE.Service;

import az.edu.ada.SITE.DTO.ProjectDTO;
import az.edu.ada.SITE.DTO.StudentDTO;

import az.edu.ada.SITE.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProjectService {

    List<ProjectDTO> getAllProjects();

    Optional<ProjectDTO> getProjectById(Long id);

    ProjectDTO saveProject(ProjectDTO projectDTO);

    void deleteProject(Long id);

    void toggleProjectStatus(ProjectDTO projectDTO);

    Page<ProjectDTO> getProjectsByStaffId(Long staffId, Pageable pageable);

    void addStudentToProject(StudentDTO student, ProjectDTO project);

    Page<ProjectDTO> getEligibleProjectsForStudent(StudentDTO student, String category, String keywords,
            String supervisorName, String supervisorSurname, Pageable pageable);

    Optional<ProjectDTO> getProjectByIdWithRubrics(Long id);

    public Page<ProjectDTO> getProjectsSupervisedByUser(User user, Pageable pageable);
}
