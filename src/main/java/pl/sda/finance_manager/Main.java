package pl.sda.finance_manager;

import pl.sda.finance_manager.entity.Category;
import pl.sda.finance_manager.entity.Income;
import pl.sda.finance_manager.repository.CategoryRepository;
import pl.sda.finance_manager.repository.IncomeRepository;
import pl.sda.finance_manager.repository.Repository;
import pl.sda.finance_manager.service.CategoryService;
import pl.sda.finance_manager.service.IncomeService;

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
            final Repository<Income, Long> incomeRepository = new IncomeRepository();
            final CategoryService categoryService = new CategoryService(categoryRepository);
            final IncomeService incomeService = new IncomeService(incomeRepository);

            boolean isProgramRunning = true;
            while (isProgramRunning) {
                showMenu();
                int selectedOperation = SCANNER.nextInt();
                SCANNER.nextLine();
                switch (selectedOperation) {
                    case 1 -> {
                        categoryMenu(categoryService, categoryRepository);
                    }
                    case 2 -> {
                        incomeMenu(incomeService, incomeRepository);
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
        while (isCategoryMenuRunning) {
            String name = "CATEGORY";
            showCrudMenu(name);
            int chosenOperation = SCANNER.nextInt();
            SCANNER.nextLine();
            switch (chosenOperation) {
                case 1 -> {
                    System.out.println("Provide new category name: ");
                    String categoryName = SCANNER.nextLine();
                    categoryService.addCategory(categoryName);
                }
                case 2 -> {
                    categoryService.readAll();
                }
                case 3 -> {
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
                case 4 -> {
                    categoryService.readAll();
                    System.out.println("Provide id of category to delete: ");
                    Long id = SCANNER.nextLong();
                    SCANNER.nextLine();
                    categoryService.deleteById(id);
                }
                case 0 -> {
                    isCategoryMenuRunning = false;
                    System.out.println("Exited " + name + " menu!");
                }
                default -> {
                    System.out.println("Invalid input. Try again");
                }
            }
        }
    }

    private static void incomeMenu(IncomeService incomeService, Repository<Income, Long> incomeRepository) {
        String name = "INCOME";
        boolean isIncomeMenuRunning = true;
        while (isIncomeMenuRunning) {
            showCrudMenu(name);
            int chosenOperation = SCANNER.nextInt();
            SCANNER.nextLine();
            switch (chosenOperation) {
                case 1 -> {
                    System.out.println("Provide income amount: ");
                    double amount = SCANNER.nextDouble();
                    SCANNER.nextLine();
                    System.out.println("Provide income date or leave this field empty to insert current date: ");
                    String date = SCANNER.nextLine();
                    System.out.println("Provide income commentary (optional): ");
                    String commentary = SCANNER.nextLine();
                    incomeService.addIncome(amount, date, commentary);
                }
                case 2 -> {
                    incomeService.readAll();
                }
                case 3 -> {
                    incomeService.readAll();
                    System.out.println("Provide id of income to update: ");
                    Long id = SCANNER.nextLong();
                    SCANNER.nextLine();
                    Income incomeToUpdate = incomeService.findById(id);
                    System.out.println("Provide income amount: ");
                    double amount = SCANNER.nextDouble();
                    SCANNER.nextLine();
                    System.out.println("Provide income date or leave this field empty to insert current date: ");
                    String date = SCANNER.nextLine();
                    System.out.println("Provide income commentary (optional): ");
                    String commentary = SCANNER.nextLine();
                    incomeService.updateIncome(incomeToUpdate, amount, date, commentary);
                    incomeRepository.update(incomeToUpdate);
                }
                case 4 -> {
                    System.out.println("Provide id of income to delete: ");
                    Long id = SCANNER.nextLong();
                    SCANNER.nextLine();
                    incomeService.deleteById(id);
                }
                case 0 -> {
                    isIncomeMenuRunning = false;
                    System.out.println("Exited " + name + " menu!");
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

    public static void showCrudMenu(String name) {
        System.out.println(name + " MENU: \n" +
                "1 - CREATE " + name + " \n" +
                "2 - READ " + name + " \n" +
                "3 - UPDATE " + name + " CATEGORY \n" +
                "4 - DELETE " + name + " \n" +
                "0 - EXIT - go back to CRUD menu");
    }

}

