package ru.practicum.exploreWithMe.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.exploreWithMe.event.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
