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
    private int studyYear;

    @ManyToMany
    @JoinTable(name = "student_projects", joinColumns = @JoinColumn(name = "student_id"), inverseJoinColumns = @JoinColumn(name = "project_id"))
    private List<Project> projects = new ArrayList<>();

    public Student(String studentId, String name, String surname, String email, String password,
                   String degree,
                   String major, int studyYear) {
        super(name, surname, email, password, Role.STUDENT);
        this.studentId = studentId;
        this.degree = degree;
        this.major = major;
        this.studyYear = studyYear;
    }
}
