package ru.practicum.exploreWithMe.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    @NotBlank
    @Email
    @Size(min = 6, max = 254, message = "Эмейл не должен быть больше 50 символов")
    private String email;
    @Column
    @NotBlank
    @Size(min = 2, max = 250, message = "Имя не должно быть больше 250 символов")
    private String name;
}
