package udemy.clone.model.elastic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import udemy.clone.model.User;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDocument {
    private String id;
    private String name;
    private String email;
    private User.Role role;
    private List<String> courseIds;
}
