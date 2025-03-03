package az.edu.ada.SITE.Controller;

import az.edu.ada.SITE.DTO.AssignmentDTO;
import az.edu.ada.SITE.DTO.ProjectDTO;
import az.edu.ada.SITE.Entity.Assignment;
import az.edu.ada.SITE.Entity.Staff;
import az.edu.ada.SITE.Repository.UserRepository;
import az.edu.ada.SITE.Service.AssignmentService;
import az.edu.ada.SITE.Service.ProjectService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class StudentManagementController {

  private final ProjectService projectService;
  private final UserRepository userRepository;
  private final AssignmentService assignmentService;

  public StudentManagementController(ProjectService projectService,
      UserRepository userRepository,
      AssignmentService assignmentService) {
    this.projectService = projectService;
    this.userRepository = userRepository;
    this.assignmentService = assignmentService;
  }

  @GetMapping("/staff/student-management")
  public String viewProjectsForStudentManagement(Model model, Principal principal) {
    String email = principal.getName();
    Staff staff = (Staff) userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    List<ProjectDTO> projects = projectService.getAllProjects().stream()
        .filter(p -> p.getSupervisor().getId().equals(staff.getId()))
        .collect(Collectors.toList());
    model.addAttribute("projects", projects);
    return "student_management_staff";
  }

  @GetMapping("/staff/student-management/{projectId}")
  public String viewProjectStudentManagement(@PathVariable Long projectId, Model model, Principal principal) {
    ProjectDTO projectDTO = projectService.getProjectById(projectId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid Project ID"));
    List<Assignment> assignments = projectDTO.getAssignments();
    model.addAttribute("project", projectDTO);
    model.addAttribute("assignments", assignments);
    return "project_student_management";
  }

  @GetMapping("/staff/student-management/{projectId}/assignments/new")
  public String newAssignmentForm(@PathVariable Long projectId, Model model, Principal principal,
      RedirectAttributes redirectAttributes) {
    ProjectDTO projectDTO = projectService.getProjectById(projectId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid Project ID"));
    double currentTotal = projectDTO.getAssignments().stream()
        .mapToDouble(a -> a.getMaxGrade() != null ? a.getMaxGrade() : 0)
        .sum();
    model.addAttribute("currentTotalGrade", currentTotal);
    AssignmentDTO assignmentDTO = new AssignmentDTO();
    assignmentDTO.setProjectId(projectId);
    model.addAttribute("assignment", assignmentDTO);
    return "new_assignment";
  }

  @PostMapping("/staff/student-management/{projectId}/assignments/save")
  public String saveAssignment(@PathVariable Long projectId,
      @ModelAttribute AssignmentDTO assignmentDTO,
      RedirectAttributes redirectAttributes) {
    ProjectDTO projectDTO = projectService.getProjectById(projectId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid Project ID"));
    double currentTotal = projectDTO.getAssignments().stream()
        .mapToDouble(a -> a.getMaxGrade() != null ? a.getMaxGrade() : 0)
        .sum();
    if (currentTotal + assignmentDTO.getMaxGrade() > 100) {
      redirectAttributes.addFlashAttribute("errorMessage",
          "Total maximum grade for assignments cannot exceed 100. Current total: " + currentTotal);
      return "redirect:/staff/student-management/" + projectId + "/assignments/new";
    }
    assignmentDTO.setProjectId(projectId);
    assignmentService.saveAssignment(assignmentDTO);
    redirectAttributes.addFlashAttribute("successMessage", "Assignment created successfully.");
    return "redirect:/staff/student-management/" + projectId;
  }

  @GetMapping("/staff/student-management/{projectId}/assignments/edit/{assignmentId}")
  public String editAssignmentForm(@PathVariable Long projectId,
      @PathVariable Long assignmentId,
      Model model) {
    AssignmentDTO assignmentDTO = assignmentService.getAssignmentById(assignmentId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid Assignment ID"));
    model.addAttribute("assignment", assignmentDTO);
    model.addAttribute("projectId", projectId);
    return "edit_assignment";
  }

  @PostMapping("/staff/student-management/{projectId}/assignments/update/{assignmentId}")
  public String updateAssignment(@PathVariable Long projectId,
      @PathVariable Long assignmentId,
      @ModelAttribute AssignmentDTO assignmentDTO,
      RedirectAttributes redirectAttributes) {
    AssignmentDTO existingAssignment = assignmentService.getAssignmentById(assignmentId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid Assignment ID"));
    ProjectDTO projectDTO = projectService.getProjectById(projectId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid Project ID"));
    double otherTotal = projectDTO.getAssignments().stream()
        .filter(a -> !a.getId().equals(assignmentId))
        .mapToDouble(a -> a.getMaxGrade() != null ? a.getMaxGrade() : 0)
        .sum();
    if (otherTotal + assignmentDTO.getMaxGrade() > 100) {
      redirectAttributes.addFlashAttribute("errorMessage",
          "Total maximum grade for assignments cannot exceed 100. Other assignments total: " + otherTotal);
      return "redirect:/staff/student-management/" + projectId + "/assignments/edit/" + assignmentId;
    }
    existingAssignment.setTitle(assignmentDTO.getTitle());
    existingAssignment.setDescription(assignmentDTO.getDescription());
    existingAssignment.setDueDate(assignmentDTO.getDueDate());
    existingAssignment.setMaxGrade(assignmentDTO.getMaxGrade());
    assignmentService.saveAssignment(existingAssignment);
    redirectAttributes.addFlashAttribute("successMessage", "Assignment updated successfully.");
    return "redirect:/staff/student-management/" + projectId;
  }

  @GetMapping("/staff/student-management/{projectId}/assignments/delete/{assignmentId}")
  public String deleteAssignment(@PathVariable Long projectId,
      @PathVariable Long assignmentId,
      RedirectAttributes redirectAttributes) {
    assignmentService.deleteAssignment(assignmentId);
    redirectAttributes.addFlashAttribute("successMessage", "Assignment deleted successfully.");
    return "redirect:/staff/student-management/" + projectId;
  }

  @GetMapping("/staff/student-management/{projectId}/assignments/{assignmentId}/submissions")
  public String viewSubmissions(@PathVariable Long projectId,
      @PathVariable Long assignmentId,
      Model model) {
    AssignmentDTO assignmentDTO = assignmentService.getAssignmentById(assignmentId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid assignment ID"));
    model.addAttribute("assignment", assignmentDTO);
    model.addAttribute("projectId", projectId);
    return "assignment_submissions";
  }

  @PostMapping("/staff/student-management/{projectId}/assignments/{assignmentId}/submissions/{submissionId}/grade")
  public String gradeSubmission(@PathVariable Long projectId,
      @PathVariable Long assignmentId,
      @PathVariable Long submissionId,
      @RequestParam Double grade,
      RedirectAttributes redirectAttributes) {
    AssignmentDTO assignmentDTO = assignmentService.getAssignmentById(assignmentId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid assignment ID"));
    if (grade > assignmentDTO.getMaxGrade()) {
      redirectAttributes.addFlashAttribute("errorMessage", "Grade cannot exceed the maximum grade of the assignment.");
      return "redirect:/staff/student-management/" + projectId + "/assignments/" + assignmentId + "/submissions";
    }
    redirectAttributes.addFlashAttribute("successMessage", "Submission graded successfully.");
    return "redirect:/staff/student-management/" + projectId + "/assignments/" + assignmentId + "/submissions";
  }
}
