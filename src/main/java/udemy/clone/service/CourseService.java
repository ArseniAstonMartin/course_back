package udemy.clone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import udemy.clone.mapper.CourseMapper;
import udemy.clone.model.Course;
import udemy.clone.model.course.CourseCreateDto;
import udemy.clone.model.course.CourseDto;
import udemy.clone.repository.CourseRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    public CourseDto findCourseById(String id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Course not found by id: " + id));
        return courseMapper.toDto(course);
    }

    public CourseDto createCourse(CourseCreateDto courseDto) {
        var course = courseRepository.save(courseMapper.fromCreateDto(courseDto));
        return courseMapper.toDto(course);
    }

    public List<CourseDto> findAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(courseMapper::toDto)
                .toList();
    }
}
