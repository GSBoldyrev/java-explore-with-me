package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.NewCategoryDto;
import ru.practicum.ewm.model.Category;

public class CategoryMapper {

    public static Category fromCategoryDto(NewCategoryDto category) {

        return new Category(null, category.getName());
    }

    public static CategoryDto toCategoryDto(Category category) {

        return new CategoryDto(category.getId(), category.getName());
    }
}
