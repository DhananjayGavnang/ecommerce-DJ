package com.djecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.djecommerce.model.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findByName(String topLevelCategory);
    @Query("Select c from Category c where c.name=:name and c.parentCategory.name=:parentCategoryName")
    public Category findByNameAndParent(@Param("name") String name,
                                        @Param("parentCategoryName") String parentCategoryName);
}
