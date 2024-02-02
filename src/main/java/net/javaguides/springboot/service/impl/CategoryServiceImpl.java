package net.javaguides.springboot.service.impl;

import net.javaguides.springboot.dto.CategoryDto;
import net.javaguides.springboot.entity.Category;
import net.javaguides.springboot.mapper.CategoryMapper;
import net.javaguides.springboot.repository.CategoryRepository;
import net.javaguides.springboot.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDto> getAllCategories() {
        try {
            List<Category> categories = categoryRepository.findAll();
            return categories.stream()
                .map(category -> new CategoryDto(category.getId(), category.getName()))
                .collect(Collectors.toList());
        } catch (Exception e) {
            // Log the exception
            e.printStackTrace();
            // Handle the exception or rethrow as needed
            throw new RuntimeException("Error fetching categories from the database", e);
        }
    }

    @Override
    public CategoryDto getCategoryById(Long categoryId) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        return categoryOptional.map(CategoryMapper::mapToCategoryDto).orElse(null);
    }
}
