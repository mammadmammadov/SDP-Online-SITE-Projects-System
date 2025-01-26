package az.edu.ada.SITE.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Student extends User {
    private String degree; // Degree type (e.g., Bachelor, Master)

    private String academicYear; // Academic year (e.g., 2023-2024)

    private String major; // Major or field of study (e.g., Computer Science, Software Engineering)

    @ManyToOne
    private Project project; // A student can be assigned to only one project
}
