package udemy.clone.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import udemy.clone.model.util.LessonProgress;

import java.util.List;

@Document(collection = "users")
@Data
@Builder
@AllArgsConstructor
public class User {
    @Id
    private String id;
    private String name;
    private String email;
    private Role role;
    private List<String> courseIds; // for student - means bought, for teacher - means created
    private List<LessonProgress> lessonProgress; // student only
    private String password;

    public enum Role {
        TEACHER, STUDENT
    }
}