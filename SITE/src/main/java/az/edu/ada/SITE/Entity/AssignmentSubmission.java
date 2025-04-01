package az.edu.ada.SITE.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "assignment_submissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentSubmission {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String fileName;

  @Column(nullable = false)
  private String filePath;

  private LocalDateTime submittedAt;
  private Double grade;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "assignment_id", nullable = false)
  private Assignment assignment;

  @ManyToOne
  @JoinColumn(name = "student_id")
  private Student student;

  @Column(length = 300)
  private String feedback;

  @ManyToOne
  @JoinColumn(name = "project_id")
  private Project project;

  @Column(name = "grade_viewed", nullable = false)
  private boolean gradeViewed = false;
}
