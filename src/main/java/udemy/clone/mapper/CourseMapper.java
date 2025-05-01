package udemy.clone.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import udemy.clone.model.Course;
import udemy.clone.model.course.CourseCreateDto;
import udemy.clone.model.course.CourseDto;

import java.util.Optional;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CourseMapper {
    Course fromCreateDto(CourseCreateDto courseDto);

    CourseDto toDto(Course byId);
}
