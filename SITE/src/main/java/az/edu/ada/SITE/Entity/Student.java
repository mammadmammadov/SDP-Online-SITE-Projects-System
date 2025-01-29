package az.edu.ada.SITE.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Student extends User {

    @Column(nullable = false, unique = true)
    private String studentId;  // Unique Student Identifier

    @Column(nullable = false)
    private String degree;  // Graduate or Undergraduate

    @Column(nullable = false)
    private String major;  // Example: Computer Science, Business

    @Column(nullable = false)
    private int studyYear;  // Example: 1st Year, 2nd Year, etc.

    @ManyToMany
    @JoinTable(
            name = "student_projects",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "project_id")
    )
    private List<Project> projects = new ArrayList<>();

    public Student(String email, String password, String studentId, String degree, String major, int studyYear) {
        super(email, password, Role.STUDENT);
        this.studentId = studentId;
        this.degree = degree;
        this.major = major;
        this.studyYear = studyYear;
    }
}
