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

@Service
public class ProjectServiceImpl implements ProjectService {
  private final ProjectRepository projectRepository;
  private final ProjectMapper projectMapper;
  private final StudentRepository studentRepository;
  private final StudentMapper studentMapper;

  private final UserRepository userRepository;

  public ProjectServiceImpl(ProjectRepository projectRepository, ProjectMapper projectMapper,
      StudentRepository studentRepository, UserRepository userRepository, StudentMapper studentMapper) {
    this.projectRepository = projectRepository;
    this.projectMapper = projectMapper;
    this.studentRepository = studentRepository;
    this.userRepository = userRepository;
    this.studentMapper = studentMapper;
  }

  @Override
  public List<ProjectDTO> getAllProjects() {
    List<Project> projects = projectRepository.findAll();
    List<ProjectDTO> projectDTOs = projectMapper.projectListToProjectDTOList(projects);
    return projectDTOs;
  }

  @Override
  public Optional<ProjectDTO> getProjectById(Long id) {
    Project project = projectRepository.findById(id).orElse(null);
    if (project == null) {
      return Optional.empty();
    }
    ProjectDTO projectDTO = projectMapper.projectToProjectDTO(project);
    return Optional.of(projectDTO);
  }

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

  @Override
  public void deleteProject(Long id) {
    if (projectRepository.existsById(id)) {
      projectRepository.deleteById(id);
    }
  }

  @Override
  public void toggleProjectStatus(ProjectDTO projectDTO) {
    Project project = projectRepository.findById(projectDTO.getId())
        .orElseThrow(() -> new IllegalArgumentException("Project not found"));

    project.toggleStatus();
    projectRepository.save(project);
  }

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

  @Override
  public Page<ProjectDTO> getProjectsExceptStaff(Long staffId, Pageable pageable) {
    Page<Project> projectsPage = projectRepository.findAll(pageable);

    List<ProjectDTO> projectDTOs = projectsPage.getContent().stream()
        .filter(project -> project.getSupervisor() == null || !project.getSupervisor().getId().equals(staffId))
        .map(project -> projectMapper.projectToProjectDTO(project))
        .collect(Collectors.toList());

    return new PageImpl<>(projectDTOs, pageable, projectsPage.getTotalElements());
  }

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
