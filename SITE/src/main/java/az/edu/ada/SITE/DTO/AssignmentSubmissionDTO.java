package az.edu.ada.SITE.DTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AssignmentSubmissionDTO {
  private Long id;
  private String fileName = "";
  private String filePath = "";
  private LocalDateTime submittedAt;
  private Double grade;
  private Long assignmentId;
  private String studentName;
  private String feedback;
  private Long studentId;
  private Long projectId;

}
