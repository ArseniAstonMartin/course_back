package udemy.clone.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import udemy.clone.model.lesson.LessonCreateDto;
import udemy.clone.model.lesson.LessonDto;
import udemy.clone.model.user.UserDto;
import udemy.clone.model.util.LessonProgress;
import udemy.clone.service.LessonService;

import java.util.List;

@RestController
@RequestMapping("/lessons")
@RequiredArgsConstructor
public class LessonController {
    private final LessonService lessonService;

    @PostMapping
    public LessonDto createLesson(@RequestBody LessonCreateDto lessonDto) {
        return lessonService.createLesson(lessonDto);
    }

    @GetMapping("/students")
    public List<UserDto> findLessonsStudentsByProgress(
            @RequestParam(name = "lessonId") String lessonId,
            @RequestParam(name = "status") LessonProgress.LessonStatus status) {
        return lessonService.findLessonsStudentsByProgress(lessonId, status);
    }

    @GetMapping
    public List<LessonDto> findStudentsLessonsByProgress(
            @RequestParam(name = "userId") String userId,
            @RequestParam(name = "status") LessonProgress.LessonStatus status) {
        return lessonService.findStudentsLessonsByProgress(userId, status);
    }
}
