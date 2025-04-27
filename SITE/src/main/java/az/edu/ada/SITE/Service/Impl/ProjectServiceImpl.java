package az.edu.ada.SITE.Service.Impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import az.edu.ada.SITE.Entity.Staff;
import az.edu.ada.SITE.Entity.User;
import az.edu.ada.SITE.Repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import az.edu.ada.SITE.DTO.ProjectDTO;
import az.edu.ada.SITE.DTO.StudentDTO;
import az.edu.ada.SITE.Entity.Project;
import az.edu.ada.SITE.Entity.Student;
import az.edu.ada.SITE.Mapper.ProjectMapper;
import az.edu.ada.SITE.Mapper.StudentMapper;
import az.edu.ada.SITE.Repository.ProjectRepository;
import az.edu.ada.SITE.Repository.StudentRepository;
import az.edu.ada.SITE.Service.ProjectService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the {@link ProjectService} interface.
 * Handles operations related to projects such as saving, retrieving, deleting,
 * and assigning students to projects.
 */
@Service
public class ProjectServiceImpl implements ProjectService {

  private final ProjectRepository projectRepository;
  private final ProjectMapper projectMapper;
  private final StudentRepository studentRepository;
  private final StudentMapper studentMapper;

  private final UserRepository userRepository;

  /**
   * Constructor for ProjectServiceImpl.
   * 
   * @param projectRepository Repository for project data.
   * @param projectMapper     Mapper for converting between Project and
   *                          ProjectDTO.
   * @param studentRepository Repository for student data.
   * @param userRepository    Repository for user data.
   * @param studentMapper     Mapper for converting between Student and
   *                          StudentDTO.
   */
  public ProjectServiceImpl(ProjectRepository projectRepository, ProjectMapper projectMapper,
      StudentRepository studentRepository, UserRepository userRepository, StudentMapper studentMapper) {
    this.projectRepository = projectRepository;
    this.projectMapper = projectMapper;
    this.studentRepository = studentRepository;
    this.userRepository = userRepository;
    this.studentMapper = studentMapper;
  }

  /**
   * Retrieves all projects.
   * 
   * @return List of ProjectDTO objects representing all projects.
   */
  @Override
  public List<ProjectDTO> getAllProjects() {
    List<Project> projects = projectRepository.findAll();
    List<ProjectDTO> projectDTOs = projectMapper.projectListToProjectDTOList(projects);
    return projectDTOs;
  }

  /**
   * Retrieves a project by its ID.
   * 
   * @param id ID of the project.
   * @return Optional containing ProjectDTO if found, or empty if not found.
   */
  @Override
  public Optional<ProjectDTO> getProjectById(Long id) {
    Project project = projectRepository.findById(id).orElse(null);
    if (project == null) {
      return Optional.empty();
    }
    ProjectDTO projectDTO = projectMapper.projectToProjectDTO(project);
    return Optional.of(projectDTO);
  }

  /**
   * Saves a project. If the project already exists, it updates it.
   * 
   * @param projectDTO Project data to be saved.
   * @return The saved ProjectDTO.
   */
  @Override
  public ProjectDTO saveProject(ProjectDTO projectDTO) {
    Project project;
    if (projectDTO.getId() != null) {
      project = projectRepository.findById(projectDTO.getId())
          .orElseThrow(() -> new IllegalArgumentException("Project not found"));

      project.setTitle(projectDTO.getTitle());
      project.setDescription(projectDTO.getDescription());
      project.setObjectives(projectDTO.getObjectives());
      project.setType(projectDTO.getType());
      project.setKeywords(projectDTO.getKeywords());
      project.setResearchFocus(projectDTO.getResearchFocus());
      project.setCategory(projectDTO.getCategory());
      project.setStudyYearRestriction(projectDTO.getStudyYearRestriction());
      project.setDegreeRestriction(projectDTO.getDegreeRestriction());
      project.setMajorRestriction(projectDTO.getMajorRestriction());
      project.setStatus(projectDTO.getStatus());
      project.setMaxStudents(projectDTO.getMaxStudents());
      project.setSupervisor(projectDTO.getSupervisor());
      project.setCoSupervisors(projectDTO.getCoSupervisors());
      project.setSubcategories(projectDTO.getSubcategories());
      project.setAppStatus(projectDTO.getAppStatus());
      project.setDeliverables(project.getDeliverables());

      project.getRequestedStudents().clear();
      if (projectDTO.getRequestedStudents() != null) {
        List<Student> pendingStudents = projectDTO.getRequestedStudents().stream()
            .map(studentMapper::studentDTOtoStudent)
            .collect(Collectors.toList());
        project.getRequestedStudents().addAll(pendingStudents);
      }

      project.getStudents().clear();
      if (projectDTO.getStudents() != null) {
        List<Student> acceptedStudents = projectDTO.getStudents().stream()
            .map(studentMapper::studentDTOtoStudent)
            .collect(Collectors.toList());
        project.getStudents().addAll(acceptedStudents);
      }
    } else {
      project = projectMapper.projectDTOtoProject(projectDTO);
    }

    project = projectRepository.save(project);
    return projectMapper.projectToProjectDTO(project);
  }

  /**
   * Deletes a project by its ID.
   * 
   * @param id ID of the project to be deleted.
   */
  @Override
  public void deleteProject(Long id) {
    if (projectRepository.existsById(id)) {
      projectRepository.deleteById(id);
    }
  }

  /**
   * Toggles the status of a project between OPEN and CLOSED.
   * 
   * @param projectDTO Project data to be updated.
   */
  @Override
  public void toggleProjectStatus(ProjectDTO projectDTO) {
    Project project = projectRepository.findById(projectDTO.getId())
        .orElseThrow(() -> new IllegalArgumentException("Project not found"));

    project.toggleStatus();
    projectRepository.save(project);
  }

  /**
   * Retrieves projects by staff ID.
   * 
   * @param userId   ID of the staff member.
   * @param pageable Pageable object for pagination.
   * @return Page of ProjectDTO objects.
   */
  @Override
  public Page<ProjectDTO> getProjectsByStaffId(Long userId, Pageable pageable) {

    Optional<User> userOptional = userRepository.findById(userId);
    if (!userOptional.isPresent() || !(userOptional.get() instanceof Staff)) {
      return Page.empty();
    }
    Staff staff = (Staff) userOptional.get();

    Page<Project> projectsPage = projectRepository.findProjectsBySupervisorOrCoSupervisor(staff, pageable);

    List<ProjectDTO> projectDTOs = projectMapper.projectListToProjectDTOList(projectsPage.getContent());

    return new PageImpl<>(projectDTOs, pageable, projectsPage.getTotalElements());
  }

  /**
   * Retrieves projects excluding those assigned to a specific staff member.
   * 
   * @param staffId  ID of the staff member to exclude.
   * @param pageable Pageable object for pagination.
   * @return Page of ProjectDTO objects.
   */
  @Override
  public Page<ProjectDTO> getProjectsExceptStaff(Long staffId, Pageable pageable) {
    Page<Project> projectsPage = projectRepository.findAll(pageable);

    List<ProjectDTO> projectDTOs = projectsPage.getContent().stream()
        .filter(project -> project.getSupervisor() == null || !project.getSupervisor().getId().equals(staffId))
        .map(project -> projectMapper.projectToProjectDTO(project))
        .collect(Collectors.toList());

    return new PageImpl<>(projectDTOs, pageable, projectsPage.getTotalElements());
  }

  /**
   * Adds a student to a project.
   * 
   * @param studentDTO Student data to be added.
   * @param projectDTO Project data to which the student will be added.
   */
  @Override
  public void addStudentToProject(StudentDTO studentDTO, ProjectDTO projectDTO) {
    Project projectEntity = projectRepository.findById(projectDTO.getId())
        .orElseThrow(() -> new RuntimeException("Project not found"));

    Student studentEntity = studentMapper.studentDTOtoStudent(studentDTO);

    studentEntity.setAccepted(true);
    studentRepository.save(studentEntity);

    projectEntity.addAcceptedStudent(studentEntity);
    projectRepository.save(projectEntity);
  }

  /**
   * Retrieves projects eligible for a student based on various filters.
   * 
   * @param student           The student data.
   * @param category          Category filter.
   * @param keywords          Keywords filter.
   * @param supervisorName    Supervisor's first name.
   * @param supervisorSurname Supervisor's last name.
   * @param pageable          Pageable object for pagination.
   * @return Page of ProjectDTO objects.
   */
  @Override
  public Page<ProjectDTO> getEligibleProjectsForStudent(StudentDTO student, String category, String keywords,
      String supervisorName, String supervisorSurname, Pageable pageable) {

    Page<Project> projectsPage = projectRepository.findEligibleProjectsForStudent(
        category, keywords, supervisorName, supervisorSurname,
        student.getStudyYear(), student.getDegree(), student.getMajor(), pageable);

    projectsPage.getContent().forEach(project -> {
      project.getDeliverables().size();
    });

    List<ProjectDTO> projectDTOs = projectMapper.projectListToProjectDTOList(projectsPage.getContent());

    return new PageImpl<>(projectDTOs, pageable, projectsPage.getTotalElements());
  }

  /**
   * Retrieves projects supervised by a specific user.
   * 
   * @param user     The user to be checked.
   * @param pageable Pageable object for pagination.
   * @return Page of ProjectDTO objects.
   */
  @Override
  @Transactional(readOnly = true)
  public Page<ProjectDTO> getProjectsSupervisedByUser(User user, Pageable pageable) {
    if (!(user instanceof Staff)) {
      return Page.empty();
    }
    Staff staff = (Staff) user;
    Page<Project> projectsPage = projectRepository.findProjectsBySupervisorOrCoSupervisor(staff, pageable);

    projectsPage.getContent().forEach(project -> {
      if (project.getSupervisor() != null) {
        project.getSupervisor().getName();
      }
      if (project.getCoSupervisors() != null) {
        project.getCoSupervisors().size();
      }
    });

    List<ProjectDTO> projectDTOs = projectMapper.projectListToProjectDTOList(projectsPage.getContent());
    return new PageImpl<>(projectDTOs, pageable, projectsPage.getTotalElements());
  }

  /**
   * Checks if a student is eligible for a project based on the student's
   * restrictions.
   * 
   * @param student The student data to check eligibility.
   * @param project The project data to check against.
   * @return True if the student is eligible, otherwise false.
   */
  @Override
  public boolean isStudentEligibleForProject(StudentDTO student, ProjectDTO project) {
    if (!project.getStudyYearRestriction().isEmpty()
        && !project.getStudyYearRestriction().contains(student.getStudyYear())) {
      return false;
    }

    if (!project.getDegreeRestriction().isEmpty()
        && !project.getDegreeRestriction().contains(student.getDegree())) {
      return false;
    }

    if (!project.getMajorRestriction().isEmpty()
        && !project.getMajorRestriction().contains(student.getMajor())) {
      return false;
    }
    return true;
  }
}
