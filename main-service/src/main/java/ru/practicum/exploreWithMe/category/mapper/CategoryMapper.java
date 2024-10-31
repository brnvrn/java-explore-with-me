package ru.practicum.exploreWithMe.category.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.exploreWithMe.category.dto.CategoryDto;
import ru.practicum.exploreWithMe.category.dto.NewCategoryDto;
import ru.practicum.exploreWithMe.category.model.Category;

import java.util.ArrayList;
import java.util.List;


@Component
public class CategoryMapper {
    public static Category toCategory(NewCategoryDto categoryDto) {
        Category category = new Category();
        category.setName(categoryDto.getName());
        return category;
    }

    public static CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }
}