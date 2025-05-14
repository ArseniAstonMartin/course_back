package udemy.clone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import udemy.clone.mapper.CourseMapper;
import udemy.clone.mapper.LessonMapper;
import udemy.clone.model.Course;
import udemy.clone.model.Lesson;
import udemy.clone.model.course.CourseCreateDto;
import udemy.clone.model.course.CourseDto;
import udemy.clone.model.lesson.LessonListDto;
import udemy.clone.repository.CourseRepository;
import udemy.clone.repository.LessonRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final CourseMapper courseMapper;
    private final LessonMapper lessonMapper;

    public Course findCourseById(String id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Course not found by id: " + id));
    }

    public CourseDto findCourseDtoById(String id) {
        Course course = findCourseById(id);
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

    public List<LessonListDto> findCourseLessonsById(String id) {
        List<String> lessonIds = findCourseById(id).getLessonIds() != null
                ? findCourseById(id).getLessonIds()
                : new ArrayList<>();
        List<Lesson> lessons = lessonIds
                .stream()
                .map(lessonId -> lessonRepository.findById(lessonId).orElseThrow())
                .toList();
        return lessonMapper.toListDtoList(lessons);
    }
}
