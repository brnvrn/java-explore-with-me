package ru.practicum.exploreWithMe.category.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "categories")
@Getter
@Setter
@Accessors(chain = true)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(min = 1, max = 50, message = "Имя не должно быть длинее 50 символов")
    private String name;
}
