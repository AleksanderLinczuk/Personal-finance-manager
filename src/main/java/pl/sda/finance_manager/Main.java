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
import java.util.stream.Collectors;

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
            final IncomeRepository incomeRepository = new IncomeRepository();
            final ExpenseRepository expenseRepository = new ExpenseRepository();
            final CategoryService categoryService = new CategoryService(categoryRepository);
            final IncomeService incomeService = new IncomeService(incomeRepository);
            final ExpenseService expenseService = new ExpenseService(expenseRepository, categoryRepository);

            boolean isProgramRunning = true;
            while (isProgramRunning) {
                showMenu();
                int selectedOperation = SCANNER.nextInt();
                SCANNER.nextLine();
                switch (selectedOperation) {
                    case 1 -> categoryMenu(categoryService, categoryRepository);
                    case 2 -> incomeMenu(incomeService, incomeRepository);
                    case 3 -> expenseMenu(expenseService, expenseRepository, categoryService);
                    case 4 -> listAllIncomesAndExpenses(incomeService, expenseService);
                    case 5 -> listExpensesInProvidedTimeRange(expenseRepository);
                    case 6 -> listExpensesFilteredByCategory(categoryService, expenseService);
                    case 7 -> displayTotalBalance(incomeRepository, expenseRepository);
                    case 8 -> displayTotalBalanceInTimeRange(incomeService, expenseService);
                    case 9 -> readExpensesGroupedByCategory(expenseRepository);
                    case 0 -> {
                        isProgramRunning = false;
                        System.out.println("Goodbye!");
                    }
                    default -> System.out.println("Invalid input. Try again");
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
                    addCategory(categoryService);
                }
                case 2 -> {
                    categoryService.readAll();
                }
                case 3 -> {
                    categoryService.readAll();
                    updateCategory(categoryService, categoryRepository);
                }
                case 4 -> {
                    categoryService.readAll();
                    deleteCategory(categoryService);
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
                    addIncome(incomeService);
                }
                case 2 -> {
                    incomeService.readAll();
                }
                case 3 -> {
                    incomeService.readAll();
                    updateIncome(incomeService);
                }
                case 4 -> {
                    incomeService.readAll();
                    deleteIncome(incomeService);
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

    private static void expenseMenu(ExpenseService expenseService, Repository<Expense, Long> expenseRepository, CategoryService categoryService) {
        boolean isExpenseMenuRunning = true;
        while (isExpenseMenuRunning) {
            String name = "EXPENSE";
            showCrudMenu(name);
            int chosenOperation = SCANNER.nextInt();
            SCANNER.nextLine();
            switch (chosenOperation) {
                case 1 -> {
                    addExpense(expenseService, categoryService);
                }
                case 2 -> {
                    expenseService.readAll();
                }
                case 3 -> {
                    expenseService.readAll();
                    updateExpense(expenseService, categoryService);

                }
                case 4 -> {
                    expenseService.readAll();
                    deleteExpense(expenseService);
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

    private static void displayTotalBalanceInTimeRange(IncomeService incomeService, ExpenseService expenseService) {
        boolean isDataCorrect = false;
        while (!isDataCorrect) {
            try {
                String startDate = getStartDateBalance();
                String endDate = getEndDateBalance();
                double incomesAmountInTimeRange = incomeService.sumAllIncomesAmountInTimeRange(startDate, endDate);
                double expensesAmountInTimeRange = expenseService.sumAllExpensesAmountInTimeRange(startDate, endDate);
                System.out.println(" Your total balance in time range from " + startDate + " to " + (StringUtils.isNullOrEmpty(endDate) ? LocalDate.now() : endDate) + " is : " + (incomesAmountInTimeRange - expensesAmountInTimeRange) + "\n");
                isDataCorrect = true;
            } catch (Exception e) {
                System.err.println("Invalid data provided! Please try again. ");
            }
        }
    }

    private static void displayTotalBalance(IncomeRepository incomeRepository, ExpenseRepository expenseRepository) {
        double sumAllIncomesAmount = incomeRepository.sumAllIncomesAmount();
        double sumAllExpensesAmount = expenseRepository.sumAllExpensesAmount();
        System.out.println("Your total balance (all incomes - all expenses) is : " + (sumAllIncomesAmount - sumAllExpensesAmount) + "\n");
    }

    private static void listExpensesFilteredByCategory(CategoryService categoryService, ExpenseService expenseService) {
        boolean isDataCorrect = false;
        while (!isDataCorrect) {
            try {
                System.out.println("Select id of category to filter expenses by: ");
                categoryService.readAll();
                Long selectedId = SCANNER.nextLong();
                SCANNER.nextLine();
                expenseService.readExpensesFilteredByCategory(selectedId, categoryService);
                isDataCorrect = true;
            } catch (Exception e) {
                System.err.println("Invalid data provided! Please try again. ");
            }
        }
    }

    private static void listExpensesInProvidedTimeRange(Repository<Expense, Long> expenseRepository) {
        boolean isDataCorrect = false;
        while (!isDataCorrect) {
            try {
                LocalDate startDate = getStartDateFromUserInput();
                LocalDate endDate = getEndDateFromUserInput();
                readExpensesWithinTimeRange(expenseRepository, startDate, endDate);
                isDataCorrect = true;
            } catch (Exception e) {
                System.err.println("Invalid data provided! Please try again. ");
            }
        }
    }

    private static void listAllIncomesAndExpenses(IncomeService incomeService, ExpenseService expenseService) {
        System.out.println("List of all incomes: ");
        incomeService.readAll();
        System.out.println("List of all expenses: ");
        expenseService.readAll();
    }


    private static void deleteCategory(CategoryService categoryService) {
        boolean isDataCorrect = false;
        while (!isDataCorrect) {
            try {
                Long id = getCategoryIdFromUserInput();
                categoryService.deleteById(id);
                isDataCorrect = true;
            } catch (Exception e) {
                System.err.println("Invalid data provided! Please try again. ");
            }
        }
    }

    private static void updateCategory(CategoryService categoryService, Repository<Category, Long> categoryRepository) {
        boolean isDataCorrect = false;
        while (!isDataCorrect) {
            try {
                Long id = getCategoryIdFromUserInput();
                Category categoryToUpdate = categoryService.findById(id);
                String updatedName = getCategoryNameFromUserInput();
                categoryToUpdate.setName(updatedName);
                categoryRepository.update(categoryToUpdate);
                isDataCorrect = true;
            } catch (Exception e) {
                System.err.println("Invalid data provided! Please try again. ");
            }
        }
    }

    private static void addCategory(CategoryService categoryService) {
        boolean isDataCorrect = false;
        while (!isDataCorrect) {
            try {
                String categoryName = getCategoryNameFromUserInput();
                categoryService.addCategory(categoryName);
                isDataCorrect = true;
            } catch (Exception e) {
                System.err.println("Invalid data provided! Please try again. ");
            }
        }
    }


    private static void deleteIncome(IncomeService incomeService) {
        boolean isDeletedSuccessfully = false;
        while (!isDeletedSuccessfully) {
            try {
                Long id = getIncomeIdFromUserInput();
                incomeService.deleteById(id);
                isDeletedSuccessfully = true;
            } catch (Exception e) {
                System.err.println("Invalid data provided! Please try again. ");
            }
        }
    }

    private static void updateIncome(IncomeService incomeService) {
        boolean isDataCorrect = false;
        while (!isDataCorrect) {
            try {
                Thread.sleep(500);
                Long id = getIncomeIdFromUserInput();
                if (incomeService.findById(id) == null) {
                    throw new NullPointerException();
                }
                Income incomeToUpdate = incomeService.findById(id);
                double amount = getIncomeFromUserInput();
                LocalDate date = getDateFromUserInput();
                String commentary = getCommentaryFromUserInput();
                incomeService.updateIncome(incomeToUpdate, amount, date, commentary);
                isDataCorrect = true;
            } catch (Exception e) {
                System.err.println("Invalid data provided! Please try again. ");
            }
        }
    }

    private static void addIncome(IncomeService incomeService) {
        boolean isDataCorrect = false;
        while (!isDataCorrect) {
            try {
                double amount = getIncomeFromUserInput();
                LocalDate date = getDateFromUserInput();
                String commentary = getCommentaryFromUserInput();
                incomeService.addIncome(amount, date, commentary);
                isDataCorrect = true;
            } catch (Exception e) {
                System.err.println("Invalid data provided! Please try again. ");
            }
        }
    }


    private static void deleteExpense(ExpenseService expenseService) {
        boolean isDataCorrect = false;
        while (!isDataCorrect) {
            try {
                long selectedExpenseId = getExpenseIdFromUserInput();
                expenseService.deleteById(selectedExpenseId);
                isDataCorrect = true;
            } catch (Exception e) {
                System.err.println("Invalid data provided! Please try again. ");
            }
        }
    }

    private static void updateExpense(ExpenseService expenseService, CategoryService categoryService) {
        boolean isDataCorrect = false;
        while (!isDataCorrect) {
            try {
                long selectedExpenseId = getExpenseIdFromUserInput();
                Expense expenseToUpdate = expenseService.findById(selectedExpenseId);
                double amount = getExpenseFromUserInput();
                long categoryId = getCategoryIdFromUserInput(categoryService);
                String date = getDateAsStringFromUserInput();
                String commentary = getCommentaryFromUserInput();
                expenseService.updateExpense(expenseToUpdate, amount, categoryId, date, commentary);
                isDataCorrect = true;
            } catch (Exception e) {
                System.err.println("Invalid data provided! Please try again. ");
            }
        }
    }

    private static void addExpense(ExpenseService expenseService, CategoryService categoryService) {
        boolean isDataCorrect = false;
        while (!isDataCorrect) {
            try {
                Thread.sleep(500);
                double amount = getExpenseFromUserInput();
                long categoryId = getCategoryIdFromUserInput(categoryService);
                LocalDate date = getDateFromUserInput();
                String commentary = getCommentaryFromUserInput();
                expenseService.addExpense(amount, categoryId, date, commentary);
                isDataCorrect = true;
            } catch (Exception e) {
                System.err.println("Invalid data provided! Please try again. ");
            }
        }
    }

    public static void showMenu() {
        System.out.println("CRUD MENU: \n"
                + "1 - CATEGORY CRUD \n"
                + "2 - INCOME CRUD \n"
                + "3 - EXPENSE CRUD\n"
                + "4 - DISPLAY ALL EXPENSES AND INCOMES \n"
                + "5 - DISPLAY EXPENSES FROM SPECIFIC DATES \n"
                + "6 - DISPLAY EXPENSES FILTERED BY CATEGORY \n"
                + "7 - DISPLAY TOTAL BALANCE (ALL INCOMES - ALL EXPENSES) \n"
                + "8 - DISPLAY TOTAL BALANCE (ALL INCOMES - ALL EXPENSES) WITHIN SPECIFIC TIME RANGE \n"
                + "9 - DISPLAY SUM AND NUMBER OF ALL EXPENSES GROUPED BY CATEGORY \n"
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

    private static void readExpensesGroupedByCategory(ExpenseRepository expenseRepository) {
        System.out.println("Sum of all expenses grouped by categories:");
        List<Object[]> results = expenseRepository.findSumOfExpensesGroupedByCategory();
        results.stream().map(r -> "Sum of all expenses: " + r[0] + " from " + r[1] + " transactions, in category:  " + r[2]).collect(Collectors.toList()).forEach(System.out::println);
    }

    private static String getEndDateBalance() {
        System.out.println("Provide end date of balance (incomes - expenses) to display: [YYYY-MM-DD]. Or leave this field empty to select current date. ");
        String endDate = SCANNER.nextLine();
        return endDate;
    }

    private static String getStartDateBalance() {
        System.out.println("Provide start date of balance (incomes - expenses) to display: [YYYY-MM-DD]");
        String startDate = SCANNER.nextLine();
        return startDate;
    }

    private static void readExpensesWithinTimeRange(Repository<Expense, Long> expenseRepository, LocalDate startDate, LocalDate endDate) {
        List<Expense> expenses = expenseRepository.findAll();
        expenses.stream().filter(expense -> expense.getDate().isAfter(startDate.minusDays(1))
                        && expense.getDate().isBefore(endDate.plusDays(1)))
                .forEach(System.out::println);
    }

    private static LocalDate getEndDateFromUserInput() {
        System.out.println("Provide end date of expenses to display: [YYYY-MM-DD]. Or leave this field empty to choose current date.");
        String endDateAsString = SCANNER.nextLine();
        LocalDate endDate;
        if (StringUtils.isNullOrEmpty(endDateAsString)) {
            endDate = LocalDate.now();
        } else {
            endDate = LocalDate.parse(endDateAsString);
        }
        return endDate;
    }

    private static LocalDate getStartDateFromUserInput() {
        System.out.println("Provide start date of expenses to display: [YYYY-MM-DD]");
        LocalDate startDate = LocalDate.parse(SCANNER.nextLine());
        return startDate;
    }

    private static long getExpenseIdFromUserInput() {
        System.out.println(" Provide id of expense: ");
        long selectedExpenseId = SCANNER.nextLong();
        SCANNER.nextLine();
        return selectedExpenseId;
    }

    private static String getCommentaryFromUserInput() {
        System.out.println("Provide commentary (optional): ");
        String commentary = SCANNER.nextLine();
        return commentary;
    }

    private static String getDateAsStringFromUserInput() {
        System.out.println("Provide expense date or leave this field empty to insert current date: ");
        String date = SCANNER.nextLine();
        return date;
    }

    private static long getCategoryIdFromUserInput(CategoryService categoryService) {
        System.out.println("Choose from available category ids: ");
        categoryService.readAll();
        long categoryId = SCANNER.nextLong();
        SCANNER.nextLine();
        return categoryId;
    }

    private static double getExpenseFromUserInput() {
        System.out.println("Provide expense amount: ");
        double amount = SCANNER.nextDouble();
        SCANNER.nextLine();
        return amount;
    }

    private static Long getCategoryIdFromUserInput() {
        System.out.println("Provide id of category: ");
        Long id = SCANNER.nextLong();
        SCANNER.nextLine();
        return id;
    }

    private static String getCategoryNameFromUserInput() {
        System.out.println("Provide new category name: ");
        String categoryName = SCANNER.nextLine();
        return categoryName;
    }

    private static Long getIncomeIdFromUserInput() {
        System.out.println("Provide id of income: ");
        Long id = SCANNER.nextLong();
        SCANNER.nextLine();
        return id;
    }

    private static LocalDate getDateFromUserInput() {
        System.out.println("Provide date [YYYY-MM-DD] or leave this field empty to insert current date: ");
        String dateAsString = SCANNER.nextLine();
        LocalDate date;
        if (!StringUtils.isNullOrEmpty(dateAsString)) {
            date = LocalDate.parse(dateAsString);
        } else {
            date = LocalDate.now();
        }
        return date;
    }

    private static double getIncomeFromUserInput() {
        System.out.println("Provide income amount: ");
        double amount = SCANNER.nextDouble();
        SCANNER.nextLine();
        return amount;
    }
}

