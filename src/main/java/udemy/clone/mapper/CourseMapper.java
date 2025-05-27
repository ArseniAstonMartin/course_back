package udemy.clone.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import udemy.clone.model.Course;
import udemy.clone.model.User;
import udemy.clone.model.course.CourseCreateDto;
import udemy.clone.model.course.CourseDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CourseMapper {
    @Mapping(target = "teacherId", expression = "java(getCurrentUserId())")
    Course fromCreateDto(CourseCreateDto courseDto);

    CourseDto toDto(Course byId);

    default String getCurrentUserId() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getId();
    }
}
