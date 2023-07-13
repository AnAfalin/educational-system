package ru.lazarenko.paymentservice.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.lazarenko.model.dto.account.BalanceOperationDto;
import ru.lazarenko.model.dto.student.StudentDto;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class StudentClient {
    private final static String GET_STUDENT_BY_ID = "/api/students/{id}";
    private final static String DECREASE_BALANCE_ACCOUNT_BY_ID = "/api/accounts/{id}/decrease";
    private final InterceptorRequest interceptorRequest;

    @Value("${address.student-manager}")
    private String STUDENT_MANAGER_ADDRESS ;

    private final RestTemplate restTemplate;

    public StudentDto getStudentById(Integer id) {
        String url = STUDENT_MANAGER_ADDRESS.concat(GET_STUDENT_BY_ID);
        interceptorRequest.interceptRequest(restTemplate);
        return restTemplate.getForObject(url, StudentDto.class, id);
    }

    public void decreaseBalanceAccountById(Integer id, BigDecimal price) {
        String url = STUDENT_MANAGER_ADDRESS.concat(DECREASE_BALANCE_ACCOUNT_BY_ID);
        interceptorRequest.interceptRequest(restTemplate);
        restTemplate.postForLocation(url, new BalanceOperationDto(price), id);
    }
}