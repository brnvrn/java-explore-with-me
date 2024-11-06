package ru.practicum.exploreWithMe.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exploreWithMe.category.dto.CategoryDto;
import ru.practicum.exploreWithMe.category.mapper.CategoryMapper;
import ru.practicum.exploreWithMe.category.model.Category;
import ru.practicum.exploreWithMe.category.repository.CategoryRepository;
import ru.practicum.exploreWithMe.exception.NotFoundException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryDto> getCategories(Integer from, Integer size) {
        log.info("Получение категорий с параметрами: from={}, size={}", from, size);
        Pageable pageable = PageRequest.of(from / size, size);
        return categoryRepository.findAll(pageable).stream()
                .map(categoryMapper::toCategoryDto)
                .toList();
    }

    public CategoryDto getCategoryById(Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException(
                "Категория с таким айди не существует"));
        log.info("Получение категории с id ={}", catId);
        return categoryMapper.toCategoryDto(category);
    }
}