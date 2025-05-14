package udemy.clone.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import udemy.clone.model.lesson.LessonCreateDto;
import udemy.clone.model.lesson.LessonDto;
import udemy.clone.model.user.UserDto;
import udemy.clone.model.util.LessonProgress;
import udemy.clone.service.LessonService;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/lessons")
@RequiredArgsConstructor
public class LessonController {
    private final LessonService lessonService;

    @PostMapping
    public LessonDto createLesson(@RequestPart("lessonDto") LessonCreateDto lessonDto,
                                  @RequestParam(value = "files", required = false) MultipartFile[] files) {
        return lessonService.createLesson(lessonDto, files);
    }

    @GetMapping("/{id}")
    public LessonDto getLesson(@PathVariable String id) {
        return lessonService.findLessonById(id);
    }

    @GetMapping("/students")
    public List<UserDto> findLessonsStudentsByProgress(
            @RequestParam(name = "lessonId") String lessonId,
            @RequestParam(name = "status") LessonProgress.Status status) {
        return lessonService.findLessonsStudentsByProgress(lessonId, status);
    }

    @GetMapping
    public List<LessonDto> findStudentsLessonsByProgress(
            @RequestParam(name = "userId") String userId,
            @RequestParam(name = "status") LessonProgress.Status status) {
        return lessonService.findStudentsLessonsByProgress(userId, status);
    }
}
