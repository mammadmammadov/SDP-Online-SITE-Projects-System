package az.edu.ada.SITE.Service;

import az.edu.ada.SITE.DTO.ProjectDTO;
import az.edu.ada.SITE.DTO.StudentDTO;
import az.edu.ada.SITE.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing projects.
 */
public interface ProjectService {

    /**
     * Retrieves all projects.
     *
     * @return a list of project DTOs
     */
    List<ProjectDTO> getAllProjects();

    /**
     * Retrieves a project by its ID.
     *
     * @param id the ID of the project to retrieve
     * @return an Optional containing the project DTO if found, or an empty Optional
     *         if not found
     */
    Optional<ProjectDTO> getProjectById(Long id);

    /**
     * Saves a new or updates an existing project.
     *
     * @param projectDTO the project data transfer object to be saved or updated
     * @return the saved or updated project DTO
     */
    ProjectDTO saveProject(ProjectDTO projectDTO);

    /**
     * Deletes a project by its ID.
     *
     * @param id the ID of the project to delete
     */
    void deleteProject(Long id);

    /**
     * Toggles the status of a project.
     *
     * @param projectDTO the project DTO whose status is to be toggled
     */
    void toggleProjectStatus(ProjectDTO projectDTO);

    /**
     * Retrieves all projects associated with a given staff member's ID.
     *
     * @param staffId  the ID of the staff member
     * @param pageable pagination information
     * @return a page of project DTOs associated with the staff member
     */
    Page<ProjectDTO> getProjectsByStaffId(Long staffId, Pageable pageable);

    /**
     * Retrieves all projects excluding those supervised by a specific staff member.
     *
     * @param staffId  the ID of the staff member to exclude
     * @param pageable pagination information
     * @return a page of project DTOs excluding those supervised by the specified
     *         staff member
     */
    Page<ProjectDTO> getProjectsExceptStaff(Long staffId, Pageable pageable);

    /**
     * Adds a student to a project.
     *
     * @param student the student DTO to be added
     * @param project the project DTO to which the student is to be added
     */
    void addStudentToProject(StudentDTO student, ProjectDTO project);

    /**
     * Retrieves all eligible projects for a student based on various filters.
     *
     * @param student           the student DTO to check eligibility
     * @param category          the category filter
     * @param keywords          the keywords filter
     * @param supervisorName    the supervisor's name filter
     * @param supervisorSurname the supervisor's surname filter
     * @param pageable          pagination information
     * @return a page of eligible project DTOs for the student
     */
    Page<ProjectDTO> getEligibleProjectsForStudent(StudentDTO student, String category, String keywords,
            String supervisorName, String supervisorSurname, Pageable pageable);

    /**
     * Retrieves all projects supervised by a user.
     *
     * @param user     the user whose supervised projects are to be retrieved
     * @param pageable pagination information
     * @return a page of project DTOs supervised by the user
     */
    public Page<ProjectDTO> getProjectsSupervisedByUser(User user, Pageable pageable);

    /**
     * Checks whether a student is eligible for a project based on various criteria.
     *
     * @param student the student DTO
     * @param project the project DTO
     * @return true if the student is eligible for the project, false otherwise
     */
    boolean isStudentEligibleForProject(StudentDTO student, ProjectDTO project);
}
