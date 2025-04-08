package az.edu.ada.SITE.Mapper;

import org.mapstruct.Mapper;

import az.edu.ada.SITE.DTO.DeliverableDTO;
import az.edu.ada.SITE.Entity.Deliverable;

@Mapper(componentModel = "spring")
public interface DeliverableMapper {
  DeliverableDTO deliverableToDeliverableDTO(Deliverable deliverable);

  Deliverable deliverableDTOtoDeliverable(DeliverableDTO deliverableDTO);
}