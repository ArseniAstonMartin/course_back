package udemy.clone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import udemy.clone.mapper.UserMapper;
import udemy.clone.model.user.UserDto;
import udemy.clone.model.util.LessonProgress;
import udemy.clone.repository.LessonRepository;
import udemy.clone.mapper.LessonMapper;
import udemy.clone.model.Lesson;
import udemy.clone.model.lesson.LessonCreateDto;
import udemy.clone.model.lesson.LessonDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonService {
    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;
    private final UserMapper userMapper;

    public LessonDto createLesson(LessonCreateDto lessonDto) {
        Lesson lesson = lessonMapper.fromCreateDto(lessonDto);
        Lesson savedLesson = lessonRepository.save(lesson);
        return lessonMapper.toDto(savedLesson);
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
