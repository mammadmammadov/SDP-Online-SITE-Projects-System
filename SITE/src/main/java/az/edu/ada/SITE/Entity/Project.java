package az.edu.ada.SITE.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, length = 500)
    private String objectives;

    @Enumerated(EnumType.STRING)
    private ProjectType type;

    private String category;

    private String keywords;

    public enum ProjectType {
        INDIVIDUAL, GROUP
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.OPEN;

    public enum Status {
        OPEN, CLOSED
    }

    @Column
    private Integer maxStudents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff supervisor;

    @ManyToMany
    @JoinTable(name = "project_students", joinColumns = @JoinColumn(name = "project_id"), inverseJoinColumns = @JoinColumn(name = "student_id"))
    private List<Student> students = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "project_requests", joinColumns = @JoinColumn(name = "project_id"), inverseJoinColumns = @JoinColumn(name = "student_id"))
    private List<Student> requestedStudents = new ArrayList<>();

    public enum ApplicationStatus {
        PENDING, ACCEPTED, REJECTED
    }

    @Column
    @Enumerated(EnumType.STRING)
    private ApplicationStatus appStatus = ApplicationStatus.PENDING;

    public ApplicationStatus getAppStatus() {
        return appStatus;
    }

    public void setAppStatus(ApplicationStatus appStatus) {
        this.appStatus = appStatus;
    }

    public List<Student> getStudentRequests() {
        return requestedStudents;

    }

    public boolean hasStudentRequest(Student student) {
        return requestedStudents.contains(student);
    }

    public void addStudent(Student student) {
        if (this.requestedStudents == null) {
            this.requestedStudents = new ArrayList<>();
        }
        this.requestedStudents.add(student);
        this.appStatus = ApplicationStatus.PENDING;
    }

    public void addAcceptedStudent(Student student) {
        if (this.students == null) {
            this.students = new ArrayList<>();
        }
        this.students.add(student);
        student.setAccepted(true);
        this.requestedStudents.remove(student);
        this.appStatus = ApplicationStatus.ACCEPTED;
    }

    public void removeStudent(Student student) {
        if (this.students != null) {
            this.students.remove(student);
        }
        if (this.requestedStudents != null) {
            this.requestedStudents.remove(student);
        }
    }

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Deliverable> deliverables = new ArrayList<>();

    public void toggleStatus() {
        this.status = (this.status == Status.OPEN) ? Status.CLOSED : Status.OPEN;
    }
}
