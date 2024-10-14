package ru.practicum.exploreWithMe.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exploreWithMe.category.dto.CategoryDto;
import ru.practicum.exploreWithMe.category.dto.NewCategoryDto;
import ru.practicum.exploreWithMe.category.mapper.CategoryMapper;
import ru.practicum.exploreWithMe.category.model.Category;
import ru.practicum.exploreWithMe.category.repository.CategoryRepository;
import ru.practicum.exploreWithMe.event.model.Event;
import ru.practicum.exploreWithMe.event.repository.EventRepository;
import ru.practicum.exploreWithMe.exception.CategoryDuplicateException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminCategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryMapper categoryMapper;
    private final Set<String> uniqueCategories = new HashSet<>();

    @Transactional
    public CategoryDto addNewCategoryByAdmin(NewCategoryDto newCategoryDto) {
        if (uniqueCategories.contains(newCategoryDto.getName())) {
            throw new CategoryDuplicateException("Категория с таким названием уже существует");
        }

        Category savedCategory = categoryRepository.save(categoryMapper.toCategory(newCategoryDto));
        uniqueCategories.add(newCategoryDto.getName());
        log.info("Добавление новой категории: {}", newCategoryDto);

        return categoryMapper.toCategoryDto(savedCategory);
    }

    @Transactional
    public CategoryDto updateCategoryByAdmin(Long catId, NewCategoryDto newCategoryDto) {
        if (!uniqueCategories.contains(newCategoryDto.getName())) {
            throw new CategoryDuplicateException("Категория с таким названием не существует");
        }
        Category category = categoryRepository.findCategoryById(catId);
        uniqueCategories.remove(category.getName());

        category.setName(newCategoryDto.getName());
        uniqueCategories.add(newCategoryDto.getName());

        log.info("Обновление категории с id ={}", catId);
        return categoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategoryByAdmin(Long catId) {
        categoryRepository.findCategoryById(catId);
        List<Event> eventList = eventRepository.findByCategoryId(catId);
        if (!eventList.isEmpty()) {
            throw new CategoryDuplicateException("Невозможно удалить категорию, так как она связана с событиями.");
        }
        log.info("Удаление категории с id={}", catId);
        categoryRepository.deleteById(catId);
    }
}
