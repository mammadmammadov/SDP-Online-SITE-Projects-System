package az.edu.ada.SITE.DTO;

import java.util.List;

import az.edu.ada.SITE.Entity.Project;
import az.edu.ada.SITE.Entity.Student;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class StudentDTO extends UserDTO {
    private String studentId;
    private String degree;
    private String major;
    private String studyYear;
    private boolean accepted;
    List<Project> projects;

    public StudentDTO() {
    }

    public StudentDTO(String studentId, String degree, String major, String studyYear, boolean accepted,
                      List<Project> projects) {
        this.studentId = studentId;
        this.degree = degree;
        this.major = major;
        this.studyYear = studyYear;
        this.accepted = accepted;
        this.projects = projects;
    }

    public Student toStudent() {
        return new Student(studentId, degree, major, studyYear, accepted, projects);
    }
}
