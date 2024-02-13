package org.project.nuwabackend.repository.jpa;

import org.project.nuwabackend.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
