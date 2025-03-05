package az.edu.ada.SITE.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class AssignmentDTO {
  private Long id;

  @NotBlank(message = "Title is required")
  @Size(max = 50, message = "Title cannot be longer than 50 characters")
  private String title;

  @Size(max = 200, message = "Description cannot be longer than 200 characters")
  private String description;

  private LocalDateTime dueDate;
  private boolean requiresSubmission;
  private Double maxGrade;
  private Long projectId;
  private AssignmentSubmissionDTO submission;
  private List<AssignmentSubmissionDTO> submissions = new ArrayList<>();

}
