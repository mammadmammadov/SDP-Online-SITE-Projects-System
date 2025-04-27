package az.edu.ada.SITE.DTO;

import java.util.List;

import az.edu.ada.SITE.Entity.Project;
import az.edu.ada.SITE.Entity.Student;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Data Transfer Object (DTO) for transferring student-related information.
 * Extends {@link UserDTO} to inherit common user fields.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StudentDTO extends UserDTO {

    /** Unique student identifier (e.g., student number or registration ID). */
    private String studentId;

    /** Degree pursued by the student (e.g., Bachelor's, Master's). */
    private String degree;

    /**
     * Major or specialization area of the student (e.g., Computer Science,
     * Business).
     */
    private String major;

    /** Current study year of the student (e.g., 2nd Year, Final Year). */
    private String studyYear;

    /** Indicates whether the student has been accepted into a project. */
    private boolean accepted;

    /** List of projects the student is assigned or associated with. */
    private List<Project> projects;

    /**
     * Default no-argument constructor.
     */
    public StudentDTO() {
    }

    /**
     * All-arguments constructor for initializing a StudentDTO.
     *
     * @param studentId the student ID
     * @param degree    the degree pursued by the student
     * @param major     the major field of study
     * @param studyYear the study year
     * @param accepted  flag indicating acceptance status
     * @param projects  list of projects associated with the student
     */
    public StudentDTO(String studentId, String degree, String major, String studyYear, boolean accepted,
            List<Project> projects) {
        this.studentId = studentId;
        this.degree = degree;
        this.major = major;
        this.studyYear = studyYear;
        this.accepted = accepted;
        this.projects = projects;
    }

    /**
     * Converts this StudentDTO into a Student entity.
     *
     * @return the Student entity built from this DTO
     */
    public Student toStudent() {
        return new Student(studentId, degree, major, studyYear, accepted, projects);
    }
}
