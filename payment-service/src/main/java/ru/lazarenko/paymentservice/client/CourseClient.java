package ru.lazarenko.paymentservice.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.lazarenko.model.dto.course.CourseDto;

@Component
@RequiredArgsConstructor
public class CourseClient {
    private final static String GET_COURSE_BY_ID = "/api/courses/{id}";
    private final static String DECREASE_FREE_PLACE_COURSE_BY_ID = "/api/courses/{id}/decrease-free-place";
    private final RestTemplate restTemplate;
    private final InterceptorRequest interceptorRequest;

    @Value("${address.course-manager}")
    private String courseManagerAddress;

    public CourseDto getCourseById(Integer id) {
        interceptorRequest.interceptRequest(restTemplate);
        String url = courseManagerAddress.concat(GET_COURSE_BY_ID);
        return restTemplate.getForObject(url, CourseDto.class, id);
    }

    public void decreaseFreePlaceCourseById(Integer courseId) {
        interceptorRequest.interceptRequest(restTemplate);
        String url = courseManagerAddress.concat(DECREASE_FREE_PLACE_COURSE_BY_ID);
        url = url.replace("{id}", courseId.toString());
        restTemplate.put(url, null);
    }
}
