package udemy.clone.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import udemy.clone.model.User;
import udemy.clone.model.authentication.JwtResponseDto;

@Mapper(componentModel = "spring")
public interface JwtMapper {
    JwtMapper INSTANCE = Mappers.getMapper(JwtMapper.class);
    default JwtResponseDto toDtoCustom(String token, User user) {
        return JwtResponseDto.builder()
                .token(token)
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
