package ru.lazarenko.coursemanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import ru.lazarenko.coursemanager.dto.CourseDto;
import ru.lazarenko.coursemanager.entity.Category;
import ru.lazarenko.coursemanager.dto.CategoryDto;
import ru.lazarenko.coursemanager.dto.ResponseDto;
import ru.lazarenko.coursemanager.exception.NoFoundElementException;
import ru.lazarenko.coursemanager.exception.NoUniqueObjectException;
import ru.lazarenko.coursemanager.entity.Course;
import ru.lazarenko.coursemanager.repository.CourseRepository;
import ru.lazarenko.coursemanager.service.mapper.CourseMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class CourseServiceTest {
    @Autowired
    CourseService underTest;

    @MockBean
    CourseRepository courseRepository;

    @MockBean
    CourseMapper courseMapper;

    @Captor
    ArgumentCaptor<Course> captor;

    Course courseFromRequest;
    Course foundCourse;
    CourseDto courseDtoRequest;
    CourseDto courseDtoResponse;

    @BeforeEach
    void prepare() {
        Category category = Category.builder().id(1).name("programming").build();
        CategoryDto categoryResponseDto = CategoryDto.builder().id(1).name("programming").build();

        CategoryDto categoryRequestDto = CategoryDto.builder().name("programming").build();
        Category categoryFromRequest = Category.builder().name("programming").build();

        courseFromRequest = Course.builder()
                .name("Java")
                .countPlace(100)
                .price(new BigDecimal(150_000))
                .startDate(LocalDate.now().plusMonths(1))
                .endDate(LocalDate.now().plusMonths(12))
                .category(categoryFromRequest)
                .build();

        foundCourse = Course.builder()
                .id(1)
                .name("Java")
                .countPlace(100)
                .countFreePlace(10)
                .price(new BigDecimal(150_000))
                .startDate(LocalDate.now().plusMonths(1))
                .endDate(LocalDate.now().plusMonths(12))
                .category(category)
                .build();

        courseDtoRequest = CourseDto.builder()
                .name("Java")
                .countPlace(100)
                .countFreePlace(9)
                .price(new BigDecimal(150_000))
                .startDate(LocalDate.now().plusMonths(1))
                .endDate(LocalDate.now().plusMonths(12))
                .category(categoryRequestDto)
                .build();

        courseDtoResponse = CourseDto.builder()
                .id(1)
                .name("Java")
                .countPlace(100)
                .price(new BigDecimal(150_000))
                .startDate(LocalDate.now().plusMonths(1))
                .endDate(LocalDate.now().plusMonths(12))
                .category(categoryResponseDto)
                .build();
    }

    @Test
    @DisplayName("get course by id | noFoundElementException | course does not exist")
    void getCourseById_noFoundElementException_courseDoesNotExist() {
        Integer id = 5;

        doThrow(NoFoundElementException.class)
                .when(courseRepository)
                .findById(anyInt());

        assertThrows(NoFoundElementException.class, () -> underTest.getCourseById(id));
    }

    @Test
    @DisplayName("get course by id | correct return course | course exist")
    void getCategoryById_correctObject_categoryExist() {
        Integer id = 1;

        when(courseRepository.findById(anyInt()))
                .thenReturn(Optional.of(foundCourse));

        when(courseMapper.toCourseDto(any(Course.class)))
                .thenReturn(courseDtoResponse);

        CourseDto result = underTest.getCourseById(id);

        verify(courseRepository, times(1))
                .findById(anyInt());

        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getId()).isEqualTo(id),
                () -> assertThat(result.getName()).isEqualTo("Java")
        );
    }

    @Test
    @DisplayName("get all courses | empty result list | courses do not exist")
    void getAllCourses_emptyResultList_categoriesDoNotExist() {
        when(courseRepository.findAll())
                .thenReturn(List.of());

        List<CourseDto> result = underTest.getAllCourses();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("get all course | not empty result list | courses exist")
    void getAllCourses_notEmptyResultList_categoriesDoNotExist() {
        when(courseRepository.findAll())
                .thenReturn(List.of(foundCourse));

        when(courseMapper.toCourseDtoList(anyList()))
                .thenReturn(List.of(courseDtoResponse));

        List<CourseDto> result = underTest.getAllCourses();

        verify(courseRepository, times(1))
                .findAll();

        assertAll(
                () -> assertThat(result).isNotEmpty(),
                () -> assertThat(result.size()).isEqualTo(1),
                () -> assertThat(result.get(0).getId()).isEqualTo(1),
                () -> assertThat(result.get(0).getName()).isEqualTo("Java")
        );
    }

    @Test
    @DisplayName("save new course | successful create | name is unique")
    void saveNewCourses_successfulCreate_nameIsUnique() {
        when(courseMapper.toCourse(any(CourseDto.class)))
                .thenReturn(courseFromRequest);

        when(courseRepository.save(any(Course.class)))
                .thenReturn(foundCourse);

        ResponseDto result = underTest.saveNewCourse(courseDtoRequest);

        verify(courseRepository, times(1))
                .save(any(Course.class));

        assertThat(result.getStatus()).isEqualTo(HttpStatus.CREATED.name());
        assertThat(result.getMessage()).isEqualTo("Course successful was create with id='1'");
    }

    @Test
    @DisplayName("save new course | successful create | name is unique")
    void saveNewCourses_noUniqueObjectException_nameIsNotUnique() {
        when(courseRepository.findByName(anyString()))
                .thenReturn(Optional.of(foundCourse));

        assertThrows(NoUniqueObjectException.class, () -> underTest.saveNewCourse(courseDtoRequest));
    }

    @Test
    @DisplayName("decrease free place | successful decrease | course is exist")
    void decreaseFreePlace_successfulDecrease_courseIsExist() {
        when(courseRepository.findById(anyInt()))
                .thenReturn(Optional.of(foundCourse));

        when(courseRepository.save(any(Course.class)))
                .thenReturn(foundCourse);

        ResponseDto result = underTest.decreaseFreePlace(1);

        verify(courseRepository, times(1))
                .findById(anyInt());
        verify(courseRepository, times(1))
                .save(any(Course.class));

        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK.name());
        assertThat(result.getMessage())
                .isEqualTo("Free place on course with id='1' successfully decrease. Actual free places now is=9");
    }

    @Test
    @DisplayName("decrease free place | successful decrease | course is not exist")
    void decreaseFreePlace_noFoundElementException_courseIsNotExist() {
        when(courseRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(NoFoundElementException.class, () -> underTest.decreaseFreePlace(1));

        verify(courseRepository, times(1))
                .findById(anyInt());
        verify(courseRepository, times(0))
                .save(any(Course.class));

    }
}