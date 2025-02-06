package az.edu.ada.SITE.Controller;

import az.edu.ada.SITE.Entity.Project;
import az.edu.ada.SITE.Entity.Staff;
import az.edu.ada.SITE.Entity.Student;
import az.edu.ada.SITE.Entity.User;
import az.edu.ada.SITE.Repository.UserRepository;
import az.edu.ada.SITE.Service.ProjectService;
import az.edu.ada.SITE.Service.StudentService;
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

    public ProjectController(ProjectService projectService, UserRepository userRepository,
            StudentService studentService) {
        this.projectService = projectService;
        this.userRepository = userRepository;
        this.studentService = studentService;
    }

    @GetMapping("/student/projects")
    public String viewProjects(@RequestParam(required = false) String category,
            @RequestParam(required = false) String keywords,
            @RequestParam(required = false) String supervisorName,
            @RequestParam(required = false) String supervisorSurname,
            Model model, Principal principal) {

        String email = principal.getName();
        Student student = studentService.getStudentByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Student not found"));

        List<Project> projects = projectService.getEligibleProjectsForStudent(student, category, keywords,
                supervisorName, supervisorSurname);

        List<String> categories = List.of("Artificial Intelligence", "Software Engineering", "Cybersecurity",
                "Data Science", "Networks", "Web Development", "Software Development");

        model.addAttribute("student", student);
        model.addAttribute("studentName", student.getName() + " " + student.getSurname());
        model.addAttribute("projects", projects);
        model.addAttribute("category", categories);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("keywords", keywords);
        model.addAttribute("supervisorName", supervisorName);
        model.addAttribute("supervisorSurname", supervisorSurname);

        return "student_projects";
    }

    @GetMapping("/staff/projects")
    public String viewProjects(Model model, Principal principal) {
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
            List<Project> projects = projectService.getProjectsByStaffId(staffId);
            model.addAttribute("projects", projects);

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

        Project project = projectService.getProjectById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Project ID"));

        if (!project.getSupervisor().equals(staff)) {
            return "redirect:/staff/projects?error=You are not authorized to view applicants for this project";
        }

        List<Student> pendingApplicants = project.getRequestedStudents().stream()
                .filter(student -> !student.isAccepted())
                .collect(Collectors.toList());

        List<Student> eligibleStudents = studentService.getAllStudents().stream()
                .filter(student -> !student.isAccepted())
                .filter(student -> project.getStudyYearRestriction().contains(student.getStudyYear()))
                .filter(student -> project.getDegreeRestriction().contains(student.getDegree()))
                .filter(student -> project.getMajorRestriction().contains(student.getMajor()))
                .collect(Collectors.toList());

        if (searchEmail != null && !searchEmail.isBlank()) {
            String searchLower = searchEmail.toLowerCase();
            eligibleStudents = eligibleStudents.stream()
                    .filter(student -> student.getEmail().toLowerCase().contains(searchLower))
                    .collect(Collectors.toList());
        }

        model.addAttribute("project", project);
        model.addAttribute("eligibleStudents", eligibleStudents);
        model.addAttribute("project", project);
        model.addAttribute("applicants", pendingApplicants);
        return "applicants";
    }

    @GetMapping("/staff/projects/applicant/accept/{studentId}/{projectId}")
    public String acceptApplicant(@PathVariable Long studentId,
            @PathVariable Long projectId,
            RedirectAttributes redirectAttributes) {
        Project project = projectService.getProjectById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Project ID"));
        Student student = studentService.getStudentById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Student ID"));

        project.getRequestedStudents().remove(student);

        if (project.getType() == Project.ProjectType.INDIVIDUAL) {
            if (!project.getStudents().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Individual project already has an accepted student.");
                return "redirect:/staff/projects/applicants/" + projectId;
            }
        } else {
            if (project.getStudents().size() >= project.getMaxStudents()) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Group project has reached its maximum number of accepted students.");
                return "redirect:/staff/projects/applicants/" + projectId;
            }
        }

        if (!project.getStudents().contains(student)) {
            project.getStudents().add(student);
            student.setAccepted(true);
            studentService.saveStudent(student);
        }

        projectService.saveProject(project);

        List<Project> allProjects = projectService.getAllProjects();
        for (Project p : allProjects) {
            if (!p.getId().equals(project.getId()) && p.getRequestedStudents().contains(student)) {
                p.getRequestedStudents().remove(student);
                projectService.saveProject(p);
            }
        }

        redirectAttributes.addFlashAttribute("successMessage", "Student accepted successfully.");
        return "redirect:/staff/projects/applicants/" + projectId;
    }

    @GetMapping("/staff/projects/applicant/reject/{studentId}/{projectId}")
    public String rejectApplicant(@PathVariable Long studentId, @PathVariable Long projectId) {
        Project project = projectService.getProjectById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Project ID"));
        Student student = studentService.getStudentById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Student ID"));

        project.removeStudent(student);
        projectService.saveProject(project);

        return "redirect:/staff/projects/applicants/" + projectId;
    }

    @GetMapping("/staff/projects/remove/{studentId}/{projectId}")
    public String removeAcceptedStudent(@PathVariable Long studentId,
            @PathVariable Long projectId,
            RedirectAttributes redirectAttributes) {
        Project project = projectService.getProjectById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Project ID"));
        Student student = studentService.getStudentById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Student ID"));

        if (project.getStudents().contains(student)) {
            project.getStudents().remove(student);
            student.setAccepted(false);
            studentService.saveStudent(student);
            projectService.saveProject(project);
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
        Student student = studentService.getStudentByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Student not found"));

        long pendingCount = projectService.getAllProjects().stream()
                .filter(p -> p.getRequestedStudents().contains(student))
                .count();
        if (pendingCount >= 5) {
            redirectAttributes.addFlashAttribute("message",
                    "You have already applied for 5 projects. Please wait until one is resolved before applying to another.");
            return "redirect:/student/projects";
        }

        Project project = projectService.getProjectById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Project ID"));

        if (project.getStatus() != Project.Status.OPEN) {
            redirectAttributes.addFlashAttribute("message", "Project is not open for applications");
            return "redirect:/student/projects";
        }

        if (project.getStudents().contains(student)) {
            redirectAttributes.addFlashAttribute("message", "You are already part of this project");
        } else if (project.getRequestedStudents().contains(student)) {
            redirectAttributes.addFlashAttribute("message", "Request already pending");
        } else {
            project.getRequestedStudents().add(student);
            projectService.saveProject(project);
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
    public String saveProject(@ModelAttribute Project project, Principal principal) throws AccessDeniedException {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (user instanceof Staff staff) {
            project.setSupervisor(staff);

            if (project.getType() == Project.ProjectType.INDIVIDUAL) {
                project.setMaxStudents(1);
            } else {
                if (project.getMaxStudents() == null) {
                    project.setMaxStudents(2);
                }
            }

            projectService.saveProject(project);
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

        Project project = projectService.getProjectById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Project ID: " + id));

        if (!project.getSupervisor().equals(loggedInStaff)) {
            return "redirect:/staff/projects?error=You are not authorized to edit this project";
        }

        if (project.getStatus() != Project.Status.OPEN) {
            return "redirect:/staff/projects?error=Closed projects cannot be edited";
        }

        model.addAttribute("category", List.of("Artificial Intelligence", "Software Engineering", "Cybersecurity",
                "Data Science", "Networks", "Web Development", "Software Development"));
        model.addAttribute("studyYears", List.of("Freshman", "Sophomore", "Junior", "Senior"));
        model.addAttribute("degrees", List.of("Undergraduate", "Graduate"));
        model.addAttribute("majors", List.of("Information Technologies", "Computer Science", "Computer Engineering",
                "Electrical Engineering"));

        model.addAttribute("project", project);
        return "edit_project";
    }

    @PostMapping("/staff/projects/update/{id}")
    public String updateProject(@PathVariable Long id, @ModelAttribute Project project,
            RedirectAttributes redirectAttributes) {
        Project existingProject = projectService.getProjectById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Project ID"));

        existingProject.setTitle(project.getTitle());
        existingProject.setDescription(project.getDescription());
        existingProject.setObjectives(project.getObjectives());
        existingProject.setType(project.getType());
        existingProject.setStatus(project.getStatus());
        if (project.getType() == Project.ProjectType.INDIVIDUAL) {
            existingProject.setMaxStudents(1);
        } else {
            existingProject.setMaxStudents(project.getMaxStudents());
        }
        existingProject.setCategory(project.getCategory());
        existingProject.setStudyYearRestriction(project.getStudyYearRestriction());
        existingProject.setDegreeRestriction(project.getDegreeRestriction());
        existingProject.setMajorRestriction(project.getMajorRestriction());
        existingProject.setResearchFocus(project.getResearchFocus());

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
    public String toggleProjectStatus(@PathVariable Long id) {
        projectService.toggleProjectStatus(id);
        return "redirect:/staff/projects";
    }
}
