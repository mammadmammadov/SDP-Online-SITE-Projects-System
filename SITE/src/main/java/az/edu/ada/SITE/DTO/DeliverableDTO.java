package az.edu.ada.SITE.DTO;

import az.edu.ada.SITE.Entity.Deliverable;
import az.edu.ada.SITE.Entity.Project;
import lombok.Data;

@Data
public class DeliverableDTO {
  private Long id;
  private String name;
  private String filePath;
  private Project project;

  public DeliverableDTO() {

  }

  public DeliverableDTO(Long id, String name, String filePath, Project project) {
    this.id = id;
    this.name = name;
    this.filePath = filePath;
    this.project = project;
  }

  public Deliverable toDeliverable() {
    return new Deliverable(id, name, filePath, project);
  }
}
