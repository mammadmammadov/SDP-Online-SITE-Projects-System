package az.edu.ada.SITE.DTO;

import az.edu.ada.SITE.Entity.Deliverable;
import az.edu.ada.SITE.Entity.Project;
import lombok.Data;

/**
 * Data Transfer Object (DTO) for representing a deliverable related to a
 * project.
 * Used for transferring deliverable data between the client and server.
 */
@Data
public class DeliverableDTO {

    /** The unique identifier of the deliverable. */
    private Long id;

    /** The name of the deliverable. */
    private String name;

    /** The file system path where the deliverable is stored. */
    private String filePath;

    /** The project associated with this deliverable. */
    private Project project;

    /**
     * Default no-args constructor.
     */
    public DeliverableDTO() {
    }

    /**
     * All-args constructor to initialize a deliverable DTO.
     *
     * @param id       the ID of the deliverable
     * @param name     the name of the deliverable
     * @param filePath the file path of the deliverable
     * @param project  the associated project
     */
    public DeliverableDTO(Long id, String name, String filePath, Project project) {
        this.id = id;
        this.name = name;
        this.filePath = filePath;
        this.project = project;
    }

    /**
     * Converts this DTO into a {@link Deliverable} entity.
     *
     * @return a new Deliverable entity populated with data from this DTO
     */
    public Deliverable toDeliverable() {
        return new Deliverable(id, name, filePath, project);
    }
}
