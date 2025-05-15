package udemy.clone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import udemy.clone.model.User;
import udemy.clone.repository.LessonRepository;

@Service
@RequiredArgsConstructor
public class SecurityService {
    private final LessonRepository lessonRepository;

    public boolean canAccessLesson(String lessonId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return lessonRepository.findById(lessonId)
                .map(lesson -> user.getCourseIds().contains(lesson.getCourseId()))
                .orElse(false);
    }
} 