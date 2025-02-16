package az.edu.ada.SITE.Mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import az.edu.ada.SITE.DTO.ProjectDTO;
import az.edu.ada.SITE.Entity.Project;

@Mapper(componentModel = "spring", uses = { StudentMapper.class })
public interface ProjectMapper {
  ProjectMapper INSTANCE = Mappers.getMapper(ProjectMapper.class);

  @Mapping(source = "id", target = "id")
  ProjectDTO projectToProjectDTO(Project project);

  @Mapping(source = "id", target = "id")
  Project projectDTOtoProject(ProjectDTO projctDTO);

  default List<ProjectDTO> projectListToProjectDTOList(List<Project> projectList) {
    return projectList.stream()
        .map(this::projectToProjectDTO)
        .collect(Collectors.toList());
  }
}
