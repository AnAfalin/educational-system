package ru.lazarenko.coursemanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.lazarenko.coursemanager.entity.Category;
import ru.lazarenko.coursemanager.exception.NoFoundElementException;
import ru.lazarenko.coursemanager.exception.NoUniqueObjectException;
import ru.lazarenko.coursemanager.entity.Course;
import ru.lazarenko.coursemanager.service.CourseService;
import ru.lazarenko.model.dto.course.CategoryDto;
import ru.lazarenko.model.dto.course.CourseDto;
import ru.lazarenko.model.dto.ResponseDto;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourseController.class)
class CourseControllerTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    CourseService courseService;

    @Autowired
    ObjectMapper objectMapper;

    Category category;

    Course course1;
    CourseDto courseRequestDto1;
    CourseDto courseResponseDto1;

    Course course2;
    CourseDto courseRequestDto2;
    CourseDto courseResponseDto2;

    @BeforeEach
    void prepare() {
        category = Category.builder().id(1).name("programming").build();
        CategoryDto categoryRequestDto = CategoryDto.builder().name("programming").build();
        CategoryDto categoryResponseDto = CategoryDto.builder().id(1).name("programming").build();

        course1 = Course.builder()
                .id(1)
                .name("Java")
                .countPlace(100)
                .price(new BigDecimal(150_000))
                .startDate(LocalDate.now().plusMonths(1))
                .endDate(LocalDate.now().plusMonths(12))
                .category(category)
                .build();

        courseRequestDto1 = CourseDto.builder()
                .name("Java")
                .countPlace(100)
                .price(new BigDecimal(150_000))
                .startDate(LocalDate.now().plusMonths(1))
                .endDate(LocalDate.now().plusMonths(12))
                .category(categoryRequestDto)
                .build();

        courseResponseDto1 = CourseDto.builder()
                .id(1)
                .name("Java")
                .countPlace(100)
                .price(new BigDecimal(150_000))
                .startDate(LocalDate.now().plusMonths(1))
                .endDate(LocalDate.now().plusMonths(12))
                .category(categoryResponseDto)
                .build();

        course2 = Course.builder()
                .id(2)
                .name("Frontend developer")
                .countPlace(70)
                .price(new BigDecimal(100_000))
                .startDate(LocalDate.now().plusMonths(4))
                .endDate(LocalDate.now().plusMonths(7))
                .category(category)
                .build();

        courseRequestDto2 = CourseDto.builder()
                .name("Frontend developer")
                .category(categoryRequestDto)
                .countPlace(70)
                .price(new BigDecimal(100_000))
                .startDate(LocalDate.now().plusMonths(4))
                .endDate(LocalDate.now().plusMonths(7))
                .build();
        courseResponseDto2 = CourseDto.builder()
                .id(2)
                .name("Frontend developer")
                .countPlace(70)
                .category(categoryResponseDto)
                .price(new BigDecimal(100_000))
                .startDate(LocalDate.now().plusMonths(4))
                .endDate(LocalDate.now().plusMonths(7))
                .build();
    }

    @Nested
    class ValidateCourseTest {
        Validator validator;

        @BeforeEach
        void prepare() {
            try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
                validator = validatorFactory.getValidator();
            }
        }

        @Test
        @DisplayName("validate course | size of validation list is 1 | filed 'name' is null")
        void validateCourse_correctSizeValidationList_fieldNameIsNull() {
            CourseDto test = new CourseDto();

            List<ConstraintViolation<CourseDto>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(6, validationSet.size()),
                    () -> assertTrue(validationSet.stream()
                            .map(ConstraintViolation::getMessage).toList().contains("Course name cannot be null or empty"))
            );
        }

        @Test
        @DisplayName("validate course | size of validation list is 1 | filed 'name' is empty")
        void validateCourse_correctSizeValidationList_fieldNameIsEmpty() {
            CategoryDto categoryDto = CategoryDto.builder().name("programming").build();

            CourseDto test = CourseDto.builder()
                    .countPlace(100)
                    .price(new BigDecimal(150_000))
                    .startDate(LocalDate.now().plusMonths(1))
                    .endDate(LocalDate.now().plusMonths(3))
                    .category(categoryDto)
                    .build();

            List<ConstraintViolation<CourseDto>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("Course name cannot be null or empty", validationSet.get(0).getMessage())
            );
        }

        @Test
        @DisplayName("validate course | size of validation list is 1 | filed 'start date' is null")
        void validateCourse_correctSizeValidationList_fieldStartDateIsEmpty() {
            CategoryDto categoryDto = CategoryDto.builder().name("programming").build();

            CourseDto test = CourseDto.builder()
                    .name("Java")
                    .countPlace(100)
                    .price(new BigDecimal(150_000))
                    .endDate(LocalDate.now().plusMonths(3))
                    .category(categoryDto)
                    .build();

            List<ConstraintViolation<CourseDto>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("Start date cannot be null", validationSet.get(0).getMessage())
            );
        }

        @Test
        @DisplayName("validate course | size of validation list is 1 | filed 'end date' is null")
        void validateCourse_correctSizeValidationList_fieldEndDateIsEmpty() {
            CategoryDto categoryDto = CategoryDto.builder().name("programming").build();

            CourseDto test = CourseDto.builder()
                    .name("Java")
                    .countPlace(100)
                    .price(new BigDecimal(150_000))
                    .startDate(LocalDate.now().plusMonths(1))
                    .category(categoryDto)
                    .build();

            List<ConstraintViolation<CourseDto>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("End date cannot be null", validationSet.get(0).getMessage())
            );
        }

        @Test
        @DisplayName("validate course | size of validation list is 1 | filed 'price' is null")
        void validateCourse_correctSizeValidationList_fieldPriceIsEmpty() {
            CategoryDto categoryDto = CategoryDto.builder().name("programming").build();

            CourseDto test = CourseDto.builder()
                    .name("Java")
                    .countPlace(100)
                    .startDate(LocalDate.now().plusMonths(1))
                    .endDate(LocalDate.now().plusMonths(3))
                    .category(categoryDto)
                    .build();

            List<ConstraintViolation<CourseDto>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("Price cannot be null", validationSet.get(0).getMessage())
            );
        }

        @Test
        @DisplayName("validate course | size of validation list is 1 | filed 'countPlace' is null")
        void validateCourse_correctSizeValidationList_fieldCountPlaceIsEmpty() {
            CategoryDto categoryDto = CategoryDto.builder().name("programming").build();

            CourseDto test = CourseDto.builder()
                    .name("Java")
                    .price(new BigDecimal(150_000))
                    .startDate(LocalDate.now().plusMonths(1))
                    .endDate(LocalDate.now().plusMonths(3))
                    .category(categoryDto)
                    .build();

            List<ConstraintViolation<CourseDto>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("Count place of course cannot be null", validationSet.get(0).getMessage())
            );
        }

        @Test
        @DisplayName("validate course | size of validation list is 1 | filed 'category' is null")
        void validateCourse_correctSizeValidationList_fieldCategoryIsEmpty() {

            CourseDto test = CourseDto.builder()
                    .name("Java")
                    .countPlace(100)
                    .price(new BigDecimal(150_000))
                    .startDate(LocalDate.now().plusMonths(1))
                    .endDate(LocalDate.now().plusMonths(3))
                    .build();

            List<ConstraintViolation<CourseDto>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("Category cannot be null", validationSet.get(0).getMessage())
            );
        }

        @Test
        @DisplayName("validate course | size of validation list is 1 | filed 'name' of category is empty")
        void validateCourse_correctSizeValidationList_fieldCategoryNameIsEmpty() {
            CategoryDto categoryDto = CategoryDto.builder().name("").build();

            CourseDto test = CourseDto.builder()
                    .name("Java")
                    .countPlace(100)
                    .price(new BigDecimal(150_000))
                    .startDate(LocalDate.now().plusMonths(1))
                    .endDate(LocalDate.now().plusMonths(3))
                    .category(categoryDto)
                    .build();

            List<ConstraintViolation> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("Category name cannot be null or empty", validationSet.get(0).getMessage())
            );
        }

        @Test
        @DisplayName("validate course | size of validation list is 1 | filed 'name' of category is null")
        void validateCourse_correctSizeValidationList_fieldCategoryNameIsNull() {
            CategoryDto categoryDto = CategoryDto.builder().build();

            CourseDto test = CourseDto.builder()
                    .name("Java")
                    .countPlace(100)
                    .price(new BigDecimal(150_000))
                    .startDate(LocalDate.now().plusMonths(1))
                    .endDate(LocalDate.now().plusMonths(3))
                    .category(categoryDto)
                    .build();

            List<ConstraintViolation> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("Category name cannot be null or empty", validationSet.get(0).getMessage())
            );
        }

        @Test
        @DisplayName("validate course | size of validation list is 1 | filed 'startDate' is in the past")
        void validateCourse_correctSizeValidationList_fieldCStartDateInThePast() {
            CategoryDto categoryDto = CategoryDto.builder().name("programming").build();

            CourseDto test = CourseDto.builder()
                    .name("Java")
                    .countPlace(100)
                    .price(new BigDecimal(150_000))
                    .startDate(LocalDate.now().minusYears(1))
                    .endDate(LocalDate.of(2024, 5, 20))
                    .category(categoryDto)
                    .build();

            List<ConstraintViolation<CourseDto>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("Start date must be in the future or in the present", validationSet.get(0).getMessage())
            );
        }

        @Test
        @DisplayName("validate course | size of validation list is 1 | filed 'endDate' is early than startDate")
        void validateCourse_correctSizeValidationList_fieldEndDateIsEarlyThanStartDate() {
            CategoryDto categoryDto = CategoryDto.builder().name("programming").build();

            CourseDto test = CourseDto.builder()
                    .name("Java")
                    .countPlace(100)
                    .price(new BigDecimal(150_000))
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now().minusDays(2))
                    .category(categoryDto)
                    .build();

            List<ConstraintViolation<CourseDto>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("End date should be after start date", validationSet.get(0).getMessage())
            );
        }
    }

    @Test
    @WithMockUser
    @DisplayName("get all courses | status is ok and empty resul | courses do not exist")
    void getAllCourses_statusIsOkAndEmptyResultList_coursesDoNotExist() throws Exception {
        when(courseService.getAllCourses())
                .thenReturn(List.of());

        mvc.perform(MockMvcRequestBuilders.get("/api/courses").with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty())
                .andExpect(jsonPath("$.id").doesNotExist());
    }

    @Test
    @WithMockUser
    @DisplayName("get all courses | status is ok and result list is not empty | courses exist")
    void getAllCourses_statusOkAndNotEmptyResultList_coursesExist() throws Exception {
        when(courseService.getAllCourses())
                .thenReturn(List.of(courseResponseDto1, courseResponseDto2));


        mvc.perform(MockMvcRequestBuilders.get("/api/courses").with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$.[0].id").exists())
                .andExpect(jsonPath("$.[0].id").value(1))
                .andExpect(jsonPath("$.[0].name").value("Java"))
                .andExpect(jsonPath("$.[1].name").value("Frontend developer"));
    }

    @Test
    @WithMockUser
    @DisplayName("get course | status is not found | course does not exist")
    void getCourse_statusNotFound_categoryDontExist() throws Exception {
        doThrow(NoFoundElementException.class)
                .when(courseService)
                .getCourseById(anyInt());

        mvc.perform(MockMvcRequestBuilders.get("/api/courses/10").with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.name()))
                .andExpect(jsonPath("$.id").doesNotExist());
    }

    @Test
    @WithMockUser
    @DisplayName("get course | status is ok and result is correct | course exist")
    void getCourse_statusOkAndReturnObject_categoryExist() throws Exception {
        when(courseService.getCourseById(anyInt()))
                .thenReturn(courseResponseDto1);

        mvc.perform(MockMvcRequestBuilders.get("/api/courses/1").with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Java"));
    }

    @Test
    @WithMockUser
    @DisplayName("save course | status is ok | request is correct, name is unique")
    void saveCourse_statusOk_requestIsCorrect() throws Exception {
        ResponseDto response = ResponseDto.builder()
                .status(HttpStatus.CREATED.name())
                .message("Course successful was created with id='1'")
                .build();

        when(courseService.saveNewCourse(any(CourseDto.class)))
                .thenReturn(response);

        mvc.perform(post("/api/courses").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(courseRequestDto1)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.name()))
                .andExpect(jsonPath("$.message").value("Course successful was created with id='1'"));
    }

    @Test
    @WithMockUser
    @DisplayName("save course | status is conflict | name is not unique")
    void saveCourse_statusBadRequest_nameIsNotUnique() throws Exception {

        doThrow(new NoUniqueObjectException("Course with name='%s' already exist"
                .formatted(courseRequestDto1.getName())))
                .when(courseService)
                .saveNewCourse(any(CourseDto.class));

        mvc.perform(post("/api/courses").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(courseRequestDto1)))
                .andDo(print())
                .andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.name()))
                .andExpect(jsonPath("$.message").value("Course with name='Java' already exist"))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser
    @DisplayName("decrease free place | status is ok | course is exist")
    void decreaseFreePlace_statusOk_courseIsExist() throws Exception {
        ResponseDto response = ResponseDto.builder()
                .status(HttpStatus.OK.name())
                .message("Free place on course with id='1' successfully decrease. Actual free places now is=9")
                .build();

        when(courseService.decreaseFreePlace(anyInt()))
                .thenReturn(response);

        mvc.perform(put("/api/courses/1/decrease-free-place").with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.name()))
                .andExpect(jsonPath("$.message")
                        .value("Free place on course with id='1' successfully decrease. Actual free places now is=9"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("decrease free place | status is not found | course is not exist")
    void decreaseFreePlace_statusNotFound_freePlaceIsExist() throws Exception {

        doThrow(new NoFoundElementException("Course with id='%s' not found"
                .formatted(course1.getId())))
                .when(courseService)
                .decreaseFreePlace(anyInt());

        mvc.perform(put("/api/courses/1/decrease-free-place").with(csrf()))
                .andDo(print())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.name()))
                .andExpect(jsonPath("$.message").value("Course with id='1' not found"))
                .andExpect(status().isNotFound());
    }

}