package udemy.clone.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import udemy.clone.model.Invitation;
import udemy.clone.model.invitations.InvitationDto;

@Mapper(componentModel = "spring")
public interface InvitationMapper {

    Invitation toEntity(InvitationDto invitationDto);

    InvitationDto toDto(Invitation invitation);

}