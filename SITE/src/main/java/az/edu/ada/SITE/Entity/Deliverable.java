package az.edu.ada.SITE.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing a deliverable associated with a project.
 * A deliverable typically refers to a document or file that must be submitted
 * as part of a project requirement.
 */
@Entity
@Table(name = "deliverables")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Deliverable {

    /** Unique identifier for the deliverable. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the deliverable.
     * This field is required and cannot be null.
     */
    @Column(nullable = false)
    private String name;

    /**
     * The file path where the deliverable is stored.
     * This field is required and cannot be null.
     */
    @Column(nullable = false)
    private String filePath;

    /**
     * The project to which this deliverable is associated.
     * A deliverable must be linked to a specific project.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
}
