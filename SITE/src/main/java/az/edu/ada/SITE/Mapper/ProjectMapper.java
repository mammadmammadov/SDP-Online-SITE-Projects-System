package az.edu.ada.SITE.Mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.hibernate.Hibernate;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import az.edu.ada.SITE.DTO.ProjectDTO;
import az.edu.ada.SITE.Entity.Project;

@Mapper(componentModel = "spring", uses = { StudentMapper.class, DeliverableMapper.class })
public abstract class ProjectMapper {

  @Autowired
  protected DeliverableMapper deliverableMapper;

  @Mapping(target = "deliverables", ignore = true)
  public abstract ProjectDTO projectToProjectDTO(Project project);

  @AfterMapping
  protected void initializeDeliverables(Project project, @MappingTarget ProjectDTO projectDTO) {
    if (project != null) {
      Hibernate.initialize(project.getDeliverables());
      if (project.getDeliverables() != null) {
        projectDTO.setDeliverables(
            deliverableMapper.deliverableListToDeliverableDTOList(
                project.getDeliverables()));
      }
    }
  }

  public List<ProjectDTO> projectListToProjectDTOList(List<Project> projectList) {
    return projectList.stream()
        .map(project -> {
          ProjectDTO dto = projectToProjectDTO(project);
          Hibernate.initialize(project.getDeliverables());
          return dto;
        })
        .collect(Collectors.toList());
  }

  @Mapping(target = "deliverables", source = "deliverables")
  public abstract Project projectDTOtoProject(ProjectDTO projectDTO);
}
