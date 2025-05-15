package udemy.clone.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import udemy.clone.model.Lesson;
import udemy.clone.model.lesson.LessonCreateDto;
import udemy.clone.model.lesson.LessonDto;
import udemy.clone.model.lesson.LessonListDto;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LessonMapper {
    Lesson fromCreateDto(LessonCreateDto lessonDto);

    LessonDto toDto(Lesson lesson);

    LessonListDto toListDto(Lesson lesson);

    List<LessonListDto> toListDtoList(List<Lesson> lessons);
}
