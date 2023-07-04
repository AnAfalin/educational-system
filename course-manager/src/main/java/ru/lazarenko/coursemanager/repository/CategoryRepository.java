package ru.lazarenko.coursemanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.lazarenko.coursemanager.entity.Category;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findByName(String anyString);
}