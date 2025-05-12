package udemy.clone.model.authentication;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtResponseDto {
    private final String type = "Bearer";
    private String token;
    private String name;
    private String email;
}