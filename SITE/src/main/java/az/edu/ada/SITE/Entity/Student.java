package az.edu.ada.SITE.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class Student extends User {
    private String degree;

    private String academicYear;

    private String major;

    @ManyToOne
    private Project project;
}