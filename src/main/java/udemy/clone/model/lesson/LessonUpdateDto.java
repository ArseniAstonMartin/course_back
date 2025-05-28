package udemy.clone.model.lesson;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import udemy.clone.model.util.ContentBlock;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonUpdateDto {
    private String id;
    private String title;
    private List<ContentBlock> content;
}
