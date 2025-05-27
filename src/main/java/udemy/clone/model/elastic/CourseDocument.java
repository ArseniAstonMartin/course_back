package udemy.clone.model.elastic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDocument {
    private String id;
    private String title;
    private String description;
    private String teacherId;
    private String filename;
}
