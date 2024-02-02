package net.javaguides.springboot.mapper;

import net.javaguides.springboot.dto.CategoryDto;
import net.javaguides.springboot.entity.Category;

public class CategoryMapper {
    // convert category entity to category dto
    public static CategoryDto mapToCategoryDto(Category category) {
        return CategoryDto.builder()
            .id(category.getId())
            .name(category.getName())
            .build();
    }

    // convert category dto to category entity
    public static Category mapToCategory(CategoryDto categoryDto) {
        return Category.builder()
            .id(categoryDto.getId())
            .name(categoryDto.getName())
            .build();
    }
}
