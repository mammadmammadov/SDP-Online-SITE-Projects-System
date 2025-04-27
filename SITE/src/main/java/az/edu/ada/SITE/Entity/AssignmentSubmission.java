package az.edu.ada.SITE.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entity class representing an assignment submission made by a student.
 * This entity holds information about the file submitted by the student,
 * the grade, feedback, and its association with an assignment and a project.
 */
@Entity
@Table(name = "assignment_submissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentSubmission {

  /** Unique identifier for the assignment submission. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The name of the file submitted by the student.
   * The file name is required and cannot be null.
   */
  @Column(nullable = false)
  private String fileName;

  /**
   * The file path where the submitted file is stored.
   * The file path is required and cannot be null.
   */
  @Column(nullable = false)
  private String filePath;

  /** The timestamp when the assignment was submitted. */
  private LocalDateTime submittedAt;

  /**
   * The grade received for this assignment submission.
   * This can be null if the assignment has not yet been graded.
   */
  private Double grade;

  /**
   * The assignment to which this submission is associated.
   * Each submission is linked to one specific assignment.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "assignment_id", nullable = false)
  private Assignment assignment;

  /**
   * The student who made this submission.
   * Each submission is linked to a single student.
   */
  @ManyToOne
  @JoinColumn(name = "student_id")
  private Student student;

  /**
   * Feedback given by the grader for the submission.
   * The feedback can be up to 300 characters long.
   */
  @Column(length = 300)
  private String feedback;

  /**
   * The project associated with the assignment submission.
   * This links the submission to a specific project.
   */
  @ManyToOne
  @JoinColumn(name = "project_id")
  private Project project;
}
