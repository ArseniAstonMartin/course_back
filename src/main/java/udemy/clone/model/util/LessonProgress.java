package udemy.clone.model.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "lessonProgress")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonProgress {
    @Id
    private String lessonProgressId;
    private String studentId;
    private String lessonId;
    private String studentId;
    private Status status;

    public enum Status {
        NOT_STARTED,
        IN_PROGRESS,
        COMPLETED
    }
}
