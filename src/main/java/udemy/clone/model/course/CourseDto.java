package udemy.clone.model.course;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CourseDto {
    private String id;
    private String title;
    private String description;
    private String filename;
}
