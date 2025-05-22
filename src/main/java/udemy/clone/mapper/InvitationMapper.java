package udemy.clone.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import udemy.clone.model.Invitation;
import udemy.clone.model.invitations.InvitationDto;

@Mapper(componentModel = "spring")
public interface InvitationMapper {
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "sender", ignore = true)
    @Mapping(target = "recipient", ignore = true)
    Invitation toEntity(InvitationDto invitationDto);

    @Mapping(source = "course.id", target = "courseId")
    @Mapping(source = "sender.id", target = "senderId")
    @Mapping(source = "recipient.id", target = "recipientId")
    InvitationDto toDto(Invitation invitation);

    @Mapping(target = "course", ignore = true)
    @Mapping(target = "sender", ignore = true)
    @Mapping(target = "recipient", ignore = true)
    void update(@MappingTarget Invitation invitation, InvitationDto invitationDto);
}
