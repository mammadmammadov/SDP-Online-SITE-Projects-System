package az.edu.ada.SITE.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "assignments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Assignment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Title is required")
  @Size(max = 50, message = "Title cannot be longer than 50 characters")
  @Column(nullable = false, length = 50)
  private String title;

  @Size(max = 200, message = "Description cannot be longer than 200 characters")
  @Column(length = 200)
  private String description;

  private LocalDateTime dueDate;

  @Column(nullable = false)
  private boolean requiresSubmission;

  private Double maxGrade;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "project_id", nullable = false)
  private Project project;

  @ManyToOne
  @JoinColumn(name = "grader_id")
  private Staff grader;
}
