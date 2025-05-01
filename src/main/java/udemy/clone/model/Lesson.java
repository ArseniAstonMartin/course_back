package udemy.clone.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import udemy.clone.model.util.ContentBlock;

import java.util.List;

@Data
@Document
@AllArgsConstructor
public class Lesson {
    @Id
    private String id;
    private String title;
    private List<ContentBlock> content;
}
