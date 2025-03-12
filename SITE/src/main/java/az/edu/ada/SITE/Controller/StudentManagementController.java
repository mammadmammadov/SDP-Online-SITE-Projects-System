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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
  public String viewProjectStudentManagement(@PathVariable Long projectId,
      Model model,
      Principal principal,
      RedirectAttributes redirectAttributes) {
    String email = principal.getName();
    Staff staff = (Staff) userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    try {
      ProjectDTO projectDTO = projectService.getProjectById(projectId)
          .orElseThrow(() -> new IllegalArgumentException("Project not found"));

      if (!projectDTO.getSupervisor().getId().equals(staff.getId())) {
        redirectAttributes.addFlashAttribute("errorMessage",
            "You don't have permission to view this project");
        return "redirect:/staff/student-management";
      }

      List<Assignment> assignments = projectDTO.getAssignments();
      model.addAttribute("project", projectDTO);
      model.addAttribute("assignments", assignments);
      return "project_student_management";

    } catch (IllegalArgumentException e) {
      redirectAttributes.addFlashAttribute("errorMessage",
          e.getMessage().contains("not found") ? "Project Not Found"
              : "Invalid project ID");
      return "redirect:/staff/student-management";
    }
  }

  @GetMapping("/staff/student-management/{projectId}/assignments/new")
  public String newAssignmentForm(@PathVariable Long projectId, Model model, Principal principal,
      RedirectAttributes redirectAttributes) {
    String email = principal.getName();
    Staff staff = (Staff) userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    ProjectDTO projectDTO = projectService.getProjectById(projectId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid Project ID"));
    if (!projectDTO.getSupervisor().getId().equals(staff.getId())) {
      redirectAttributes.addFlashAttribute("errorMessage",
          "You don't have permission to add assignments for this project");
      return "redirect:/staff/student-management";
    }
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
      Model model, Principal principal, RedirectAttributes redirectAttributes) {
    String email = principal.getName();
    Staff staff = (Staff) userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    ProjectDTO projectDTO = projectService.getProjectById(projectId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid Project ID"));
    if (!projectDTO.getSupervisor().getId().equals(staff.getId())) {
      redirectAttributes.addFlashAttribute("errorMessage",
          "You don't have permission to edit assignments of this project");
      return "redirect:/staff/student-management";
    }
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
    existingAssignment.setRequiresSubmission(assignmentDTO.isRequiresSubmission());
    assignmentService.saveAssignment(existingAssignment);
    redirectAttributes.addFlashAttribute("successMessage", "Assignment updated successfully.");
    return "redirect:/staff/student-management/" + projectId;
  }

  @GetMapping("/staff/student-management/{projectId}/assignments/{assignmentId}/submissions/{submissionId}/delete")
  public String deleteSubmission(
      @PathVariable Long projectId,
      @PathVariable Long assignmentId,
      @PathVariable Long submissionId,
      RedirectAttributes redirectAttributes, Principal principal) {

    String email = principal.getName();
    Staff staff = (Staff) userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    ProjectDTO projectDTO = projectService.getProjectById(projectId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid Project ID"));

    if (!projectDTO.getSupervisor().getId().equals(staff.getId())) {
      redirectAttributes.addFlashAttribute("errorMessage",
          "You don't have permission to edit assignments of this project");
      return "redirect:/staff/student-management";
    }

    AssignmentSubmissionDTO submission = assignmentSubmissionService.getSubmissionById(submissionId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid submission ID"));

    try {
      if (submission.getFilePath() != null && !submission.getFilePath().isEmpty()) {
        Path filePath = Paths.get("student_submissions").resolve(submission.getFilePath());
        Files.deleteIfExists(filePath);
      }
    } catch (IOException e) {
      redirectAttributes.addFlashAttribute("errorMessage",
          "File deletion failed: " + e.getMessage());
    }

    assignmentSubmissionService.deleteSubmission(submissionId);
    redirectAttributes.addFlashAttribute("successMessage", "Submission deleted successfully.");
    return "redirect:/staff/student-management/" + projectId + "/assignments/" + assignmentId + "/submissions";
  }

  @GetMapping("/staff/student-management/{projectId}/assignments/delete/{assignmentId}")
  public String deleteAssignment(@PathVariable Long projectId,
      @PathVariable Long assignmentId,
      RedirectAttributes redirectAttributes, Principal principal) {
    String email = principal.getName();
    Staff staff = (Staff) userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    ProjectDTO projectDTO = projectService.getProjectById(projectId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid Project ID"));

    if (!projectDTO.getSupervisor().getId().equals(staff.getId())) {
      redirectAttributes.addFlashAttribute("errorMessage",
          "You don't have permission to delete assignments of this project");
      return "redirect:/staff/student-management";
    }
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

    ProjectDTO projectDTO = projectService.getProjectById(projectId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid Project ID"));

    List<AssignmentSubmissionDTO> submissionsToDisplay = new ArrayList<>();

    if (assignmentDTO.isRequiresSubmission()) {
      submissionsToDisplay = assignmentSubmissionService
          .getSubmissionsByAssignmentAndProject(assignmentId, projectId);

      if (projectDTO.getStudents().size() > 1) {
        submissionsToDisplay.removeIf(s -> s.getStudentId() != null);
      }
    } else {
      List<StudentDTO> students = projectDTO.getStudents();
      List<AssignmentSubmissionDTO> existingSubmissions = assignmentSubmissionService
          .getSubmissionsByAssignmentId(assignmentId);

      List<AssignmentSubmissionDTO> tempSubmissions = new ArrayList<>();
      students.forEach(student -> {
        Optional<AssignmentSubmissionDTO> existing = existingSubmissions.stream()
            .filter(s -> s.getStudentId() != null && s.getStudentId().equals(student.getId()))
            .findFirst();

        if (existing.isEmpty()) {
          AssignmentSubmissionDTO newSubmission = new AssignmentSubmissionDTO();
          newSubmission.setStudentId(student.getId());
          newSubmission.setStudentName(student.getName() + " " + student.getSurname());
          tempSubmissions.add(newSubmission);
        } else {
          tempSubmissions.add(existing.get());
        }
      });
      submissionsToDisplay.addAll(tempSubmissions);
    }

    model.addAttribute("project", projectDTO);
    model.addAttribute("assignment", assignmentDTO);
    model.addAttribute("submissions", submissionsToDisplay);
    return "assignment_submissions";
  }

  @PostMapping("/staff/student-management/{projectId}/assignments/{assignmentId}/submissions")
  public String gradeSubmission(
      @PathVariable Long projectId,
      @PathVariable Long assignmentId,
      @RequestParam(required = false) Long submissionId,
      @RequestParam(required = false) Long studentId,
      @RequestParam(required = false) Double grade,
      @RequestParam(required = false) String feedback,
      RedirectAttributes redirectAttributes) {

    try {
      AssignmentDTO assignment = assignmentService.getAssignmentById(assignmentId)
          .orElseThrow(() -> new IllegalArgumentException("Invalid assignment ID"));

      ProjectDTO project = projectService.getProjectById(projectId)
          .orElseThrow(() -> new IllegalArgumentException("Invalid project ID"));

      if (grade != null) {
        if (grade < 0)
          throw new IllegalArgumentException("Grade cannot be negative");
        if (grade > assignment.getMaxGrade()) {
          throw new IllegalArgumentException("Grade cannot exceed maximum value");
        }
      }

      if (feedback != null && feedback.length() > 300) {
        throw new IllegalArgumentException("Feedback cannot exceed 300 characters");
      }

      if (assignment.isRequiresSubmission()) {
        if (submissionId == null) {
          throw new IllegalArgumentException("Submission ID required for grading");
        }

        AssignmentSubmissionDTO submission = assignmentSubmissionService.getSubmissionById(submissionId)
            .orElseThrow(() -> new IllegalArgumentException("Submission not found"));

        if (project.getStudents().size() > 1 && submission.getStudentId() != null) {
          throw new IllegalArgumentException("Invalid team submission format");
        }

        submission.setGrade(grade);
        submission.setFeedback(feedback);
        assignmentSubmissionService.saveSubmission(submission);
      } else {
        if (studentId == null) {
          throw new IllegalArgumentException("Student ID required for grading");
        }

        boolean validStudent = project.getStudents().stream()
            .anyMatch(s -> s.getId().equals(studentId));

        if (!validStudent) {
          throw new IllegalArgumentException("Student not found in project");
        }

        AssignmentSubmissionDTO submission = assignmentSubmissionService
            .getOrCreateIndividualSubmission(assignmentId, studentId);

        submission.setGrade(grade);
        submission.setFeedback(feedback);
        assignmentSubmissionService.saveSubmission(submission);
      }

      redirectAttributes.addFlashAttribute("successMessage", "Grade updated successfully");
    } catch (IllegalArgumentException e) {
      redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred");
    }

    return "redirect:/staff/student-management/" + projectId +
        "/assignments/" + assignmentId +
        "/submissions?t=" + System.currentTimeMillis();
  }

  @GetMapping("/student/assignments")
  public String viewStudentAssignments(Model model, Principal principal) {
    StudentDTO studentDTO = studentService.getStudentByEmail(principal.getName())
        .orElseThrow(() -> new UsernameNotFoundException("Student not found"));

    try {
      ProjectDTO projectDTO = studentDTO.getProjects().stream()
          .map(projectMapper::projectToProjectDTO)
          .findFirst()
          .orElseThrow(IllegalArgumentException::new);

      List<AssignmentDTO> assignments = assignmentService.getAssignmentsByProjectId(projectDTO.getId());

      for (AssignmentDTO assignment : assignments) {
        if (assignment.isRequiresSubmission()) {
          List<AssignmentSubmissionDTO> teamSubmissions = assignmentSubmissionService
              .getSubmissionsByAssignmentAndProject(assignment.getId(), projectDTO.getId());
          assignment.setSubmissions(teamSubmissions);
        } else {
          assignment.setSubmission(assignmentSubmissionService
              .getSubmissionByAssignmentAndStudent(assignment.getId(), studentDTO.getId())
              .orElse(null));
        }
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

    if (file.isEmpty()) {
      redirectAttributes.addFlashAttribute("errorMessage", "File cannot be empty");
      return "redirect:/student/assignments";
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
    submissionDTO.setSubmittedAt(LocalDateTime.now());
    submissionDTO.setAssignmentId(assignmentId);
    submissionDTO.setProjectId(projectId);

    ProjectDTO projectDTO = projectService.getProjectById(projectId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid project ID"));
    boolean isGroupProject = projectDTO.getStudents().size() > 1;
    if (!isGroupProject) {
      submissionDTO.setStudentId(studentDTO.getId());
    }

    assignmentSubmissionService.saveSubmission(submissionDTO);
    redirectAttributes.addFlashAttribute("successMessage", "Assignment submitted successfully!");
    return "redirect:/student/assignments";
  }
}
