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

    @GetMapping("/admin/students")
    public String showStudents(Model model) {
        List<Student> students = userRepository.findAll().stream()
                .filter(user -> user instanceof Student)
                .map(user -> (Student) user)
                .collect(Collectors.toList());

        model.addAttribute("students", students);
        return "admin_students";
    }

    @GetMapping("/admin/staff")
    public String showStaff(Model model) {
        List<Staff> staff_members = userRepository.findAll().stream()
                .filter(user -> user instanceof Staff)
                .map(user -> (Staff) user)
                .collect(Collectors.toList());

        model.addAttribute("staff", staff_members);
        return "admin_staff";
    }

    @GetMapping("/admin/projects")
    public String showProjects(Model model) {
        List<ProjectDTO> projects = projectService.getAllProjects();
        model.addAttribute("projects", projects);
        return "admin_projects";
    }

    @GetMapping("/admin/projects/delete/{id}")
    public String deleteProjectAdmin(@PathVariable Long id) {
        projectService.deleteProject(id);
        return "redirect:/admin/projects";
    }

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
            mergedProjects.addAll(staffProjects);
            mergedProjects.addAll(otherProjects);

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

        ProjectDTO projectDTO = projectService.getProjectById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Project ID"));

        if (projectDTO.getStatus() != Project.Status.OPEN) {
            redirectAttributes.addFlashAttribute("message", "Project is not open for applications");
            return "redirect:/student/projects";
        }

        if (projectDTO.getStudents().contains(studentDTO)) {
            redirectAttributes.addFlashAttribute("message", "You are already part of this project");
        } else if (projectDTO.getRequestedStudents().contains(studentDTO)) {
            redirectAttributes.addFlashAttribute("message", "Request already pending");
        } else {
            projectDTO.getRequestedStudents().add(studentDTO);
            projectService.saveProject(projectDTO);
            redirectAttributes.addFlashAttribute("message", "Join request sent successfully");
        }
        return "redirect:/student/projects";
    }

    @GetMapping("/staff/projects/new")
    public String newProjectForm(Model model) {
        model.addAttribute("project", new Project());
        return "new_project";
    }

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

    @GetMapping("/toggle-status/{id}")
    public String toggleProjectStatus(@PathVariable ProjectDTO projectDTO) {
        projectService.toggleProjectStatus(projectDTO);
        return "redirect:/staff/projects";
    }
}
