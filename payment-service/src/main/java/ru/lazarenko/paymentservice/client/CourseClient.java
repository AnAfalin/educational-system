package ru.lazarenko.paymentservice.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import ru.lazarenko.paymentservice.dto.CourseDto;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CourseClient {
    private final static String GET_COURSE_BY_ID = "/api/courses/{id}";
    private final static String DECREASE_FREE_PLACE_COURSE_BY_ID = "/api/courses/{id}/decrease-free-place";
    private final RestTemplate restTemplate;

    @Value("${address.course-manager}")
    private String courseManagerAddress;

    public CourseDto getCourseById(Integer id) {
        String url = courseManagerAddress.concat(GET_COURSE_BY_ID);
        return restTemplate.getForObject(url, CourseDto.class, id);
    }

    public void decreaseFreePlaceCourseById(Integer courseId) {
        String url = courseManagerAddress.concat(DECREASE_FREE_PLACE_COURSE_BY_ID);
        url = url.replace("{id}", courseId.toString());
        restTemplate.postForLocation(url, null);
    }
}
