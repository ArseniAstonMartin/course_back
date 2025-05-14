package udemy.clone.model.lesson;

import lombok.Data;
import udemy.clone.model.util.ContentBlock;

import java.util.List;

@Data
public class LessonCreateDto {
    private String courseId;
    private String title;
    private List<ContentBlock> content;
    private int lessonOrder;
}
