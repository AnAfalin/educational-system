package ru.lazarenko.paymentservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.lazarenko.paymentservice.client.CourseClient;
import ru.lazarenko.paymentservice.client.StudentClient;
import ru.lazarenko.paymentservice.dto.AccountDto;
import ru.lazarenko.paymentservice.dto.CourseDto;
import ru.lazarenko.paymentservice.dto.ResponseDto;
import ru.lazarenko.paymentservice.dto.StudentDto;
import ru.lazarenko.paymentservice.entity.CourseRecord;
import ru.lazarenko.paymentservice.exception.CourseRegistrationException;
import ru.lazarenko.paymentservice.repository.CourseRecordRepository;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CourseRegistrationService {
    private final CourseClient courseClient;
    private final StudentClient studentClient;
    private final CourseRecordRepository courseRecordRepository;

    public ResponseDto signUpNewUser(Integer courseId, Integer studentId) {
        CourseDto course = courseClient.getCourseById(courseId);
        StudentDto student = studentClient.getStudentById(studentId);

        checkFreePlacesOnCourse(course);
        checkAmountAccount(student.getAccount(), course.getPrice());

        CourseRecord record = CourseRecord.builder()
                .courseId(courseId)
                .studentId(studentId)
                .build();

        studentClient.decreaseBalanceAccountById(student.getAccount().getId(), course.getPrice());

        course.setCountFreePlace(course.getCountFreePlace() - 1);

        courseClient.decreaseFreePlaceCourseById(course.getId());

        courseRecordRepository.save(record);

        return ResponseDto.builder()
                .status(HttpStatus.OK.name())
                .message("Student with id='%s' successful sign up for course '%s' with id='%s'"
                        .formatted(studentId, course.getName(), courseId))
                .build();
    }

    private void checkAmountAccount(AccountDto account, BigDecimal price) {
        if (account.getBalance().doubleValue() < price.doubleValue()) {
            throw new CourseRegistrationException(
                    "Insufficient funds in the account to pay for the course. Balance is '%s', price of course is '%s'. Top up your balance."
                            .formatted(account.getBalance(), price));
        }
    }

    private void checkFreePlacesOnCourse(CourseDto course) {
        if (course.getCountFreePlace() == 0) {
            throw new CourseRegistrationException(
                    "Registration for course (id='%s', name='%s') is not possible. There are no free places."
                            .formatted(course.getId(), course.getName()));
        }
    }
}
