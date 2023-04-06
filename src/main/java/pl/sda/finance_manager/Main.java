package pl.sda.finance_manager;

import pl.sda.finance_manager.entity.Category;
import pl.sda.finance_manager.repository.CategoryRepository;
import pl.sda.finance_manager.repository.Repository;
import pl.sda.finance_manager.service.CategoryService;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    public static final String JDBC_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "db_name";
    private static final String DB_USER = "db_user";
    private static final String DB_PASSWORD = "db_password";
    private static final Scanner SCANNER = new Scanner(System.in);


    public static void main(String[] args) throws IOException, SQLException {
        try (final Connection connection = DriverManager.getConnection(JDBC_URL + System.getenv(DB_NAME),
                System.getenv(DB_USER), System.getenv(DB_PASSWORD));) {
            System.out.println("Connected to DB!");

            DbInit dbInit = new DbInit(connection);
            dbInit.initDb();

            final Repository<Category, Long> categoryRepository = new CategoryRepository();
            final CategoryService categoryService = new CategoryService(categoryRepository);

            boolean isProgramRunning = true;
            while (isProgramRunning) {
                showMenu();
                int selectedOperation = SCANNER.nextInt();
                SCANNER.nextLine();
                switch (selectedOperation) {
                    case 1 -> {
                        categoryMenu(categoryService,categoryRepository);
                    }
                    case 0 -> {
                        isProgramRunning = false;
                        System.out.println("Goodbye!");
                    }
                    default -> {
                        System.out.println("Invalid input. Try again");
                    }
                }

            }
        }
    }

    private static void categoryMenu(CategoryService categoryService, Repository<Category, Long> categoryRepository) {
        boolean isCategoryMenuRunning = true;
        while (isCategoryMenuRunning){
            showCategoryMenu();
            int chosenOperation = SCANNER.nextInt();
            SCANNER.nextLine();
            switch (chosenOperation){
                case 1 -> {
                    System.out.println("Provide new category name: ");
                    String name = SCANNER.nextLine();
                    categoryService.addCategory(name);
                }
                case 2 -> {
                    categoryService.readAll();
                }
                case 3 ->{
                    categoryService.readAll();
                    System.out.println("Provide id of category to update: ");
                    Long id = SCANNER.nextLong();
                    SCANNER.nextLine();
                    Category categoryToUpdate = categoryService.findById(id);
                    System.out.println("Provide new category name: ");
                    String updatedName = SCANNER.nextLine();
                    categoryToUpdate.setName(updatedName);
                    categoryRepository.update(categoryToUpdate);
                }
                case 4 ->{
                    categoryService.readAll();
                    System.out.println("Provide id of category to delete: ");
                    Long id = SCANNER.nextLong();
                    categoryService.deleteById(id);
                }
                case 0 -> {
                    isCategoryMenuRunning = false;
                    System.out.println("Exited category menu!");
                }
                default -> {
                    System.out.println("Invalid input. Try again");
                }
            }
        }

    }

    public static void showMenu() {
        System.out.println("CRUD MENU: \n" +
                "1 - CATEGORY \n" +
                "0 - EXIT \n");
    }

    public static void showCategoryMenu() {
        System.out.println("CATEGORY MENU: \n" +
                "1 - CREATE CATEGORY \n" +
                "2 - READ CATEGORIES \n" +
                "3 - UPDATE CATEGORY \n" +
                "4 - DELETE CATEGORY \n" +
                "0 - EXIT - go back to CRUD menu");
    }
}

