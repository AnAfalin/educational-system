package ru.lazarenko.coursemanager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.lazarenko.coursemanager.service.CategoryService;
import ru.lazarenko.model.dto.course.CategoryDto;
import ru.lazarenko.model.dto.ResponseDto;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/{id}")
    public CategoryDto getCategory(@PathVariable(name = "id") Integer id) {
        return categoryService.getCategoryById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto saveCategory(@RequestBody @Valid CategoryDto categoryDto) {
        return categoryService.saveNewCategory(categoryDto);
    }

    @PutMapping("/{id}")
    public ResponseDto updateCategory(@PathVariable Integer id, @RequestBody @Valid CategoryDto categoryDto) {
        return categoryService.updateCategoryById(id, categoryDto);
    }
}
