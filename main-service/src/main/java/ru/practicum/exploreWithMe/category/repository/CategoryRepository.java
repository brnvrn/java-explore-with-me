package ru.practicum.exploreWithMe.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.exploreWithMe.category.model.Category;
import ru.practicum.exploreWithMe.exception.NotFoundException;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    default Category findCategoryById(Long catId) {
        return findById(catId).orElseThrow(() -> new NotFoundException("Такой категории не существует"));
    }
}