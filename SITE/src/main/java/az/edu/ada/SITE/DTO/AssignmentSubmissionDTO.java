package az.edu.ada.SITE.DTO;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) for representing a student's assignment
 * submission.
 * Contains details about the submitted file, grading, and feedback.
 */
@Data
public class AssignmentSubmissionDTO {

  /** The unique identifier of the assignment submission. */
  private Long id;

  /** The name of the submitted file. */
  private String fileName = "";

  /** The file system path where the submitted file is stored. */
  private String filePath = "";

  /** The date and time when the submission was made. */
  private LocalDateTime submittedAt;

  /** The grade awarded for the submission. */
  private Double grade;

  /** The ID of the assignment related to this submission. */
  private Long assignmentId;

  /** The name of the student who submitted the assignment. */
  private String studentName;

  /** Feedback provided by the grader for the submission. */
  private String feedback;

  /** The ID of the student who made the submission. */
  private Long studentId;

  /** The ID of the project to which this submission belongs. */
  private Long projectId;
}
