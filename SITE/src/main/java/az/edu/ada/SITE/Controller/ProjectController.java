package az.edu.ada.SITE.Controller;

import az.edu.ada.SITE.DTO.ProjectDTO;
import az.edu.ada.SITE.DTO.StudentDTO;
import az.edu.ada.SITE.Entity.Project;
import az.edu.ada.SITE.Entity.Staff;
import az.edu.ada.SITE.Entity.Student;
import az.edu.ada.SITE.Entity.User;
import az.edu.ada.SITE.Mapper.ProjectMapper;
import az.edu.ada.SITE.Mapper.StudentMapper;
import az.edu.ada.SITE.Repository.UserRepository;
import az.edu.ada.SITE.Service.ProjectService;
import az.edu.ada.SITE.Service.StudentService;
import az.edu.ada.SITE.Entity.Rubric;
import az.edu.ada.SITE.Service.RubricService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping
public class ProjectController {
    private final ProjectService projectService;
    private final UserRepository userRepository;
    private final StudentService studentService;

    private final RubricService rubricService;

    public ProjectController(ProjectService projectService, UserRepository userRepository,
            StudentService studentService, RubricService rubricService) {
        this.projectService = projectService;
        this.userRepository = userRepository;
        this.studentService = studentService;
        this.rubricService = rubricService;
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

        model.addAttribute("project", projectDTO);
        return "edit_project_admin";
    }

    @PostMapping("/admin/projects/update/{id}")
    public String updateProjectAdmin(@PathVariable Long id, @ModelAttribute ProjectDTO projectDTO,
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
        var pageable = org.springframework.data.domain.PageRequest.of(page, 4);
        var projectPage = projectService.getEligibleProjectsForStudent(studentDTO, category, keywords, supervisorName,
                supervisorSurname, pageable);

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
            var pageable = org.springframework.data.domain.PageRequest.of(page, 4);
            var projectPage = projectService.getProjectsByStaffId(staffId, pageable);
            model.addAttribute("projects", projectPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", projectPage.getTotalPages());

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
                .map(StudentMapper.INSTANCE::studentDTOtoStudent)
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
        ProjectDTO projectDTO = projectService.getProjectById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Project ID"));
        StudentDTO studentDTO = studentService.getStudentById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Student ID"));

        projectDTO.getRequestedStudents().remove(studentDTO);

        if (projectDTO.getType() == Project.ProjectType.INDIVIDUAL) {
            if (!projectDTO.getStudents().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Individual project already has an accepted student.");
                return "redirect:/staff/projects/applicants/" + projectId;
            }
        } else {
            if (projectDTO.getStudents().size() >= projectDTO.getMaxStudents()) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Group project has reached its maximum number of accepted students.");
                return "redirect:/staff/projects/applicants/" + projectId;
            }
        }

        if (!projectDTO.getStudents().contains(studentDTO)) {
            projectDTO.getStudents().add(studentDTO);
            studentDTO.setAccepted(true);
            studentService.saveStudent(studentDTO);
        }
        projectService.saveProject(projectDTO);

        List<ProjectDTO> allProjects = projectService.getAllProjects();
        for (ProjectDTO p : allProjects) {
            if (!p.getId().equals(projectDTO.getId()) && p.getRequestedStudents().contains(studentDTO)) {
                p.getRequestedStudents().remove(studentDTO);
                projectService.saveProject(p);
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
    public String saveProject(@ModelAttribute ProjectDTO projectDTO, Principal principal) throws AccessDeniedException {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (user instanceof Staff staff) {
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

            projectService.saveProject(projectDTO);
            return "redirect:/staff/projects";
        } else {
            throw new AccessDeniedException("You are not authorized to create projects");
        }
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

        projectService.saveProject(existingProject);
        redirectAttributes.addFlashAttribute("success", "Project updated successfully!");

        return "redirect:/staff/projects";
    }

    @GetMapping("/staff/projects/delete/{id}")
    public String deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return "redirect:/staff/projects";
    }

    @GetMapping("/toggle-status/{id}")
    public String toggleProjectStatus(@PathVariable ProjectDTO projectDTO) {
        projectService.toggleProjectStatus(projectDTO);
        return "redirect:/staff/projects";
    }

    @GetMapping("/staff/projects/rubrics/{projectId}")
    public String manageRubrics(@PathVariable Long projectId, Model model, Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        ProjectDTO projectDTO = projectService.getProjectById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Project ID"));

        if (!(user instanceof Staff) || !projectDTO.getSupervisor().equals(user)) {
            return "redirect:/staff/projects?error=Unauthorized access";
        }

        List<Rubric> rubrics = rubricService.getRubricsByProjectId(projectId);
        double totalWeightage = rubrics.stream().mapToDouble(Rubric::getWeightage).sum();

        model.addAttribute("project", projectDTO);
        model.addAttribute("rubrics", rubrics);
        model.addAttribute("newRubric", new Rubric());
        model.addAttribute("totalWeightage", totalWeightage);
        return "rubric_management";
    }

    @PostMapping("/staff/projects/rubrics/save/{projectId}")
    public String saveRubric(@PathVariable Long projectId,
            @ModelAttribute("newRubric") Rubric rubric,
            RedirectAttributes redirectAttributes) {
        ProjectDTO projectDTO = projectService.getProjectById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Project ID"));

        Project project = ProjectMapper.INSTANCE.projectDTOtoProject(projectDTO);

        rubric.setProject(project);
        rubricService.saveRubric(rubric);

        redirectAttributes.addFlashAttribute("successMessage", "Rubric added successfully");
        return "redirect:/staff/projects/rubrics/" + projectId;
    }

    @GetMapping("/staff/projects/rubrics/delete/{rubricId}")
    public String deleteRubric(@PathVariable Long rubricId, RedirectAttributes redirectAttributes) {
        Rubric rubric = rubricService.getRubricById(rubricId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Rubric ID"));

        Long projectId = rubric.getProject().getId();
        rubricService.deleteRubric(rubricId);

        redirectAttributes.addFlashAttribute("successMessage", "Rubric deleted successfully");
        return "redirect:/staff/projects/rubrics/" + projectId;
    }
}
