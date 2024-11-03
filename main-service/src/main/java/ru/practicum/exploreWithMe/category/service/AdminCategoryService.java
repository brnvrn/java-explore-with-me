package ru.practicum.exploreWithMe.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exploreWithMe.category.dto.CategoryDto;
import ru.practicum.exploreWithMe.category.dto.NewCategoryDto;
import ru.practicum.exploreWithMe.category.dto.UpdateCategoryDto;
import ru.practicum.exploreWithMe.category.mapper.CategoryMapper;
import ru.practicum.exploreWithMe.category.model.Category;
import ru.practicum.exploreWithMe.category.repository.CategoryRepository;
import ru.practicum.exploreWithMe.event.repository.EventRepository;
import ru.practicum.exploreWithMe.exception.CategoryException;
import ru.practicum.exploreWithMe.exception.NotFoundException;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCategoryService {
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Transactional
    public CategoryDto addNewCategoryByAdmin(NewCategoryDto newCategoryDto) {
        Category category = categoryRepository.save(categoryMapper.toCategory(newCategoryDto));
        log.info("Добавление новой категории: {}", newCategoryDto);
        return categoryMapper.toCategoryDto(category);
    }

    @Transactional
    public CategoryDto updateCategoryByAdmin(Long catId, UpdateCategoryDto updateCategoryDto) {
        Category category = categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException(
                "Категория с таким айди не существует"));
        category.setName(updateCategoryDto.getName());
        categoryRepository.save(category);
        log.info("Обновление категории с id = {}", category.getId());
        return categoryMapper.toCategoryDto(category);
    }

    @Transactional
    public void deleteCategoryByAdmin(Long catId) {
        categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException(
                "Категория с таким айди не существует"));
        boolean exists = eventRepository.existsByEventCategoryId(catId);
        if (exists) {
            throw new CategoryException("Категория с таким названием уже существует");
        } else {
            categoryRepository.deleteById(catId);
            log.info("Удаление категории с id={}", catId);
        }
    }
}
