package udemy.clone.model.lesson;

import lombok.Data;
import udemy.clone.model.util.ContentBlock;

import java.util.List;

@Data
public class LessonDto {
    private String id;
    private String title;
    private List<ContentBlock> content;
}
