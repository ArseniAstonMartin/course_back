package udemy.clone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import udemy.clone.model.Course;
import udemy.clone.model.User;
import udemy.clone.model.event.AcceptInvitationEvent;
import udemy.clone.model.util.LessonProgress;
import udemy.clone.repository.LessonProgressRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonProgressService {

    private final UserService userService;
    private final CourseService courseService;
    private final LessonProgressRepository lessonProgressRepository;

    public LessonProgress getLessonProgress(String lessonProgressId) {
        return lessonProgressRepository.findById(lessonProgressId)
                .orElseThrow(() -> new IllegalArgumentException("LessonProgress not found"));
    }

    public void addLessonProgress(AcceptInvitationEvent event) {
        Course course = courseService.findCourseById(event.getCourseId());
        List<LessonProgress> progressList = course.getLessonIds().stream()
                .map(id -> new LessonProgress(null, event.getInvitedId(), id, LessonProgress.Status.NOT_STARTED))
                .peek(lessonProgressRepository::save)
                .toList();
        User invitedUser = userService.findUserById(event.getInvitedId());
        for (LessonProgress progress : progressList) {
            invitedUser.getLessonProgress().add(progress);
        }
    }
}