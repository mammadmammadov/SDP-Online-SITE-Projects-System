package az.edu.ada.SITE.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import az.edu.ada.SITE.DTO.AssignmentDTO;
import az.edu.ada.SITE.Entity.Assignment;

@Mapper(componentModel = "spring")
public interface AssignmentMapper {
  AssignmentMapper INSTANCE = Mappers.getMapper(AssignmentMapper.class);

  @Mapping(target = "projectId", source = "project.id")
  AssignmentDTO assignmentToAssignmentDTO(Assignment assignment);

  @Mapping(target = "project", ignore = true)
  Assignment assignmentDTOtoAssignment(AssignmentDTO assignmentDTO);
}
