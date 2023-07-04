package ru.lazarenko.coursemanager.service.mapper;

import org.mapstruct.Mapper;
import ru.lazarenko.coursemanager.dto.CategoryDto;
import ru.lazarenko.coursemanager.entity.Category;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toCategory(CategoryDto categoryDto);

    CategoryDto toCategoryDto(Category category);

    List<CategoryDto> toCategoryDtoList(List<Category> categories);
}
