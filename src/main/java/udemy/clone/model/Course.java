package udemy.clone.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "courses")
public class Course {
    @Id
    private String id;
    private String title;
    private String description;
    private String teacherId;
    private List<String> lessonIds;
    private String filename;
}
