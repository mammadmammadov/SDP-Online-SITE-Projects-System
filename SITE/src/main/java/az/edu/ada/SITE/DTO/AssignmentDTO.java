package az.edu.ada.SITE.DTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import az.edu.ada.SITE.Entity.Staff;

/**
 * Data Transfer Object (DTO) for representing an assignment within a project.
 * Used for creating, updating, and displaying assignment information.
 */
@Data
public class AssignmentDTO {

  /** The unique identifier of the assignment. */
  private Long id;

  /**
   * The title of the assignment.
   * Must not be blank and cannot exceed 50 characters.
   */
  @NotBlank(message = "Title is required")
  @Size(max = 50, message = "Title cannot be longer than 50 characters")
  private String title;

  /**
   * An optional description of the assignment.
   * Cannot exceed 1500 characters.
   */
  @Size(max = 1500, message = "Description cannot be longer than 1500 characters")
  @Column(length = 1500)
  private String description;

  /** The due date and time for the assignment submission. */
  private LocalDateTime dueDate;

  /** Flag indicating whether a submission is required for the assignment. */
  private boolean requiresSubmission;

  /** The maximum grade achievable for the assignment. */
  private Double maxGrade;

  /** The ID of the project to which this assignment belongs. */
  private Long projectId;

  /** The current student's submission for the assignment, if any. */
  private AssignmentSubmissionDTO submission;

  /** All submissions for the assignment (used mainly for staff views). */
  private List<AssignmentSubmissionDTO> submissions = new ArrayList<>();

  /** The staff member assigned as the grader for the assignment. */
  private Staff grader;
}
