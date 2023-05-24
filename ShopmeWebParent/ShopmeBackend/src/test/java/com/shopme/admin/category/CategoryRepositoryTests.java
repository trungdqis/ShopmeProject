package com.shopme.admin.category;

import com.shopme.common.entity.Category;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Set;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
class CategoryRepositoryTests {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void testCreateRootCategory() {
        Category category = new Category("Electronics");
        var savedCategory = categoryRepository.save(category);

        Assertions.assertThat(savedCategory.getId()).isPositive();
    }

    @Test
    void testCreateSubCategory() {
        Category parent = new Category(5);
        Category subCategory = new Category("Memory", parent);

        var savedCategory = categoryRepository.save(subCategory);
        Assertions.assertThat(savedCategory.getId()).isPositive();
    }

    @Test
    void testGetCategory() {
        Category category = categoryRepository.findById(1).get();
        System.out.println(category.getName());

        Set<Category> children = category.getChildren();

        for (Category subCategory : children) {
            System.out.println(subCategory.getName());
        }

        Assertions.assertThat(children.size()).isPositive();
    }

    @Test
    void testPrintHierarchicalCategories() {
        var categories = categoryRepository.findAll();

        for (Category category : categories) {
            if (null == category.getParent()) {
                System.out.println(category.getName());

                var children = category.getChildren();

                for (Category subCategory : children) {
                    System.out.println("--" + subCategory.getName());
                    printChildren(subCategory, 1);
                }
            }
        }
    }

    private void printChildren(Category parent, int subLevel) {
        int newSubLevel = subLevel + 1;
        var children = parent.getChildren();

        for (Category subCategory : children) {
            for (int i = 0; i < newSubLevel;  i++) {
                System.out.print("--");
            }
            System.out.println(subCategory.getName());

            printChildren(subCategory, newSubLevel);
        }
    }
}
