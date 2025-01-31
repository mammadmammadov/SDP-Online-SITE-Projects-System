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

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;

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
            @RequestParam(required = false) Long supervisorId,
            Model model) {

        List<Project> projects = projectService.getProjectsByFilters(category, keywords, supervisorId);

        model.addAttribute("projects", projects);
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
    public String viewApplicants(@PathVariable Long projectId, Model model, Principal principal)
            throws AccessDeniedException {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!(user instanceof Staff staff)) {
            throw new AccessDeniedException("You are not authorized to view applicants");
        }

        Project project = projectService.getProjectById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Project ID"));

        if (!project.getSupervisor().equals(staff)) {
            throw new AccessDeniedException("You are not authorized to view applicants for this project");
        }

        model.addAttribute("project", project);
        return "applicants";
    }

    @GetMapping("/staff/projects/applicant/accept/{studentId}/{projectId}")
    public String acceptApplicant(@PathVariable Long studentId, @PathVariable Long projectId) {
        Project project = projectService.getProjectById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Project ID"));
        Student student = studentService.getStudentById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Student ID"));

        project.addAcceptedStudent(student);
        studentService.addStudentToProject(student, project);

        projectService.saveProject(project);
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

    @GetMapping("/student/projects/join/{projectId}")
    public String joinProject(@PathVariable Long projectId, Principal principal, Model model) {
        String email = principal.getName();
        Student student = studentService.getStudentByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        Project project = projectService.getProjectById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Project ID"));

        if (project.getStudents().contains(student)) {
            model.addAttribute("message", "You have already sent a request to join this project.");
            return "student_projects";
        }

        if (project.getStatus().equals(Project.Status.OPEN)) {
            project.addStudent(student);
            projectService.saveProject(project);
            model.addAttribute("message", "Your request has been sent to join the project.");
        } else {
            throw new IllegalArgumentException("Project is not open for applications");
        }

        return "student_projects";
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
            projectService.saveProject(project);
            return "redirect:/staff/projects";
        } else {
            throw new AccessDeniedException("You are not authorized to create projects");
        }
    }

    @GetMapping("/staff/projects/edit/{id}")
    public String editProjectForm(@PathVariable Long id, Model model) {
        Project project = projectService.getProjectById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Project ID: " + id));
        model.addAttribute("project", project);
        return "edit_project";
    }

    @PostMapping("/staff/projects/update/{id}")

    public String updateProject(@PathVariable Long id, @ModelAttribute Project project, Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Project existingProject = projectService.getProjectById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Project ID: " + id));

        if (project.getType() == null) {
            project.setType(existingProject.getType());
        }

        if (user instanceof Staff staff) {
            project.setSupervisor(staff);
        }
        project.setId(id);
        projectService.saveProject(project);
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
