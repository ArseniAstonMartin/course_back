package udemy.clone.model.util;

public class LessonProgress {
    private String lessonId;
    private LessonStatus status;

    public enum LessonStatus {
        TO_STUDY, IN_PROGRESS, FINISHED
    }
}
