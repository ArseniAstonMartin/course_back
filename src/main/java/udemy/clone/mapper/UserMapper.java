package udemy.clone.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import udemy.clone.model.User;
import udemy.clone.model.user.UserDto;
import udemy.clone.model.user.UserUpdateDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserDto toDto(User user);
    void update(@MappingTarget User user, UserUpdateDto userUpdateDto);
}
