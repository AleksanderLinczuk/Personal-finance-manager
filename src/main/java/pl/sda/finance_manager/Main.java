package pl.sda.finance_manager;

import com.mysql.cj.util.StringUtils;
import pl.sda.finance_manager.entity.Category;
import pl.sda.finance_manager.entity.Expense;
import pl.sda.finance_manager.entity.Income;
import pl.sda.finance_manager.repository.CategoryRepository;
import pl.sda.finance_manager.repository.ExpenseRepository;
import pl.sda.finance_manager.repository.IncomeRepository;
import pl.sda.finance_manager.repository.Repository;
import pl.sda.finance_manager.service.CategoryService;
import pl.sda.finance_manager.service.ExpenseService;
import pl.sda.finance_manager.service.IncomeService;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;

public class Main {

    public static final String JDBC_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "db_name";
    private static final String DB_USER = "db_user";
    private static final String DB_PASSWORD = "db_password";
    private static final Scanner SCANNER = new Scanner(System.in);


    public static void main(String[] args) throws IOException, SQLException {
        try (final Connection connection = DriverManager.getConnection(JDBC_URL + System.getenv(DB_NAME), System.getenv(DB_USER), System.getenv(DB_PASSWORD));) {
            System.out.println("Connected to DB!");

            DbInit dbInit = new DbInit(connection);
            dbInit.initDb();

            final Repository<Category, Long> categoryRepository = new CategoryRepository();
            final Repository<Income, Long> incomeRepository = new IncomeRepository();
            final Repository<Expense, Long> expenseRepository = new ExpenseRepository();
            final CategoryService categoryService = new CategoryService(categoryRepository);
            final IncomeService incomeService = new IncomeService(incomeRepository);
            final ExpenseService expenseService = new ExpenseService(expenseRepository, categoryRepository);

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
                    case 3 -> {
                        expenseMenu(expenseService, expenseRepository, categoryService);
                    }
                    case 4 -> {
                        System.out.println("List of all incomes: ");
                        incomeService.readAll();
                        System.out.println("List of all expenses: ");
                        expenseService.readAll();
                    }
                    case 5 -> {
                        System.out.println("Provide start date of expenses to display: [YYYY-MM-DD]");
                        LocalDate startDate = LocalDate.parse(SCANNER.nextLine());
                        System.out.println("Provide end date of expenses to display: [YYYY-MM-DD]. Or leave this field empty to choose current date.");
                        String endDateAsString = SCANNER.nextLine();
                        LocalDate endDate;
                        if (StringUtils.isNullOrEmpty(endDateAsString)) {
                            endDate = LocalDate.now();
                        } else {
                            endDate = LocalDate.parse(endDateAsString);
                        }
                        List<Expense> expenses = expenseRepository.findAll();
                        expenses.stream().filter(expense -> expense.getDate().isAfter(startDate.minusDays(1))
                                        && expense.getDate().isBefore(endDate.plusDays(1)))
                                .forEach(System.out::println);
                    }
                    case 6 -> {
                        System.out.println("Select id of category to filter expenses by: ");
                        categoryService.readAll();
                        Long selectedId = SCANNER.nextLong();
                        SCANNER.nextLine();
                        expenseService.readExpensesFilteredByCategory(selectedId, categoryService);
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

    private static void expenseMenu(ExpenseService expenseService, Repository<Expense, Long> expenseRepository, CategoryService categoryService) {
        boolean isExpenseMenuRunning = true;
        while (isExpenseMenuRunning) {
            String name = "EXPENSE";
            showCrudMenu(name);
            int chosenOperation = SCANNER.nextInt();
            SCANNER.nextLine();
            switch (chosenOperation) {
                case 1 -> {
                    System.out.println("Provide expense amount: ");
                    double amount = SCANNER.nextDouble();
                    SCANNER.nextLine();
                    System.out.println("Choose from available category ids: ");
                    categoryService.readAll();
                    long categoryId = SCANNER.nextLong();
                    SCANNER.nextLine();
                    System.out.println("Provide expense date or leave this field empty to insert current date: ");
                    String date = SCANNER.nextLine();
                    System.out.println("Provide expense commentary (optional): ");
                    String commentary = SCANNER.nextLine();
                    expenseService.addExpense(amount, categoryId, date, commentary);
                }
                case 2 -> {
                    expenseService.readAll();
                }
                case 3 -> {
                    expenseService.readAll();
                    System.out.println(" Provide id of expense to update: ");
                    long selectedExpenseId = SCANNER.nextLong();
                    SCANNER.nextLine();
                    Expense expenseToUpdate = expenseService.findById(selectedExpenseId);
                    System.out.println("Provide expense amount: ");
                    double amount = SCANNER.nextDouble();
                    SCANNER.nextLine();
                    System.out.println("Choose from available category ids: ");
                    categoryService.readAll();
                    long categoryId = SCANNER.nextLong();
                    SCANNER.nextLine();
                    System.out.println("Provide expense date or leave this field empty to insert current date: ");
                    String date = SCANNER.nextLine();
                    System.out.println("Provide expense commentary (optional): ");
                    String commentary = SCANNER.nextLine();
                    expenseService.updateExpense(expenseToUpdate, amount, categoryId, date, commentary);
                }
                case 4 -> {
                    System.out.println("Provide id of expense to delete: ");
                    expenseService.readAll();
                    long id = SCANNER.nextLong();
                    SCANNER.nextLine();
                    expenseService.deleteById(id);
                }
                case 0 -> {
                    isExpenseMenuRunning = false;
                    System.out.println("Exited " + name + " menu!");
                }
                default -> {
                    System.out.println("Invalid input. Try again");
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
                    System.out.println("Provide income date [YYYY-MM-DD] or leave this field empty to insert current date: ");
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
                    System.out.println("Provide income date [YYYY-MM-DD] or leave this field empty to insert current date: ");
                    String date = SCANNER.nextLine();
                    System.out.println("Provide income commentary (optional): ");
                    String commentary = SCANNER.nextLine();
                    incomeService.updateIncome(incomeToUpdate, amount, date, commentary);

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
        System.out.println("CRUD MENU: \n"
                + "1 - CATEGORY \n"
                + "2 - INCOME \n"
                + "3 - EXPENSE \n"
                + "4 - DISPLAY ALL EXPENSES AND INCOMES \n"
                + "5 - DISPLAY EXPENSES FROM SPECIFIC DATES \n"
                + "6 - DISPLAY EXPENSES FILTERED BY CATEGORY \n"
                + "0 - EXIT \n");
    }

    public static void showCrudMenu(String name) {
        System.out.println(name + " MENU: \n"
                + "1 - CREATE " + name + " \n"
                + "2 - READ " + name + " \n"
                + "3 - UPDATE " + name + " \n"
                + "4 - DELETE " + name + " \n"
                + "0 - EXIT - go back to CRUD menu");
    }

}

