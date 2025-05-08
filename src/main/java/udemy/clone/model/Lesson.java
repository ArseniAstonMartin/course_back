package udemy.clone.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import udemy.clone.model.util.ContentBlock;

import java.util.List;

@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class Lesson {
    @Id
    private String id;
    private String courseId;
    private String title;
    private List<ContentBlock> content;
    private int order;
}
