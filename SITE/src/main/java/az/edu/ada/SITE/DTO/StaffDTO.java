package az.edu.ada.SITE.DTO;

import java.util.ArrayList;
import java.util.List;

import az.edu.ada.SITE.Entity.Project;
import az.edu.ada.SITE.Entity.Staff;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class StaffDTO extends UserDTO {
    private String staffId;
    private String position;
    private String department;
    private List<Project> projects = new ArrayList<>();

    public StaffDTO() {

    }

    public StaffDTO(String staffId, String position, String department, List<Project> projects) {
        this.staffId = staffId;
        this.position = position;
        this.department = department;
        this.projects = projects;
    }

    public Staff toStaff() {
        return new Staff(staffId, position, department, projects);
    }

}
