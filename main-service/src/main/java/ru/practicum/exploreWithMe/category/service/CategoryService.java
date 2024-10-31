package ru.practicum.exploreWithMe.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exploreWithMe.category.dto.CategoryDto;
import ru.practicum.exploreWithMe.category.mapper.CategoryMapper;
import ru.practicum.exploreWithMe.category.repository.CategoryRepository;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryDto> getCategories(Integer from, Integer size) {
        log.info("Получение категорий с параметрами: from={}, size={}", from, size);
        return categoryMapper.toCategoryDtoList(categoryRepository.findAll(PageRequest.of(from, size)).toList());
    }

    public CategoryDto getCategoryById(Long catId) {
        log.info("Получение категории с id ={}", catId);
        return categoryMapper.toCategoryDto(categoryRepository.findCategoryById(catId));
    }
}
