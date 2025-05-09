package az.edu.ada.SITE.Controller;

import az.edu.ada.SITE.DTO.DeliverableDTO;
import az.edu.ada.SITE.DTO.ProjectDTO;
import az.edu.ada.SITE.DTO.StudentDTO;
import az.edu.ada.SITE.Entity.Deliverable;
import az.edu.ada.SITE.Entity.Project;
import az.edu.ada.SITE.Entity.Staff;
import az.edu.ada.SITE.Entity.Student;
import az.edu.ada.SITE.Entity.User;
import az.edu.ada.SITE.Mapper.DeliverableMapper;
import az.edu.ada.SITE.Mapper.ProjectMapper;
import az.edu.ada.SITE.Mapper.StudentMapper;
import az.edu.ada.SITE.Repository.ProjectRepository;
import az.edu.ada.SITE.Repository.StudentRepository;
import az.edu.ada.SITE.Repository.UserRepository;
import az.edu.ada.SITE.Repository.DeliverableRepository;
import az.edu.ada.SITE.Service.ProjectService;
import az.edu.ada.SITE.Service.StudentService;

import org.hibernate.Hibernate;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller class that manages the project-related operations 
 * for administrators, staff, and students.
 */
@Controller
@RequestMapping
public class ProjectController {
    private final ProjectService projectService;
    private final UserRepository userRepository;
    private final StudentService studentService;
    private final ProjectMapper projectMapper;
    private final ProjectRepository projectRepository;
    private final DeliverableRepository deliverableRepository;
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    @Autowired
    private DeliverableMapper deliverableMapper;

    /**
     * Constructs a ProjectController with required services, repositories, and mappers.
     *
     * @param projectService Service for managing projects
     * @param userRepository Repository for managing users
     * @param studentService Service for managing student-related operations
     * @param projectMapper Mapper for converting between Project and ProjectDTO
     * @param projectRepository Repository for managing project entities
     * @param studentRepository Repository for managing student entities
     * @param deliverableRepository Repository for managing deliverable entities
     * @param studentMapper Mapper for converting between Student and StudentDTO
     */
    public ProjectController(ProjectService projectService, UserRepository userRepository,
            StudentService studentService, ProjectMapper projectMapper,
            ProjectRepository projectRepository, StudentRepository studentRepository,
            DeliverableRepository deliverableRepository, StudentMapper studentMapper) {
        this.projectService = projectService;
        this.userRepository = userRepository;
        this.studentService = studentService;
        this.projectMapper = projectMapper;
        this.projectRepository = projectRepository;
        this.studentRepository = studentRepository;
        this.deliverableRepository = deliverableRepository;
        this.studentMapper = studentMapper;
    }

    /**
     * Displays a list of all students to the administrator.
     *
     * @param model Spring model to pass data to the view
     * @return Name of the view template
     */
    @GetMapping("/admin/students")
    public String showStudents(Model model) {
        List<Student> students = userRepository.findAll().stream()
                .filter(user -> user instanceof Student)
                .map(user -> (Student) user)
                .collect(Collectors.toList());

        model.addAttribute("students", students);
        return "admin_students";
    }

    /**
     * Displays a list of all staff members to the administrator.
     *
     * @param model Spring model to pass data to the view
     * @return Name of the view template
     */
    @GetMapping("/admin/staff")
    public String showStaff(Model model) {
        List<Staff> staff_members = userRepository.findAll().stream()
                .filter(user -> user instanceof Staff)
                .map(user -> (Staff) user)
                .collect(Collectors.toList());

        model.addAttribute("staff", staff_members);
        return "admin_staff";
    }

    /**
     * Displays a list of all projects to the administrator.
     *
     * @param model Spring model to pass data to the view
     * @return Name of the view template
     */
    @GetMapping("/admin/projects")
    public String showProjects(Model model) {
        List<ProjectDTO> projects = projectService.getAllProjects();
        model.addAttribute("projects", projects);
        return "admin_projects";
    }

    /**
     * Deletes a project with the specified ID.
     *
     * @param id ID of the project to be deleted
     * @return Redirect URL after deletion
     */
    @GetMapping("/admin/projects/delete/{id}")
    public String deleteProjectAdmin(@PathVariable Long id) {
        projectService.deleteProject(id);
        return "redirect:/admin/projects";
    }

    /**
     * Displays the project edit form for administrators.
     *
     * @param id ID of the project to edit
     * @param model Spring model to pass data to the view
     * @param principal Principal object to access authenticated user information
     * @return Name of the view template
     */
    @GetMapping("/admin/projects/edit/{id}")
    public String editProjectFormAdmin(@PathVariable Long id, Model model, Principal principal) {

        ProjectDTO projectDTO = projectService.getProjectById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Project ID: " + id));

        model.addAttribute("category", List.of("Artificial Intelligence", "Software Engineering", "Cybersecurity",
                "Data Science", "Networks", "Web Development", "Software Development"));
        model.addAttribute("studyYears", List.of("Freshman", "Sophomore", "Junior", "Senior"));
        model.addAttribute("degrees", List.of("Undergraduate", "Graduate"));
        model.addAttribute("majors", List.of("Information Technologies", "Computer Science", "Computer Engineering",
                "Electrical Engineering"));

        List<Staff> staff_members = userRepository.findAll().stream()
                .filter(user -> user instanceof Staff)
                .map(user -> (Staff) user)
                .collect(Collectors.toList());

        Staff supervisor = projectDTO.getSupervisor();
        if (supervisor != null) {
            staff_members.remove(supervisor);
        }

        model.addAttribute("staffUsers", staff_members);

        model.addAttribute("currentCoSupervisors", projectDTO.getCoSupervisors());

        model.addAttribute("project", projectDTO);

        return "edit_project_admin";
    }

    /**
     * Updates a project's information based on the administrator's input.
     *
     * @param id ID of the project to update
     * @param projectDTO Updated project details
     * @param coSupervisorIds IDs of selected co-supervisors
     * @param redirectAttributes Redirect attributes for post-update messaging
     * @return Redirect URL after update
     */
    @PostMapping("/admin/projects/update/{id}")
    public String updateProjectAdmin(@PathVariable Long id, @ModelAttribute ProjectDTO projectDTO,
            @RequestParam(value = "coSupervisorIds", required = false) List<Long> coSupervisorIds,
            RedirectAttributes redirectAttributes) {
        ProjectDTO existingProject = projectService.getProjectById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Project ID"));

        existingProject.setTitle(projectDTO.getTitle());
        existingProject.setDescription(projectDTO.getDescription());
        existingProject.setObjectives(projectDTO.getObjectives());
        existingProject.setType(projectDTO.getType());
        existingProject.setStatus(projectDTO.getStatus());
        if (projectDTO.getType() == Project.ProjectType.INDIVIDUAL) {
            existingProject.setMaxStudents(1);
        } else {
            existingProject.setMaxStudents(projectDTO.getMaxStudents());
        }
        existingProject.setCategory(projectDTO.getCategory());
        existingProject.setStudyYearRestriction(projectDTO.getStudyYearRestriction());
        existingProject.setDegreeRestriction(projectDTO.getDegreeRestriction());
        existingProject.setMajorRestriction(projectDTO.getMajorRestriction());
        existingProject.setResearchFocus(projectDTO.getResearchFocus());
        existingProject.setSubcategories(projectDTO.getSubcategories());

        List<Staff> updatedCoSupervisors = new ArrayList<>();

        if (coSupervisorIds != null && !coSupervisorIds.isEmpty()) {
            List<Staff> selectedCoSupervisors = userRepository.findAllById(coSupervisorIds).stream()
                    .filter(user -> user instanceof Staff)
                    .map(user -> (Staff) user)
                    .collect(Collectors.toList());
            updatedCoSupervisors.addAll(selectedCoSupervisors);
        }
        existingProject.setCoSupervisors(updatedCoSupervisors);

        projectService.saveProject(existingProject);
        redirectAttributes.addFlashAttribute("success", "Project updated successfully!");

        return "redirect:/admin/projects";
    }

    /**
     * Displays available projects to the student with optional filtering and pagination.
     *
     * @param category Project category filter
     * @param keywords Keywords for search
     * @param supervisorName Supervisor's first name filter
     * @param supervisorSurname Supervisor's surname filter
     * @param page Page number for pagination
     * @param model Spring model to pass data to the view
     * @param principal Principal object to access authenticated student
     * @return Name of the view template
     */
    @GetMapping("/student/projects")
    public String viewProjects(@RequestParam(required = false) String category,
            @RequestParam(required = false) String keywords,
            @RequestParam(required = false) String supervisorName,
            @RequestParam(required = false) String supervisorSurname,
            @RequestParam(defaultValue = "0") int page,
            Model model, Principal principal) {

        String email = principal.getName();
        StudentDTO studentDTO = studentService.getStudentByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Student not found"));

        if (page < 0)
            page = 0;
        var pageable = org.springframework.data.domain.PageRequest.of(page, 6);
        var projectPage = projectService.getEligibleProjectsForStudent(studentDTO, category, keywords, supervisorName,
                supervisorSurname, pageable);

        projectPage.getContent().forEach(project -> {
            Hibernate.initialize(project.getDeliverables());
        });

        List<String> categories = List.of("Artificial Intelligence", "Software Engineering", "Cybersecurity",
                "Data Science", "Networks", "Web Development", "Software Development");

        model.addAttribute("student", studentDTO);
        model.addAttribute("studentName", studentDTO.getName() + " " + studentDTO.getSurname());
        model.addAttribute("projects", projectPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", projectPage.getTotalPages());
        model.addAttribute("category", categories);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("keywords", keywords);
        model.addAttribute("supervisorName", supervisorName);
        model.addAttribute("supervisorSurname", supervisorSurname);

        return "student_projects";
    }

    /**
     * Displays projects relevant to the logged-in staff member.
     *
     * @param model Spring model to pass data to the view
     * @param principal Principal object to access authenticated staff
     * @param page Page number for pagination
     * @return Name of the view template
     */
    @GetMapping("/staff/projects")
    public String viewProjects(Model model, Principal principal,
            @RequestParam(defaultValue = "0") int page) {
        try {
            String email = principal.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            String fullName = user.getName() + " " + user.getSurname();
            model.addAttribute("staffName", fullName);

            Long staffId = null;
            if (user instanceof Staff) {
                staffId = ((Staff) user).getId();
            }

            if (page < 0)
                page = 0;
            int pageSize = 6;

            List<ProjectDTO> staffProjects = staffId != null
                    ? projectService.getProjectsByStaffId(staffId, Pageable.unpaged()).getContent()
                    : new ArrayList<>();
            List<ProjectDTO> otherProjects = projectService.getProjectsExceptStaff(staffId, Pageable.unpaged())
                    .getContent();

            List<ProjectDTO> mergedProjects = new ArrayList<>();
            Set<Long> projectIds = new HashSet<>();

            for (ProjectDTO p : staffProjects) {
                if (projectIds.add(p.getId())) {
                    mergedProjects.add(p);
                }
            }
            for (ProjectDTO p : otherProjects) {
                if (projectIds.add(p.getId())) {
                    mergedProjects.add(p);
                }
            }

            int totalProjects = mergedProjects.size();
            int fromIndex = page * pageSize;
            int toIndex = Math.min(fromIndex + pageSize, totalProjects);

            List<ProjectDTO> paginatedProjects = mergedProjects.subList(fromIndex, toIndex);

            model.addAttribute("projects", paginatedProjects);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", (int) Math.ceil((double) totalProjects / pageSize));
            model.addAttribute("staffId", staffId);

            return "staff_projects";
        } catch (UsernameNotFoundException e) {
            return "redirect:/auth/login?error=user_not_found";
        }
    }

    /**
     * Displays the list of applicants and eligible students for a specific project.
     *
     * @param projectId ID of the project
     * @param searchEmail Optional search query for student's email
     * @param model Spring model to pass data to the view
     * @param principal Principal object to access authenticated staff
     * @return Name of the view template
     */
    @GetMapping("/staff/projects/applicants/{projectId}")
    public String viewApplicants(@PathVariable Long projectId, @RequestParam(required = false) String searchEmail,
            Model model, Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!(user instanceof Staff)) {
            return "redirect:/staff/projects?error=You are not authorized to view applicants";
        }
        Staff staff = (Staff) user;

        ProjectDTO projectDTO = projectService.getProjectById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Project ID"));

        if (!projectDTO.getSupervisor().equals(staff)) {
            return "redirect:/staff/projects?error=You are not authorized to view applicants for this project";
        }

        List<Student> pendingApplicants = projectDTO.getRequestedStudents().stream()
                .filter(studentDTO -> !studentDTO.isAccepted())
                .map(studentMapper::studentDTOtoStudent)
                .collect(Collectors.toList());

        List<StudentDTO> eligibleStudents = studentService.getAllStudents().stream()
                .filter(studentDTO -> !studentDTO.isAccepted())
                .filter(studentDTO -> projectDTO.getStudyYearRestriction().contains(studentDTO.getStudyYear()))
                .filter(studentDTO -> projectDTO.getDegreeRestriction().contains(studentDTO.getDegree()))
                .filter(studentDTO -> projectDTO.getMajorRestriction().contains(studentDTO.getMajor()))
                .collect(Collectors.toList());

        if (searchEmail != null && !searchEmail.isBlank()) {
            String searchLower = searchEmail.toLowerCase();
            eligibleStudents = eligibleStudents.stream()
                    .filter(studentDTO -> studentDTO.getEmail().toLowerCase().contains(searchLower))
                    .collect(Collectors.toList());
        }

        model.addAttribute("project", projectDTO);
        model.addAttribute("eligibleStudents", eligibleStudents);
        model.addAttribute("project", projectDTO);
        model.addAttribute("applicants", pendingApplicants);
        return "applicants";
    }

    /**
     * Accepts a student's application for a specific project.
     *
     * @param studentId ID of the student to accept
     * @param projectId ID of the project
     * @param redirectAttributes Redirect attributes for messaging
     * @return Redirect URL after accepting the student
     */
    @GetMapping("/staff/projects/applicant/accept/{studentId}/{projectId}")
    public String acceptApplicant(@PathVariable Long studentId,
            @PathVariable Long projectId,
            RedirectAttributes redirectAttributes) {
        Project projectEntity = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Project ID"));
        Student studentEntity = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Student ID"));

        projectEntity.getRequestedStudents().remove(studentEntity);

        if (projectEntity.getType() == Project.ProjectType.INDIVIDUAL && !projectEntity.getStudents().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Individual project already has an accepted student.");
            return "redirect:/staff/projects/applicants/" + projectId;
        }
        if (projectEntity.getType() == Project.ProjectType.GROUP &&
                projectEntity.getStudents().size() >= projectEntity.getMaxStudents()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Group project has reached its maximum number of accepted students.");
            return "redirect:/staff/projects/applicants/" + projectId;
        }

        if (!projectEntity.getStudents().contains(studentEntity)) {
            projectEntity.getStudents().add(studentEntity);
            studentEntity.setAccepted(true);
            studentRepository.save(studentEntity);
        }
        projectRepository.save(projectEntity);

        List<Project> allProjects = projectRepository.findAll();
        for (Project p : allProjects) {
            if (!p.getId().equals(projectEntity.getId()) && p.getRequestedStudents().contains(studentEntity)) {
                p.getRequestedStudents().remove(studentEntity);
                projectRepository.save(p);
            }
        }

        redirectAttributes.addFlashAttribute("successMessage", "Student accepted successfully.");
        return "redirect:/staff/projects/applicants/" + projectId;
    }

    /**
     * Rejects a student's application to a project.
     *
     * @param studentId the ID of the student
     * @param projectId the ID of the project
     * @return redirect to the list of applicants for the project
     */
    @GetMapping("/staff/projects/applicant/reject/{studentId}/{projectId}")
    public String rejectApplicant(@PathVariable Long studentId, @PathVariable Long projectId) {
        ProjectDTO projectDTO = projectService.getProjectById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Project ID"));
        StudentDTO studentDTO = studentService.getStudentById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Student ID"));

        projectDTO.getRequestedStudents().remove(studentDTO);
        projectService.saveProject(projectDTO);

        return "redirect:/staff/projects/applicants/" + projectId;
    }

    /**
     * Removes an accepted student from a project.
     *
     * @param studentId the ID of the student to remove
     * @param projectId the ID of the project
     * @param redirectAttributes attributes to pass flash messages
     * @return redirect to the list of applicants for the project
     */
    @GetMapping("/staff/projects/remove/{studentId}/{projectId}")
    public String removeAcceptedStudent(@PathVariable Long studentId,
            @PathVariable Long projectId,
            RedirectAttributes redirectAttributes) {
        ProjectDTO projectDTO = projectService.getProjectById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Project ID"));
        StudentDTO studentDTO = studentService.getStudentById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Student ID"));

        if (projectDTO.getStudents().contains(studentDTO)) {
            projectDTO.getStudents().remove(studentDTO);
            studentDTO.setAccepted(false);
            studentService.saveStudent(studentDTO);
            projectService.saveProject(projectDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Student removed from accepted list successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Student is not in the accepted list.");
        }
        return "redirect:/staff/projects/applicants/" + projectId;
    }

    /**
     * Allows a student to request to join a project.
     *
     * @param projectId the ID of the project to join
     * @param principal the currently authenticated student
     * @param redirectAttributes attributes to pass flash messages
     * @return redirect to the list of available projects
     */
    @GetMapping("/student/projects/join/{projectId}")
    public String joinProject(@PathVariable Long projectId, Principal principal,
            RedirectAttributes redirectAttributes) {
        String email = principal.getName();
        StudentDTO studentDTO = studentService.getStudentByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Student not found"));

        if (studentDTO.isAccepted()) {
            redirectAttributes.addFlashAttribute("message",
                    "You are already part of a project and cannot apply to others.");
            return "redirect:/student/projects";
        }

        long pendingCount = projectService.getAllProjects().stream()
                .filter(p -> p.getRequestedStudents().contains(studentDTO))
                .count();
        if (pendingCount >= 5) {
            redirectAttributes.addFlashAttribute("message",
                    "You have already applied for 5 projects. Please wait until one is resolved before applying to another.");
            return "redirect:/student/projects";
        }

        Optional<ProjectDTO> projectOpt = projectService.getProjectById(projectId);
        if (projectOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Project not found.");
            redirectAttributes.addFlashAttribute("alertType", "error");
            return "redirect:/student/projects";
        }

        ProjectDTO projectDTO = projectOpt.get();

        boolean isEligible = projectService.isStudentEligibleForProject(studentDTO, projectDTO);
        if (!isEligible) {
            redirectAttributes.addFlashAttribute("message", "You are not eligible to join this project");
            redirectAttributes.addFlashAttribute("alertType", "error");
            return "redirect:/student/projects";
        }

        if (projectDTO.getStatus() != Project.Status.OPEN) {
            redirectAttributes.addFlashAttribute("message", "Project is not open for applications");
            redirectAttributes.addFlashAttribute("alertType", "error");
            return "redirect:/student/projects";
        }

        if (projectDTO.getStudents().contains(studentDTO)) {
            redirectAttributes.addFlashAttribute("message", "You are already part of this project");
            redirectAttributes.addFlashAttribute("alertType", "error");
        } else if (projectDTO.getRequestedStudents().contains(studentDTO)) {
            redirectAttributes.addFlashAttribute("message", "Request already pending");
            redirectAttributes.addFlashAttribute("alertType", "error");
        } else {
            projectDTO.getRequestedStudents().add(studentDTO);
            projectService.saveProject(projectDTO);
            redirectAttributes.addFlashAttribute("message", "Join request sent successfully");
        }
        return "redirect:/student/projects";
    }

    /**
     * Displays the form to create a new project.
     *
     * @param model the model to hold the new project object
     * @return the new project form view
     */
    @GetMapping("/staff/projects/new")
    public String newProjectForm(Model model) {
        model.addAttribute("project", new Project());
        return "new_project";
    }

    /**
     * Saves a new project along with optional deliverable files.
     *
     * @param projectDTO the project details
     * @param files optional uploaded files
     * @param principal the currently authenticated staff user
     * @param redirectAttributes attributes to pass flash messages
     * @return redirect to the list of staff projects
     * @throws Exception if file handling fails
     */
    @PostMapping("/staff/projects/save")
    public String saveProject(@ModelAttribute ProjectDTO projectDTO,
            @RequestParam(value = "files", required = false) MultipartFile[] files,
            Principal principal,
            RedirectAttributes redirectAttributes) throws Exception {

        Path uploadDir = Paths.get("uploads");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (!(user instanceof Staff)) {
            throw new AccessDeniedException("You are not authorized to create projects");
        }
        Staff staff = (Staff) user;
        projectDTO.setSupervisor(staff);

        if (projectDTO.getType() == Project.ProjectType.INDIVIDUAL) {
            projectDTO.setMaxStudents(1);
        } else {
            if (projectDTO.getMaxStudents() == null) {
                projectDTO.setMaxStudents(2);
            }
        }
        if (projectDTO.getStatus() == null) {
            projectDTO.setStatus(Project.Status.OPEN);
        }
        if (projectDTO.getAppStatus() == null) {
            projectDTO.setAppStatus(Project.ApplicationStatus.PENDING);
        }

        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                if (!file.isEmpty() && !file.getOriginalFilename().toLowerCase().endsWith(".pdf")) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Only PDF files are allowed.");
                    return "redirect:/staff/projects/new";
                }
            }
        }

        ProjectDTO savedProjectDTO = projectService.saveProject(projectDTO);
        Project savedProject = projectMapper.projectDTOtoProject(savedProjectDTO);

        if (files != null && files.length > 0) {
            List<Deliverable> deliverables = new ArrayList<>();
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String uniqueFileName = "PRJ" + savedProject.getId() + "_" + file.getOriginalFilename();
                    Path filePath = uploadDir.resolve(uniqueFileName);
                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                    Deliverable deliverable = new Deliverable();
                    deliverable.setName(file.getOriginalFilename());
                    deliverable.setFilePath(uniqueFileName);
                    deliverable.setProject(savedProject);
                    deliverables.add(deliverable);
                }
            }
            savedProject.getDeliverables().addAll(deliverables);
            projectRepository.save(savedProject);
        }

        return "redirect:/staff/projects";
    }

    private static final List<Path> FILE_PATHS = List.of(
            Paths.get("student_submissions"),
            Paths.get("uploads"));

    /**
     * Downloads a file from either the student submissions or uploads directory.
     *
     * @param filename the name of the file to download
     * @return the file resource as a response
     * @throws IOException if file access fails
     */
    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) throws IOException {
        for (Path basePath : FILE_PATHS) {
            Path filePath = basePath.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            }
        }
        throw new FileNotFoundException("File not found: " + filename);
    }

    /**
     * Displays the form to edit an existing project.
     *
     * @param id the ID of the project to edit
     * @param model the model to populate with project data
     * @param principal the currently authenticated staff user
     * @return the edit project form view or redirect if not authorized
     */
    @GetMapping("/staff/projects/edit/{id}")
    public String editProjectForm(@PathVariable Long id, Model model, Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!(user instanceof Staff)) {
            return "redirect:/staff/projects?error=Only staff are allowed to edit projects";
        }
        Staff loggedInStaff = (Staff) user;

        ProjectDTO projectDTO = projectService.getProjectById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Project ID: " + id));

        if (!projectDTO.getSupervisor().equals(loggedInStaff)) {
            return "redirect:/staff/projects?error=You are not authorized to edit this project";
        }

        if (projectDTO.getStatus() != Project.Status.OPEN) {
            return "redirect:/staff/projects?error=Closed projects cannot be edited";
        }

        model.addAttribute("category", List.of("Artificial Intelligence", "Software Engineering", "Cybersecurity",
                "Data Science", "Networks", "Web Development", "Software Development"));
        model.addAttribute("studyYears", List.of("Freshman", "Sophomore", "Junior", "Senior"));
        model.addAttribute("degrees", List.of("Undergraduate", "Graduate"));
        model.addAttribute("majors", List.of("Information Technologies", "Computer Science", "Computer Engineering",
                "Electrical Engineering"));

        model.addAttribute("project", projectDTO);
        return "edit_project";
    }

    /**
     * Updates an existing project with new details and optional new deliverable files.
     *
     * @param id the ID of the project to update
     * @param projectDTO the updated project data
     * @param files optional uploaded files
     * @param redirectAttributes attributes to pass flash messages
     * @return redirect to the list of staff projects
     * @throws IOException if file handling fails
     */
    @PostMapping("/staff/projects/update/{id}")
    public String updateProject(@PathVariable Long id, @ModelAttribute ProjectDTO projectDTO,
            @RequestParam(value = "files", required = false) MultipartFile[] files,
            RedirectAttributes redirectAttributes) throws IOException {
        ProjectDTO existingProject = projectService.getProjectById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Project ID"));

        existingProject.setTitle(projectDTO.getTitle());
        existingProject.setDescription(projectDTO.getDescription());
        existingProject.setObjectives(projectDTO.getObjectives());
        existingProject.setType(projectDTO.getType());
        existingProject.setStatus(projectDTO.getStatus());

        if (projectDTO.getType() == Project.ProjectType.INDIVIDUAL) {
            existingProject.setMaxStudents(1);
        } else {
            existingProject.setMaxStudents(projectDTO.getMaxStudents());
        }

        existingProject.setCategory(projectDTO.getCategory());
        existingProject.setStudyYearRestriction(projectDTO.getStudyYearRestriction());
        existingProject.setDegreeRestriction(projectDTO.getDegreeRestriction());
        existingProject.setMajorRestriction(projectDTO.getMajorRestriction());
        existingProject.setResearchFocus(projectDTO.getResearchFocus());
        existingProject.setSubcategories(projectDTO.getSubcategories());

        if (files != null && files.length > 0) {
            Path uploadDir = Paths.get("uploads");

            List<Deliverable> deliverables = new ArrayList<>();

            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String uniqueFileName = "PRJ" + existingProject.getId() + "_" + file.getOriginalFilename();
                    Path filePath = uploadDir.resolve(uniqueFileName);

                    if (Files.exists(filePath)) {
                        redirectAttributes.addFlashAttribute("errorMessage", "A file with this name already exists.");
                        return "redirect:/staff/projects/edit/" + id;
                    }

                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                    Deliverable deliverable = new Deliverable();
                    deliverable.setName(file.getOriginalFilename());
                    deliverable.setFilePath(uniqueFileName);
                    deliverable.setProject(projectMapper.projectDTOtoProject(existingProject));

                    deliverables.add(deliverable);
                }
            }

            deliverableRepository.saveAll(deliverables);

            List<DeliverableDTO> deliverableDTOs = deliverables.stream()
                    .map(deliverable -> deliverableMapper.deliverableToDeliverableDTO(deliverable))
                    .collect(Collectors.toList());

            existingProject.getDeliverables().addAll(deliverableDTOs);
        }

        projectService.saveProject(existingProject);
        redirectAttributes.addFlashAttribute("success", "Project updated successfully!");

        return "redirect:/staff/projects";
    }

    /**
     * Deletes a deliverable file associated with a project.
     *
     * @param deliverableId the ID of the deliverable to delete
     * @return HTTP response indicating success or failure
     */
    @DeleteMapping("/staff/projects/delete-file/{deliverableId}")
    public ResponseEntity<?> deleteFile(@PathVariable Long deliverableId) {
        Optional<Deliverable> deliverableOpt = deliverableRepository.findById(deliverableId);

        if (deliverableOpt.isPresent()) {
            Deliverable deliverable = deliverableOpt.get();
            Project project = deliverable.getProject();

            Path filePath = Paths.get("uploads").resolve(deliverable.getFilePath());
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error deleting the file from the server.");
            }

            project.getDeliverables().remove(deliverable);

            deliverableRepository.delete(deliverable);

            projectRepository.save(project);

            return ResponseEntity.ok().body("File deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found.");
        }
    }

    /**
     * Uploads a file as a deliverable for a specific project.
     *
     * @param id   the ID of the project to associate the uploaded file with
     * @param file the file to be uploaded
     * @return a ResponseEntity containing success status and message
     */
    @PostMapping("/staff/projects/upload/{id}")
    public ResponseEntity<?> uploadFile(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        Optional<Project> projectOpt = projectRepository.findById(id);
        if (projectOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Project not found."));
        }

        Project project = projectOpt.get();
        Path uploadDir = Paths.get("uploads");

        try {
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            String uniqueFileName = "PRJ" + project.getId() + "_" + file.getOriginalFilename();
            Path filePath = uploadDir.resolve(uniqueFileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            Deliverable deliverable = new Deliverable();
            deliverable.setName(file.getOriginalFilename());
            deliverable.setFilePath(uniqueFileName);
            deliverable.setProject(project);

            deliverableRepository.save(deliverable);

            return ResponseEntity.ok(Map.of("success", true, "message", "File uploaded successfully."));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "File upload error: " + e.getMessage()));
        }
    }

    /**
     * Deletes a project owned by the logged-in staff member.
     *
     * @param id        the ID of the project to delete
     * @param principal the currently authenticated user's principal
     * @return a redirect URL to the staff projects page with success or error message
     */
    @GetMapping("/staff/projects/delete/{id}")
    public String deleteProject(@PathVariable Long id, Principal principal) {

        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Staff loggedInStaff = (Staff) user;

        ProjectDTO projectDTO = projectService.getProjectById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Project ID: " + id));
        if (!projectDTO.getSupervisor().equals(loggedInStaff)) {
            return "redirect:/staff/projects?error=You are not authorized to delete this project";
        }
        projectService.deleteProject(id);
        return "redirect:/staff/projects";
    }

    /**
     * Toggles the status of a project.
     *
     * @param projectDTO the project to toggle the status for
     * @return a redirect URL to the staff projects page
     */
    @GetMapping("/toggle-status/{id}")
    public String toggleProjectStatus(@PathVariable ProjectDTO projectDTO) {
        projectService.toggleProjectStatus(projectDTO);
        return "redirect:/staff/projects";
    }
}
