package net.javaguides.springboot.service;

import net.javaguides.springboot.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getAllCategories();
    CategoryDto getCategoryById(Long categoryId);

}
