package az.edu.ada.SITE.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a student in the system. A student has a unique student
 * ID, degree, major, study year,
 * and can be part of multiple projects. The student can also be accepted into a
 * project after the supervisor's approval.
 */
@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Student extends User {

    /** Unique identifier for the student. */
    @Column(nullable = false, unique = true)
    private String studentId;

    /**
     * The degree program the student is enrolled in (e.g., Bachelor's, Master's).
     */
    @Column(nullable = false)
    private String degree;

    /** The major or field of study for the student. */
    @Column(nullable = false)
    private String major;

    /** The year of study for the student (e.g., 1st year, 2nd year). */
    @Column(nullable = false)
    private String studyYear;

    /** Indicates if the student has been accepted into a project. */
    @Column(nullable = false)
    private boolean accepted = false;

    /**
     * List of projects that the student is associated with.
     * This establishes a many-to-many relationship between students and projects.
     */
    @ManyToMany(mappedBy = "students")
    private List<Project> projects = new ArrayList<>();

    /**
     * Constructor to initialize a student with specific details.
     * 
     * @param studentId The unique ID of the student.
     * @param name      The name of the student.
     * @param surname   The surname of the student.
     * @param email     The email address of the student.
     * @param password  The password for the student.
     * @param degree    The degree program of the student.
     * @param major     The student's field of study.
     * @param studyYear The year of study the student is in.
     */
    public Student(String studentId, String name, String surname, String email, String password,
            String degree, String major, String studyYear) {
        super(name, surname, email, password, Role.STUDENT);
        this.studentId = studentId;
        this.degree = degree;
        this.major = major;
        this.studyYear = studyYear;
    }
}
