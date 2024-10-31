package ru.practicum.exploreWithMe.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.exploreWithMe.category.model.Category;
import ru.practicum.exploreWithMe.exception.NotFoundException;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    default Category findCategoryById(Long catId) {
        return findById(catId).orElseThrow(() -> new NotFoundException("Такой категории не существует"));
    }

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM Category c WHERE c.name = :name AND c.id <> :id")
    boolean existsByNameAndNotId(@Param("name") String name, @Param("id") long id);
}