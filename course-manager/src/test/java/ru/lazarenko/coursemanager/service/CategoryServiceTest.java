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
import ru.lazarenko.coursemanager.client.UserClient;
import ru.lazarenko.coursemanager.entity.Category;
import ru.lazarenko.coursemanager.repository.CategoryRepository;
import ru.lazarenko.coursemanager.exception.NoFoundElementException;
import ru.lazarenko.coursemanager.exception.NoUniqueObjectException;
import ru.lazarenko.coursemanager.service.mapper.CategoryMapper;
import ru.lazarenko.model.dto.course.CategoryDto;
import ru.lazarenko.model.dto.ResponseDto;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class CategoryServiceTest {
    @Autowired
    CategoryService underTest;

    @MockBean
    CategoryRepository categoryRepository;

    @MockBean
    CategoryMapper categoryMapper;

    @MockBean
    UserClient userClient;

    @Captor
    ArgumentCaptor<Category> captor;

    Category foundCategory;
    Category categoryFromRequest;
    CategoryDto categoryDtoRequest;
    CategoryDto categoryDtoResponse;

    @BeforeEach
    void prepare() {
        categoryDtoRequest = CategoryDto.builder().name("programming").build();
        categoryFromRequest = Category.builder().name("programming").build();

        foundCategory = Category.builder().id(1).name("programming").build();
        categoryDtoResponse = CategoryDto.builder().id(1).name("programming").build();
    }

    @Test
    @DisplayName("get category by id | noFoundElementException | category does not exist")
    void getCategoryById_noFoundElementException_categoryDoesNotExist() {
        Integer id = 5;

        doThrow(NoFoundElementException.class)
                .when(categoryRepository)
                .findById(anyInt());

        assertThrows(NoFoundElementException.class, () -> underTest.getCategoryById(id));
    }

    @Test
    @DisplayName("get category by id | correct return category | category exist")
    void getCategoryById_correctObject_categoryExist() {
        Integer id = 1;

        when(categoryRepository.findById(anyInt()))
                .thenReturn(Optional.of(foundCategory));

        when(categoryMapper.toCategoryDto(any(Category.class)))
                .thenReturn(categoryDtoResponse);

        CategoryDto result = underTest.getCategoryById(id);

        verify(categoryRepository, times(1))
                .findById(anyInt());

        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getId()).isEqualTo(id),
                () -> assertThat(result.getName()).isEqualTo("programming")
        );
    }

    @Test
    @DisplayName("get all categories | empty result list | categories do not exist")
    void getAllCategories_emptyResultList_categoriesDoNotExist() {
        when(categoryRepository.findAll())
                .thenReturn(List.of());

        List<CategoryDto> result = underTest.getAllCategories();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("get all categories | not empty result list | categories exist")
    void getAllCategories_notEmptyResultList_categoriesDoNotExist() {
        when(categoryRepository.findAll())
                .thenReturn(List.of(foundCategory));

        when(categoryMapper.toCategoryDtoList(anyList()))
                .thenReturn(List.of(categoryDtoResponse));

        List<CategoryDto> result = underTest.getAllCategories();

        verify(categoryRepository, times(1))
                .findAll();

        assertAll(
                () -> assertThat(result).isNotEmpty(),
                () -> assertThat(result.get(0).getId()).isEqualTo(1),
                () -> assertThat(result.get(0).getName()).isEqualTo("programming")
        );
    }

    @Test
    @DisplayName("save new category | successful create | name is unique")
    void saveNewCategory_successfulCreate_nameIsUnique() {
        when(categoryMapper.toCategory(any(CategoryDto.class)))
                .thenReturn(categoryFromRequest);

        when(categoryRepository.save(any(Category.class)))
                .thenReturn(foundCategory);

        ResponseDto result = underTest.saveNewCategory(categoryDtoRequest);

        verify(categoryRepository, times(1))
                .save(any(Category.class));

        assertThat(result.getStatus()).isEqualTo(HttpStatus.CREATED.name());
        assertThat(result.getMessage()).isEqualTo("Category successful was create with id='1'");
    }

    @Test
    @DisplayName("save new category | successful create | name is unique")
    void saveNewCategory_NoUniqueObjectException_nameIsNotUnique() {
        when(categoryRepository.findByName(anyString()))
                .thenReturn(Optional.of(foundCategory));

        assertThrows(NoUniqueObjectException.class, () -> underTest.saveNewCategory(categoryDtoRequest));
    }

    @Test
    @DisplayName("update category by id | noFoundElementException | category does not exist")
    void updateCategoryById_noFoundElementException_courseDoesNotExist() {
        CategoryDto newCategoryDto = CategoryDto.builder().name("it").build();
        Integer id = 10;

        when(categoryRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(NoFoundElementException.class, () -> underTest.updateCategoryById(id, newCategoryDto));
    }

    @Test
    @DisplayName("update category by id | successful update | category exist")
    void updateCategoryById_noFoundElementException_courseExist() {
        String newName = "IT";
        Integer id = 1;

        CategoryDto newCategory = categoryDtoRequest;
        newCategory.setName(newName);

        Category categoryFromRequestDto = categoryFromRequest;
        categoryFromRequest.setName(newName);

        Category savedCategory = foundCategory;
        foundCategory.setName(newName);


        when(categoryRepository.findById(anyInt()))
                .thenReturn(Optional.of(foundCategory));

        when(categoryMapper.toCategory(any(CategoryDto.class)))
                .thenReturn(categoryFromRequestDto);

        when(categoryRepository.save(any(Category.class)))
                .thenReturn(savedCategory);

        underTest.updateCategoryById(id, newCategory);

        verify(categoryRepository, times(1))
                .save(any(Category.class));

        verify(categoryRepository).save(captor.capture());
        Category value = captor.getValue();

        assertThat(value.getName()).isEqualTo(newName);
        assertThat(value.getId()).isEqualTo(1);
    }

}