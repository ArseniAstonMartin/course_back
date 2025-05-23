package udemy.clone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import udemy.clone.mapper.UserMapper;
import udemy.clone.model.Course;
import udemy.clone.model.User;
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
    private final SecurityService securityService;

    @PreAuthorize("hasRole('TEACHER') && principal.courseIds.contains(#lessonDto.getCourseId())")
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

    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT') && @securityService.canAccessLesson(#lessonId)") 
    public LessonDto findLessonById(String lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NoSuchElementException("Lesson not found by id: " + lessonId));
        return lessonMapper.toDto(lesson);
    }

    @PreAuthorize("principal.id == #userId")
    public List<LessonDto> findStudentsLessonsByProgress(String userId, LessonProgress.Status status) {
        return lessonRepository
                .findStudentsLessonsByProgress(userId, status.name())
                .stream()
                .map(lessonMapper::toDto)
                .toList();
    }

    @PreAuthorize("hasRole('TEACHER') && @securityService.canAccessLesson(#lessonId)")
    public List<UserDto> findLessonsStudentsByProgress(String lessonId, LessonProgress.Status status) {
        List<User> users = lessonRepository.findLessonsUsersByProgress(lessonId, status.toString());
        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }

    public List<LessonProgress> findLessonsProgressByLCourseId(String lessonId) {

    }
}
