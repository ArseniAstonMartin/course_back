package udemy.clone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import udemy.clone.mapper.UserMapper;
import udemy.clone.model.Course;
import udemy.clone.model.user.UserDto;
import udemy.clone.model.util.ContentBlock;
import udemy.clone.model.util.LessonProgress;
import udemy.clone.repository.CourseRepository;
import udemy.clone.repository.LessonRepository;
import udemy.clone.mapper.LessonMapper;
import udemy.clone.model.Lesson;
import udemy.clone.model.lesson.LessonCreateDto;
import udemy.clone.model.lesson.LessonDto;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class LessonService {
    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final LessonMapper lessonMapper;
    private final UserMapper userMapper;
    private final ImageService imageService;

    public LessonDto createLesson(LessonCreateDto lessonDto, MultipartFile[] files) {
        int fileIndex = 0;
        Course course = courseRepository.findById(lessonDto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Cannot create lesson because course doesnt exist"));
        for (ContentBlock block : lessonDto.getContent()) {
            ContentBlock.ContentType type = block.getContentType();
            if (type == ContentBlock.ContentType.IMAGE || type == ContentBlock.ContentType.VIDEO) {
                MultipartFile file = files[fileIndex];
                String filename = imageService.upload(file);
                block.setContent(filename);
                fileIndex++;
            }
        }
        Lesson savedLesson = lessonRepository.save(lessonMapper.fromCreateDto(lessonDto));
        List<String> lessonIds = course.getLessonIds() != null
                ? course.getLessonIds()
                : new ArrayList<>();
        lessonIds.add(savedLesson.getId());
        course.setLessonIds(lessonIds);
        courseRepository.save(course);
        return lessonMapper.toDto(savedLesson);
    }

    public LessonDto findLessonById(String id) {
        Lesson lesson = lessonRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Course not found by id: " + id));
        return lessonMapper.toDto(lesson);
    }

    public List<LessonDto> findStudentsLessonsByProgress(String userId, LessonProgress.LessonStatus status) {
        return lessonRepository
                .findStudentsLessonsByProgress(userId, status.name())
                .stream()
                .map(lessonMapper::toDto)
                .toList();
    }

    public List<UserDto> findLessonsStudentsByProgress(String lessonId, LessonProgress.LessonStatus status) {
        return lessonRepository
                .findLessonsUsersByProgress(lessonId, status.name())
                .stream()
                .map(userMapper::toDto)
                .toList();
    }
}
