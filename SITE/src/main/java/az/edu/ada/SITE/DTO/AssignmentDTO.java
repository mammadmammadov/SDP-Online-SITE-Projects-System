package az.edu.ada.SITE.DTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AssignmentDTO {
  private Long id;
  private String title;
  private String description;
  private LocalDateTime dueDate;
  private boolean requiresSubmission;
  private Double maxGrade;
  private Long projectId;
}
