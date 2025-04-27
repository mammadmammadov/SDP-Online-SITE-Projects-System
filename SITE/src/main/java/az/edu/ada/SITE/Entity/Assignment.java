package az.edu.ada.SITE.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entity class representing an Assignment within a project.
 * An assignment is associated with a project and can have a grader assigned to
 * it.
 */
@Entity
@Table(name = "assignments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Assignment {

  /** Unique identifier for the assignment. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The title of the assignment.
   * The title is required and cannot exceed 50 characters.
   */
  @NotBlank(message = "Title is required")
  @Size(max = 50, message = "Title cannot be longer than 50 characters")
  @Column(nullable = false, length = 50)
  private String title;

  /**
   * The description of the assignment.
   * The description cannot exceed 200 characters.
   */
  @Size(max = 200, message = "Description cannot be longer than 200 characters")
  @Column(length = 200)
  private String description;

  /** The due date for the assignment submission. */
  private LocalDateTime dueDate;

  /**
   * A flag indicating whether the assignment requires submission by students.
   * The field is required and cannot be null.
   */
  @Column(nullable = false)
  private boolean requiresSubmission;

  /** The maximum grade that can be achieved for the assignment. */
  private Double maxGrade;

  /**
   * The project associated with this assignment.
   * Each assignment must be linked to a project.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "project_id", nullable = false)
  private Project project;

  /**
   * The staff member responsible for grading the assignment.
   * This field may be null if no grader has been assigned.
   */
  @ManyToOne
  @JoinColumn(name = "grader_id")
  private Staff grader;
}
