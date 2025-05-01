package udemy.clone.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import udemy.clone.model.Lesson;
import udemy.clone.model.lesson.LessonCreateDto;
import udemy.clone.model.lesson.LessonDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LessonMapper {
    Lesson fromCreateDto(LessonCreateDto lessonDto);

    LessonDto toDto(Lesson savedLesson);
}
