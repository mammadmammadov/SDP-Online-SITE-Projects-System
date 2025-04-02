package az.edu.ada.SITE.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;

@Entity
@Table(name = "projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100, unique = true)
    private String title;

    @Column(length = 1000)
    private String description;

    @ElementCollection(fetch = FetchType.LAZY)
    @Column(nullable = false, length = 500)
    private List<String> objectives;

    @Enumerated(EnumType.STRING)
    private ProjectType type;

    private String keywords;

    @ElementCollection
    @NotEmpty(message = "At least one research focus must be selected")
    @CollectionTable(name = "project_research_focus", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "research_focus")
    private List<String> researchFocus = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @NotEmpty(message = "At least one category must be selected")
    @CollectionTable(name = "project_categories", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "category")
    private List<String> category = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @NotEmpty(message = "At least one study year restriction must be selected")
    @CollectionTable(name = "project_study_year_restrictions", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "study_year")
    private List<String> studyYearRestriction = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @NotEmpty(message = "At least one degree restriction must be selected")
    @CollectionTable(name = "project_degree_restrictions", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "degree")
    private List<String> degreeRestriction = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @NotEmpty(message = "At least one major restriction must be selected")
    @CollectionTable(name = "project_major_restrictions", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "major")
    private List<String> majorRestriction = new ArrayList<>();

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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "project_co_supervisors", joinColumns = @JoinColumn(name = "project_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<Staff> coSupervisors = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "project_students", joinColumns = @JoinColumn(name = "project_id"), inverseJoinColumns = @JoinColumn(name = "student_id"))
    private List<Student> students = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "project_requests", joinColumns = @JoinColumn(name = "project_id"), inverseJoinColumns = @JoinColumn(name = "student_id"))
    private List<Student> requestedStudents = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "project_subcategories", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "subcategory")
    private List<String> subcategories = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Assignment> assignments = new ArrayList<>();

    public enum ApplicationStatus {
        PENDING, ACCEPTED
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
        if (this.students == null)
            this.students = new ArrayList<>();
        this.students.add(student);
        student.setAccepted(true);
        this.requestedStudents.remove(student);
    }

    public void removeStudent(Student student) {
        if (this.students != null) {
            this.students.remove(student);
        }
        if (this.requestedStudents != null) {
            this.requestedStudents.remove(student);
        }
    }

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Deliverable> deliverables = new ArrayList<>();

    public void toggleStatus() {
        this.status = (this.status == Status.OPEN) ? Status.CLOSED : Status.OPEN;
    }

    public void initializeAllCollections() {
        Hibernate.initialize(this.objectives);
        Hibernate.initialize(this.researchFocus);
        Hibernate.initialize(this.category);
        Hibernate.initialize(this.studyYearRestriction);
        Hibernate.initialize(this.degreeRestriction);
        Hibernate.initialize(this.majorRestriction);
        Hibernate.initialize(this.subcategories);
        Hibernate.initialize(this.assignments);
        Hibernate.initialize(this.deliverables);
        Hibernate.initialize(this.coSupervisors);
        Hibernate.initialize(this.students);
        Hibernate.initialize(this.requestedStudents);
    }
}
