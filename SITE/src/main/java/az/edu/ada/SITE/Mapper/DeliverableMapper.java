package az.edu.ada.SITE.Mapper;

import org.mapstruct.Mapper;
import az.edu.ada.SITE.DTO.DeliverableDTO;
import az.edu.ada.SITE.Entity.Deliverable;

/**
 * Mapper interface for converting between {@link DeliverableDTO} and
 * {@link Deliverable} entities.
 * This interface uses MapStruct for automatic code generation.
 * The component model is set to "spring" for Spring integration.
 */
@Mapper(componentModel = "spring")
public interface DeliverableMapper {

  /**
   * Converts a {@link Deliverable} entity to a {@link DeliverableDTO}.
   * 
   * @param deliverable the {@link Deliverable} entity to convert
   * @return the corresponding {@link DeliverableDTO}
   */
  DeliverableDTO deliverableToDeliverableDTO(Deliverable deliverable);

  /**
   * Converts a {@link DeliverableDTO} to a {@link Deliverable} entity.
   * 
   * @param deliverableDTO the {@link DeliverableDTO} to convert
   * @return the corresponding {@link Deliverable} entity
   */
  Deliverable deliverableDTOtoDeliverable(DeliverableDTO deliverableDTO);
}
