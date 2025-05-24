package udemy.clone.model.authentication;

import lombok.Builder;
import lombok.Data;
import udemy.clone.model.User;

@Data
@Builder
public class JwtResponseDto {
    private final String type = "Bearer";
    private String token;
    private String name;
    private User.Role role;
    private String email;
}