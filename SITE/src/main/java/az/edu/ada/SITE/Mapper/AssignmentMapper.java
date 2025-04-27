package az.edu.ada.SITE.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import az.edu.ada.SITE.DTO.AssignmentDTO;
import az.edu.ada.SITE.Entity.Assignment;

/**
 * Mapper interface for mapping between {@link Assignment} entity and
 * {@link AssignmentDTO}.
 * This interface uses MapStruct for automatic generation of the mapping code.
 * The component model is set to "spring" for Spring integration.
 */
@Mapper(componentModel = "spring")
public interface AssignmentMapper {

  /**
   * Instance of the AssignmentMapper for manual usage if needed.
   * MapStruct will generate this instance.
   */
  AssignmentMapper INSTANCE = Mappers.getMapper(AssignmentMapper.class);

  /**
   * Converts an {@link Assignment} entity to an {@link AssignmentDTO}.
   * 
   * @param assignment the {@link Assignment} entity to be converted
   * @return the corresponding {@link AssignmentDTO}
   */
  @Mapping(target = "projectId", source = "project.id")
  AssignmentDTO assignmentToAssignmentDTO(Assignment assignment);

  /**
   * Converts an {@link AssignmentDTO} to an {@link Assignment} entity.
   * 
   * @param assignmentDTO the {@link AssignmentDTO} to be converted
   * @return the corresponding {@link Assignment} entity
   */
  @Mapping(target = "project", ignore = true)
  Assignment assignmentDTOtoAssignment(AssignmentDTO assignmentDTO);
}
