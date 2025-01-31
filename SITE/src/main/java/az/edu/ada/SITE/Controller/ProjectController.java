package az.edu.ada.SITE.Controller;

import az.edu.ada.SITE.Entity.Project;
import az.edu.ada.SITE.Entity.Staff;
import az.edu.ada.SITE.Entity.User;
import az.edu.ada.SITE.Repository.UserRepository;
import az.edu.ada.SITE.Service.ProjectService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/staff/projects")
public class ProjectController {
    private final ProjectService projectService;
    private final UserRepository userRepository;

    public ProjectController(ProjectService projectService, UserRepository userRepository) {
        this.projectService = projectService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String viewProjects(Model model, Principal principal) {
        try {
            String email = principal.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            String fullName = user.getName() + " " + user.getSurname();
            model.addAttribute("staffName", fullName);

            List<Project> projects = projectService.getAllProjects();
            model.addAttribute("projects", projects);

            return "staff_projects";
        } catch (UsernameNotFoundException e) {
            return "redirect:/auth/login?error=user_not_found";
        }
    }

    @GetMapping("/new")
    public String newProjectForm(Model model) {
        model.addAttribute("project", new Project());
        return "new_project";
    }

    @PostMapping("/save")
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

    @GetMapping("/edit/{id}")
    public String editProjectForm(@PathVariable Long id, Model model) {
        Project project = projectService.getProjectById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Project ID: " + id));
        model.addAttribute("project", project);
        return "edit_project";
    }

    @PostMapping("/update/{id}")
    public String updateProject(@PathVariable Long id, @ModelAttribute Project project, Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Project existingProject = projectService.getProjectById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Project ID: " + id));

        if (project.getType() == null) {
            project.setType(existingProject.getType()); // Preserve the previous type
        }

        if (user instanceof Staff staff) {
            project.setSupervisor(staff);
        }
        project.setId(id);
        projectService.saveProject(project);
        return "redirect:/staff/projects";
    }


    @GetMapping("/delete/{id}")
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
