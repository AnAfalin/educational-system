package ru.lazarenko.coursemanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
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
import ru.lazarenko.coursemanager.service.CategoryService;
import ru.lazarenko.model.dto.course.CategoryDto;
import ru.lazarenko.model.dto.ResponseDto;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    CategoryService categoryService;

    @Autowired
    ObjectMapper objectMapper;

    Category category;
    CategoryDto categoryRequestDto;
    CategoryDto categoryResponseDto;

    @BeforeEach
    void prepare() {
        categoryRequestDto = CategoryDto.builder().name("programming").build();

        category = Category.builder().id(1).name("programming").build();
        categoryResponseDto = CategoryDto.builder().id(1).name("programming").build();

    }

    @Nested
    class ValidateCategoryTest {
        Validator validator;

        @BeforeEach
        void prepare() {
            try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
                validator = validatorFactory.getValidator();
            }
        }

        @Test
        @DisplayName("validate category | size of validation list is 1 | filed 'name' is null")
        void validateCategory_correctSizeValidationList_fieldNameIsNull() {
            CategoryDto test = new CategoryDto();

            List<ConstraintViolation<CategoryDto>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("Category name cannot be null or empty", validationSet.get(0).getMessage())
            );
        }

        @Test
        @DisplayName("validate category | size of validation list is 1 | filed 'name' is empty")
        void validateCategory_correctSizeValidationList_fieldNameIsEmpty() {
            CategoryDto test = CategoryDto.builder()
                    .name("")
                    .build();

            List<ConstraintViolation<CategoryDto>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("Category name cannot be null or empty", validationSet.get(0).getMessage())
            );
        }

        @Test
        @DisplayName("validate category | size of validation list is empty | object is correct")
        void validateCategory_correctSizeValidationList_regionCorrect() {
            CategoryDto test = CategoryDto.builder()
                    .name("category name")
                    .build();

            List<ConstraintViolation<CategoryDto>> validationSet = new ArrayList<>(validator.validate(test));
            assertEquals(0, validationSet.size());
        }
    }

    @Test
    @WithMockUser
    @DisplayName("save category | status is ok | request is correct, name is unique")
    void saveCategory_statusOk_requestIsCorrect() throws Exception {
        ResponseDto response = ResponseDto.builder()
                .status(HttpStatus.CREATED.name())
                .message("Category successful was create with id='1'")
                .build();

        when(categoryService.saveNewCategory(any(CategoryDto.class)))
                .thenReturn(response);

        mvc.perform(post("/api/categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(categoryRequestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.name()))
                .andExpect(jsonPath("$.message").value("Category successful was create with id='1'"));
    }

    @Test
    @WithMockUser
    @DisplayName("save category | status is conflict | name is not unique")
    void saveCategory_statusBadRequest_nameIsNotUnique() throws Exception {

        Mockito.doThrow(new NoUniqueObjectException("Category wit name='%s' already exist"
                        .formatted(categoryResponseDto.getName())))
                .when(categoryService)
                .saveNewCategory(any(CategoryDto.class));

        mvc.perform(post("/api/categories").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(categoryRequestDto)))
                .andDo(print())
                .andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.name()))
                .andExpect(jsonPath("$.message").value("Category wit name='programming' already exist"))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser
    @DisplayName("get all categories | status is ok and result list is empty | categories don't exist")
    void getAllCategories_statusOkAndEmptyResultList_categoriesDontExist() throws Exception {
        when(categoryService.getAllCategories())
                .thenReturn(List.of());

        mvc.perform(MockMvcRequestBuilders.get("/api/categories").with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty())
                .andExpect(jsonPath("$.id").doesNotExist());
    }

    @Test
    @WithMockUser

    @DisplayName("get all categories | status is ok and result list is not empty | categories exist")
    void getAllCategories_statusOkAndNotEmptyResultList_categoriesExist() throws Exception {
        when(categoryService.getAllCategories())
                .thenReturn(List.of(categoryResponseDto));

        mvc.perform(MockMvcRequestBuilders.get("/api/categories").with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").exists())
                .andExpect(jsonPath("$.[0].id").value(1))
                .andExpect(jsonPath("$.[0].name").value("programming"));
    }

    @Test
    @WithMockUser
    @DisplayName("get category | status is not found | category does not exist")
    void getCategory_statusNotFound_categoryDontExist() throws Exception {
        Mockito.doThrow(NoFoundElementException.class)
                .when(categoryService)
                .getCategoryById(anyInt());

        mvc.perform(MockMvcRequestBuilders.get("/api/categories/10").with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.name()))
                .andExpect(jsonPath("$.id").doesNotExist());
    }

    @Test
    @WithMockUser
    @DisplayName("get category | status is ok and result is correct | category exist")
    void getCategory_statusOkAndReturnObject_categoryExist() throws Exception {
        when(categoryService.getCategoryById(anyInt()))
                .thenReturn(categoryResponseDto);

        mvc.perform(MockMvcRequestBuilders.get("/api/categories/1").with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("programming"));
    }

    @Test
    @WithMockUser
    @DisplayName("update category | status is not found | category does not exist")
    void updateCategory_statusNotFound_categoryDontExist() throws Exception {
        doThrow(NoFoundElementException.class)
                .when(categoryService)
                .updateCategoryById(anyInt(), any(CategoryDto.class));

        mvc.perform(put("/api/categories/10").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(categoryRequestDto)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.name()))
                .andExpect(jsonPath("$.id").doesNotExist());
    }

    @Test
    @WithMockUser
    @DisplayName("update category | status is ok | category exist")
    void updateCategory_statusOk_categoryExist() throws Exception {
        ResponseDto response = ResponseDto.builder()
                .status(HttpStatus.OK.name())
                .message("Category with id='%s' was successful updated".formatted(category.getId()))
                .build();

        when(categoryService.updateCategoryById(anyInt(), any(CategoryDto.class)))
                .thenReturn(response);

        mvc.perform(put("/api/categories/1").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(categoryRequestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.name()))
                .andExpect(jsonPath("$.message").value("Category with id='1' was successful updated"));
    }

}