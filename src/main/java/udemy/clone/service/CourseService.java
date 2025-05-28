package udemy.clone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import udemy.clone.mapper.CourseMapper;
import udemy.clone.mapper.LessonMapper;
import udemy.clone.model.Course;
import udemy.clone.model.Lesson;
import udemy.clone.model.User;
import udemy.clone.model.course.CourseCreateDto;
import udemy.clone.model.course.CourseDto;
import udemy.clone.model.course.CourseUpdateDto;
import udemy.clone.model.lesson.LessonListDto;
import udemy.clone.repository.CourseRepository;
import udemy.clone.repository.LessonRepository;
import udemy.clone.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final CourseMapper courseMapper;
    private final LessonMapper lessonMapper;
    private final ImageService imageService;

    public Course findCourseById(String id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Course not found by id: " + id));
    }

    public CourseDto findCourseDtoById(String id) {
        return courseMapper.toDto(findCourseById(id));
    }

    @PreAuthorize("hasRole('TEACHER')")
    public CourseDto createCourse(CourseCreateDto courseDto, MultipartFile courseImage) {
        Course course = courseMapper.fromCreateDto(courseDto);
        if (courseImage != null) {
            course.setFilename(imageService.upload(courseImage));
        }
        Course savedCourse = courseRepository.save(course);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var courses = user.getCourseIds();
        courses = courses != null ? courses : new ArrayList<>();
        courses.add(savedCourse.getId());
        user.setCourseIds(courses);
        userRepository.save(user);
        return courseMapper.toDto(savedCourse);
    }

    public List<CourseDto> findAllCourses() {
        return courseRepository.findAll().stream()
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

    public CourseDto uploadCourseImage(String id, MultipartFile image) {
        Course course = findCourseById(id);
        if (course.getFilename() != null) {
            imageService.deleteImage(course.getFilename());
        }
        course.setFilename(imageService.upload(image));
        Course savedCourse = courseRepository.save(course);
        return courseMapper.toDto(savedCourse);
    }

    public List<CourseDto> findMyCourses() {
        String userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return courseRepository.findByTeacherId(userId).stream().map(courseMapper::toDto).toList();
    }

    public CourseDto update(CourseUpdateDto courseDto) {
        Course course = findCourseById(courseDto.getId());
        courseMapper.update(course, courseDto);
        return courseMapper.toDto(courseRepository.save(course));
    }
}
