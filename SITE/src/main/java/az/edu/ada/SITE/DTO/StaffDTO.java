package az.edu.ada.SITE.DTO;

import java.util.ArrayList;
import java.util.List;

import az.edu.ada.SITE.Entity.Project;
import az.edu.ada.SITE.Entity.Staff;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Data Transfer Object (DTO) for representing staff data.
 * Extends {@link UserDTO} to inherit common user properties.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StaffDTO extends UserDTO {

    /** Unique identifier for the staff member. */
    private String staffId;

    /**
     * The position or title held by the staff member (e.g., Professor, Research
     * Assistant).
     */
    private String position;

    /** The department to which the staff member belongs. */
    private String department;

    /** List of projects supervised or participated in by the staff member. */
    private List<Project> projects = new ArrayList<>();

    /**
     * Default no-argument constructor.
     */
    public StaffDTO() {
    }

    /**
     * All-arguments constructor to initialize a StaffDTO with given values.
     *
     * @param staffId    the unique ID of the staff member
     * @param position   the position or title of the staff member
     * @param department the department name
     * @param projects   the list of projects associated with the staff member
     */
    public StaffDTO(String staffId, String position, String department, List<Project> projects) {
        this.staffId = staffId;
        this.position = position;
        this.department = department;
        this.projects = projects;
    }

    /**
     * Converts this StaffDTO instance into a {@link Staff} entity object.
     *
     * @return the {@link Staff} entity
     */
    public Staff toStaff() {
        return new Staff(staffId, position, department, projects);
    }
}
