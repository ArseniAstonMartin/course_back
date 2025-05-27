package udemy.clone.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import udemy.clone.model.elastic.CourseDocument;
import udemy.clone.model.elastic.UserDocument;
import udemy.clone.service.ElasticService;

import java.io.IOException;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {
    private final ElasticService elasticService;

    @GetMapping("/all")
    public Page<CourseDocument> getAllCourses(@RequestParam(name = "page", defaultValue = "0") int page,
                                            @RequestParam(name = "pageSize", defaultValue = "3") int pageSize) {
        return elasticService.findAllCourses(page, pageSize);
    }

    @GetMapping("/teachers")
    public Page<UserDocument> getRelevantTeachers(@RequestParam(name = "query") String query,
                                                  @RequestParam(name = "page", defaultValue = "0") int page,
                                                  @RequestParam(name = "pageSize", defaultValue = "3") int pageSize) {
        return elasticService.searchTeachersByCourseRelevance(query, page, pageSize);
    }

    @GetMapping("/moreLikeThis")
    public Page<CourseDocument> findMoreLikeThisCourse(@RequestParam(name = "courseId") String courseId,
                                                       @RequestParam(name = "page", defaultValue = "0") int page,
                                                       @RequestParam(name = "pageSize", defaultValue = "3") int pageSize) throws IOException {
        return elasticService.findMoreLikeThisCourses(courseId, page, pageSize);
    }

    @GetMapping("/courses")
    public Page<CourseDocument> findRelevantCourses(@RequestParam(name = "query") String query,
                                             @RequestParam(name = "page", defaultValue = "0") int page,
                                             @RequestParam(name = "pageSize", defaultValue = "3") int pageSize) {
        return elasticService.findCoursesByFuzzyQuery(query, page, pageSize);
    }
}
