package az.edu.ada.SITE.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a project in the system.
 * Includes details such as project title, description, objectives, research
 * focus, and supervisor(s).
 */
@Entity
@Table(name = "projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    /**
     * The unique identifier of the project.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The title of the project.
     */
    @Column(nullable = false, length = 100)
    private String title;

    /**
     * A detailed description of the project.
     */
    @Column(length = 1000)
    private String description;

    /**
     * The list of objectives for the project.
     */
    @ElementCollection
    @Column(nullable = false, length = 500)
    private List<String> objectives;

    /**
     * The type of the project (e.g., individual, group).
     */
    @Enumerated(EnumType.STRING)
    private ProjectType type;

    /**
     * Keywords associated with the project.
     */
    private String keywords;

    /**
     * The list of research focus areas for the project.
     */
    @ElementCollection
    @NotEmpty(message = "At least one research focus must be selected")
    private List<String> researchFocus = new ArrayList<>();

    /**
     * The list of categories the project belongs to.
     */
    @ElementCollection
    @NotEmpty(message = "At least one category must be selected")
    private List<String> category = new ArrayList<>();

    /**
     * The list of study year restrictions for the project.
     */
    @ElementCollection
    @NotEmpty(message = "At least one study year restriction must be selected")
    private List<String> studyYearRestriction = new ArrayList<>();

    /**
     * The list of degree restrictions for the project (e.g., Bachelor's, Master's).
     */
    @ElementCollection
    @NotEmpty(message = "At least one degree restriction must be selected")
    private List<String> degreeRestriction = new ArrayList<>();

    /**
     * The list of major restrictions for the project.
     */
    @ElementCollection
    @NotEmpty(message = "At least one major restriction must be selected")
    private List<String> majorRestriction = new ArrayList<>();

    /**
     * Enum representing the type of project: either INDIVIDUAL or GROUP.
     */
    public enum ProjectType {
        INDIVIDUAL, GROUP
    }

    /**
     * The status of the project (e.g., OPEN, CLOSED).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.OPEN;

    /**
     * Enum representing the status of the project: either OPEN or CLOSED.
     */
    public enum Status {
        OPEN, CLOSED
    }

    /**
     * The maximum number of students that can be assigned to the project.
     */
    @Column
    private Integer maxStudents;

    /**
     * The staff member who is supervising the project.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff supervisor;

    /**
     * The list of staff members who are co-supervising the project.
     */
    @ManyToMany
    @JoinTable(name = "project_co_supervisors", joinColumns = @JoinColumn(name = "project_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<Staff> coSupervisors = new ArrayList<>();

    /**
     * The list of students assigned to the project.
     */
    @ManyToMany
    @JoinTable(name = "project_students", joinColumns = @JoinColumn(name = "project_id"), inverseJoinColumns = @JoinColumn(name = "student_id"))
    private List<Student> students = new ArrayList<>();

    /**
     * The list of students who have requested to join the project.
     */
    @ManyToMany
    @JoinTable(name = "project_requests", joinColumns = @JoinColumn(name = "project_id"), inverseJoinColumns = @JoinColumn(name = "student_id"))
    private List<Student> requestedStudents = new ArrayList<>();

    /**
     * The list of subcategories related to the project.
     */
    @ElementCollection
    @CollectionTable(name = "project_subcategories", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "subcategory")
    private List<String> subcategories = new ArrayList<>();

    /**
     * The list of assignments associated with the project.
     */
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Assignment> assignments = new ArrayList<>();

    /**
     * Enum representing the application status of the project: either PENDING or
     * ACCEPTED.
     */
    public enum ApplicationStatus {
        PENDING, ACCEPTED
    }

    /**
     * The application status of the project (e.g., PENDING, ACCEPTED).
     */
    @Column
    @Enumerated(EnumType.STRING)
    private ApplicationStatus appStatus = ApplicationStatus.PENDING;

    /**
     * Gets the application status for the project.
     *
     * @return the application status.
     */
    public ApplicationStatus getAppStatus() {
        return appStatus;
    }

    /**
     * Sets the application status for the project.
     *
     * @param appStatus the application status to set.
     */
    public void setAppStatus(ApplicationStatus appStatus) {
        this.appStatus = appStatus;
    }

    /**
     * Gets the list of student requests for the project.
     *
     * @return the list of student requests.
     */
    public List<Student> getStudentRequests() {
        return requestedStudents;
    }

    /**
     * Checks if the given student has made a request to join the project.
     *
     * @param student the student to check.
     * @return true if the student has made a request, false otherwise.
     */
    public boolean hasStudentRequest(Student student) {
        return requestedStudents.contains(student);
    }

    /**
     * Adds a student to the list of requested students for the project.
     *
     * @param student the student to add.
     */
    public void addStudent(Student student) {
        if (this.requestedStudents == null) {
            this.requestedStudents = new ArrayList<>();
        }
        this.requestedStudents.add(student);
        this.appStatus = ApplicationStatus.PENDING;
    }

    /**
     * Adds a student to the list of assigned students for the project and removes
     * them from the requested list.
     *
     * @param student the student to add.
     */
    public void addAcceptedStudent(Student student) {
        if (this.students == null)
            this.students = new ArrayList<>();
        this.students.add(student);
        student.setAccepted(true);
        this.requestedStudents.remove(student);
    }

    /**
     * Removes a student from both the assigned and requested students lists.
     *
     * @param student the student to remove.
     */
    public void removeStudent(Student student) {
        if (this.students != null) {
            this.students.remove(student);
        }
        if (this.requestedStudents != null) {
            this.requestedStudents.remove(student);
        }
    }

    /**
     * The list of deliverables associated with the project.
     */
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Deliverable> deliverables = new ArrayList<>();

    /**
     * Toggles the project status between OPEN and CLOSED.
     */
    public void toggleStatus() {
        this.status = (this.status == Status.OPEN) ? Status.CLOSED : Status.OPEN;
    }
}
