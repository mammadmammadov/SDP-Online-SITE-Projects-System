package az.edu.ada.SITE.Controller;

import az.edu.ada.SITE.DTO.AssignmentDTO;
import az.edu.ada.SITE.DTO.AssignmentSubmissionDTO;
import az.edu.ada.SITE.DTO.ProjectDTO;
import az.edu.ada.SITE.DTO.StudentDTO;
import az.edu.ada.SITE.Entity.Assignment;
import az.edu.ada.SITE.Entity.Staff;
import az.edu.ada.SITE.Mapper.ProjectMapper;
import az.edu.ada.SITE.Repository.UserRepository;
import az.edu.ada.SITE.Service.AssignmentService;
import az.edu.ada.SITE.Service.AssignmentSubmissionService;
import az.edu.ada.SITE.Service.ProjectService;
import az.edu.ada.SITE.Service.StudentService;
import jakarta.validation.Valid;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class StudentManagementController {

  private final ProjectService projectService;
  private final UserRepository userRepository;
  private final AssignmentService assignmentService;
  private final StudentService studentService;
  private final AssignmentSubmissionService assignmentSubmissionService;
  private final ProjectMapper projectMapper;

  public StudentManagementController(ProjectService projectService,
      UserRepository userRepository,
      AssignmentService assignmentService, StudentService studentService,
      AssignmentSubmissionService assignmentSubmissionService, ProjectMapper projectMapper) {
    this.projectService = projectService;
    this.userRepository = userRepository;
    this.assignmentService = assignmentService;
    this.studentService = studentService;
    this.assignmentSubmissionService = assignmentSubmissionService;
    this.projectMapper = projectMapper;
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
      @Valid @ModelAttribute("assignment") AssignmentDTO assignmentDTO,
      BindingResult bindingResult,
      Model model,
      RedirectAttributes redirectAttributes) {

    if (assignmentDTO.getDueDate() != null && assignmentDTO.getDueDate().isBefore(java.time.LocalDateTime.now())) {
      bindingResult.rejectValue("dueDate", "error.assignment", "Due date cannot be in the past");
    }

    if (bindingResult.hasErrors()) {
      model.addAttribute("assignment", assignmentDTO);
      return "new_assignment";
    }

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

    if (assignmentDTO.getDueDate() != null) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
      String formattedDueDate = assignmentDTO.getDueDate().format(formatter);
      model.addAttribute("formattedDueDate", formattedDueDate);
    }

    model.addAttribute("assignment", assignmentDTO);
    model.addAttribute("projectId", projectId);
    return "edit_assignment";
  }

  @PostMapping("/staff/student-management/{projectId}/assignments/update/{assignmentId}")
  public String updateAssignment(@PathVariable Long projectId,
      @PathVariable Long assignmentId,
      @Valid @ModelAttribute("assignment") AssignmentDTO assignmentDTO,
      BindingResult bindingResult,
      Model model,
      RedirectAttributes redirectAttributes) {

    if (assignmentDTO.getDueDate() != null && assignmentDTO.getDueDate().isBefore(java.time.LocalDateTime.now())) {
      bindingResult.rejectValue("dueDate", "error.assignment", "Due date cannot be in the past");
    }

    if (bindingResult.hasErrors()) {
      model.addAttribute("assignment", assignmentDTO);
      return "edit_assignment";
    }

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

    List<AssignmentSubmissionDTO> submissions = assignmentSubmissionService.getSubmissionsByAssignmentId(assignmentId);

    model.addAttribute("assignment", assignmentDTO);
    model.addAttribute("projectId", projectId);
    model.addAttribute("submissions", submissions);
    return "assignment_submissions";
  }

  @PostMapping("/staff/student-management/{projectId}/assignments/{assignmentId}/submissions/{submissionId}/grade")
  public String gradeSubmission(@PathVariable Long projectId,
      @PathVariable Long assignmentId,
      @PathVariable Long submissionId,
      @RequestParam Double grade,
      RedirectAttributes redirectAttributes) {
    AssignmentSubmissionDTO submissionDTO = assignmentSubmissionService.getSubmissionById(submissionId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid submission ID"));

    AssignmentDTO assignmentDTO = assignmentService.getAssignmentById(assignmentId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid assignment ID"));

    if (grade > assignmentDTO.getMaxGrade()) {
      redirectAttributes.addFlashAttribute("errorMessage",
          "Grade cannot exceed the maximum grade of the assignment.");
      return "redirect:/staff/student-management/" + projectId + "/assignments/" + assignmentId + "/submissions";
    }

    submissionDTO.setGrade(grade);
    assignmentSubmissionService.saveSubmission(submissionDTO);

    redirectAttributes.addFlashAttribute("successMessage", "Submission graded successfully.");
    return "redirect:/staff/student-management/" + projectId + "/assignments/" + assignmentId + "/submissions";
  }

  @GetMapping("/student/assignments")
  public String viewStudentAssignments(Model model, Principal principal) {
    StudentDTO studentDTO = studentService.getStudentByEmail(principal.getName())
        .orElseThrow(() -> new UsernameNotFoundException("Student not found"));

    try {
      ProjectDTO projectDTO = studentDTO.getProjects().stream()
          .map(projectMapper::projectToProjectDTO)
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException());

      List<AssignmentDTO> assignments = assignmentService.getAssignmentsByProjectId(projectDTO.getId());

      for (AssignmentDTO assignment : assignments) {
        assignment.setSubmission(assignmentSubmissionService
            .getSubmissionByAssignmentAndStudent(assignment.getId(), studentDTO.getId()).orElse(null));
      }

      model.addAttribute("project", projectDTO);
      model.addAttribute("assignments", assignments);
    } catch (IllegalArgumentException e) {
      model.addAttribute("message", "You are not enrolled in any project");
      model.addAttribute("assignments", Collections.emptyList());
    }

    return "student_assignments";
  }

  @PostMapping("/student/assignments/{assignmentId}/submit")
  public String submitAssignment(@PathVariable Long assignmentId,
      @RequestParam("file") MultipartFile file,
      Principal principal,
      RedirectAttributes redirectAttributes) throws Exception {
    String email = principal.getName();
    StudentDTO studentDTO = studentService.getStudentByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("Student not found"));

    if (!file.getContentType().equalsIgnoreCase("application/pdf")) {
      redirectAttributes.addFlashAttribute("errorMessage",
          "Invalid file type. Only PDF files are allowed.");
      return "redirect:/student/assignments";
    }

    Path uploadDir = Paths.get("student_submissions");
    if (!Files.exists(uploadDir)) {
      Files.createDirectories(uploadDir);
    }

    AssignmentDTO assignmentDTO = assignmentService.getAssignmentById(assignmentId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid assignment ID"));

    Long projectId = assignmentDTO.getProjectId();
    String uniqueFileName = "PRJ" + projectId + "_" + file.getOriginalFilename();
    Path filePath = uploadDir.resolve(uniqueFileName);
    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

    AssignmentSubmissionDTO submissionDTO = new AssignmentSubmissionDTO();
    submissionDTO.setFileName(file.getOriginalFilename());
    submissionDTO.setFilePath(uniqueFileName);
    submissionDTO.setSubmittedAt(java.time.LocalDateTime.now());
    submissionDTO.setAssignmentId(assignmentId);
    submissionDTO.setStudentId(studentDTO.getId());
    submissionDTO.setStudentName(studentDTO.getName() + " " + studentDTO.getSurname());

    assignmentSubmissionService.saveSubmission(submissionDTO);
    redirectAttributes.addFlashAttribute("successMessage", "Assignment submitted successfully!");
    return "redirect:/student/assignments";
  }
}
