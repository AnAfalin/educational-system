package ru.lazarenko.coursemanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.lazarenko.coursemanager.exception.NoFoundElementException;
import ru.lazarenko.coursemanager.exception.NoUniqueObjectException;
import ru.lazarenko.coursemanager.entity.Category;
import ru.lazarenko.coursemanager.repository.CategoryRepository;
import ru.lazarenko.coursemanager.service.mapper.CategoryMapper;
import ru.lazarenko.model.dto.course.CategoryDto;
import ru.lazarenko.model.dto.ResponseDto;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();

        return categoryMapper.toCategoryDtoList(categories);
    }

    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Integer id) {
        Category foundCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new NoFoundElementException("Category wit id='%s' not found".formatted(id)));

        return categoryMapper.toCategoryDto(foundCategory);
    }

    @Transactional
    public ResponseDto saveNewCategory(CategoryDto categoryDto) {
        checkExistCategoryName(categoryDto.getName());

        Category category = categoryMapper.toCategory(categoryDto);

        Category savedCategory = categoryRepository.save(category);
        return ResponseDto.builder()
                .status(HttpStatus.CREATED.name())
                .message("Category successful was create with id='%s'".formatted(savedCategory.getId()))
                .build();
    }

    @Transactional
    public ResponseDto updateCategoryById(Integer id, CategoryDto categoryDto) {
        Category foundCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new NoFoundElementException("Category wit id='%s' not found".formatted(id)));

        foundCategory.setName(categoryDto.getName());

        Category savedCategory = categoryRepository.save(foundCategory);

        return ResponseDto.builder()
                .status(HttpStatus.OK.name())
                .message("Category with id='%s' was successful updated".formatted(savedCategory.getId()))
                .build();
    }

    private void checkExistCategoryName(String name) {
        if (categoryRepository.findByName(name).isPresent()) {
            throw new NoUniqueObjectException("Category with name='%s' already exist".formatted(name));
        }
    }
}
