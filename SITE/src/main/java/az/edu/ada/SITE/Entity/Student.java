package az.edu.ada.SITE.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
    private String studentId;

    @Column(nullable = false)
    private String degree;

    @Column(nullable = false)
    private String major;

    @Column(nullable = false)
    private String studyYear;

    @Column(nullable = false)
    private boolean accepted = false;

    @ManyToMany(mappedBy = "students")
    private List<Project> projects = new ArrayList<>();

    public Student(String studentId, String name, String surname, String email, String password,
            String degree,
            String major, String studyYear) {
        super(name, surname, email, password, Role.STUDENT);
        this.studentId = studentId;
        this.degree = degree;
        this.major = major;
        this.studyYear = studyYear;
    }
}
