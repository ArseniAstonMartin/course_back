package udemy.clone.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import udemy.clone.model.course.CourseCreateDto;
import udemy.clone.model.course.CourseDto;
import udemy.clone.model.lesson.LessonListDto;
import udemy.clone.service.CourseService;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/courses")
public class CourseController {
    private final CourseService courseService;

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<CourseDto> findAllCourses() {
        return courseService.findAllCourses();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CourseDto findCourseById(@PathVariable String id) {
        return courseService.findCourseDtoById(id);
    }

    @GetMapping("/{id}/lessons")
    @ResponseStatus(HttpStatus.OK)
    public List<LessonListDto> findCourseLessonsById(@PathVariable String id) {
        return courseService.findCourseLessonsById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseDto createCourse(@RequestPart CourseCreateDto courseDto,
                                  @RequestPart(required = false, name = "image") MultipartFile courseImage) {
        return courseService.createCourse(courseDto, courseImage);
    }

    @PutMapping("/{id}/image")
    @ResponseStatus(HttpStatus.OK)
    public CourseDto uploadCourseImage(@PathVariable(name = "id") String id,
                                       @RequestPart(name = "image") MultipartFile image) {
        return courseService.uploadCourseImage(id, image);
    }
}
