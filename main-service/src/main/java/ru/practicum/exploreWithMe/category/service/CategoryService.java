package ru.practicum.exploreWithMe.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public List<CategoryDto> getCategories(int from, int size) {
        log.info("Получение категорий с параметрами: from={}, size={}", from, size);
        Pageable pageable = PageRequest.of(from / size, size);
        return categoryRepository.findAll(pageable).stream()
                .map(CategoryMapper::toCategoryDto)
                .toList();
    }

    public CategoryDto getCategoryById(Long catId) {
        log.info("Получение категории с id ={}", catId);
        return CategoryMapper.toCategoryDto(categoryRepository.findCategoryById(catId));
    }
}
