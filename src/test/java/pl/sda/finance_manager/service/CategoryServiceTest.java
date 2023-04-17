package pl.sda.finance_manager.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.sda.finance_manager.entity.Category;
import pl.sda.finance_manager.repository.CategoryRepository;
import pl.sda.finance_manager.repository.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CategoryServiceTest {




    private CategoryService categoryService;
    private Repository<Category, Long> categoryRepository;

    private Connection connection;

    @BeforeEach
    public void setup() {
        categoryRepository = new CategoryRepository();
        categoryService = new CategoryService(categoryRepository);
    }


    @Test
    public void addCategoryShouldCreateCategoryWhenValidNameProvided() {
        // given
        String categoryName = "Test category";

        // when
        categoryService.addCategory(categoryName);

        // then
        List<Category> categories = categoryRepository.findAll();
        Category testCategory = categories.get(categories.size()-1);
        assertEquals(categoryName, testCategory.getName());
        categoryService.deleteById(testCategory.getId());
    }
}