package az.edu.ada.SITE.Mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import az.edu.ada.SITE.DTO.ProjectDTO;
import az.edu.ada.SITE.Entity.Project;

@Mapper(componentModel = "spring", uses = { StudentMapper.class, DeliverableMapper.class })
public interface ProjectMapper {
  @Mapping(target = "deliverables", source = "deliverables")
  ProjectDTO projectToProjectDTO(Project project);

  @Mapping(target = "deliverables", source = "deliverables")
  Project projectDTOtoProject(ProjectDTO projectDTO);

  default List<ProjectDTO> projectListToProjectDTOList(List<Project> projectList) {
    return projectList.stream()
        .map(this::projectToProjectDTO)
        .collect(Collectors.toList());
  }
}