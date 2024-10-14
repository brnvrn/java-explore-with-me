package ru.practicum.exploreWithMe.compilation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.exploreWithMe.compilation.model.Compilation;
import ru.practicum.exploreWithMe.exception.NotFoundException;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    default Compilation findCompilationById(Long compilationId) {
        return findById(compilationId).orElseThrow(() -> new NotFoundException("Такой подборки не существует"));
    }
    @Query("SELECT c FROM Compilation c WHERE (c.pinned = :pinned OR :pinned IS NULL)")
    Page<Compilation> findByPinned(@Param("pinned") Boolean pinned, Pageable pageable);
}
