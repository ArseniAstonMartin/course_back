package udemy.clone.model.lesson;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonListDto {
    private String id;
    private int order;
    private String title;
}
