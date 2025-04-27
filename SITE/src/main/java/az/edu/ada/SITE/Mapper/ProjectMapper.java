package az.edu.ada.SITE.Mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import az.edu.ada.SITE.DTO.ProjectDTO;
import az.edu.ada.SITE.Entity.Project;

/**
 * Mapper interface for converting between {@link ProjectDTO} and
 * {@link Project} entities.
 * This interface uses MapStruct for automatic code generation.
 * The component model is set to "spring" for Spring integration. It also
 * utilizes
 * other mappers such as {@link StudentMapper} and {@link DeliverableMapper} to
 * handle nested entities.
 */
@Mapper(componentModel = "spring", uses = { StudentMapper.class, DeliverableMapper.class })
public interface ProjectMapper {

  /**
   * Converts a {@link Project} entity to a {@link ProjectDTO}.
   * 
   * @param project the {@link Project} entity to convert
   * @return the corresponding {@link ProjectDTO}
   */
  @Mapping(target = "deliverables", source = "deliverables")
  ProjectDTO projectToProjectDTO(Project project);

  /**
   * Converts a {@link ProjectDTO} to a {@link Project} entity.
   * 
   * @param projectDTO the {@link ProjectDTO} to convert
   * @return the corresponding {@link Project} entity
   */
  @Mapping(target = "deliverables", source = "deliverables")
  Project projectDTOtoProject(ProjectDTO projectDTO);

  /**
   * Converts a list of {@link Project} entities to a list of {@link ProjectDTO}
   * objects.
   * 
   * @param projectList the list of {@link Project} entities to convert
   * @return the corresponding list of {@link ProjectDTO} objects
   */
  default List<ProjectDTO> projectListToProjectDTOList(List<Project> projectList) {
    return projectList.stream()
        .map(this::projectToProjectDTO)
        .collect(Collectors.toList());
  }
}
