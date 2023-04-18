package pl.sda.finance_manager.service;

import com.mysql.cj.util.StringUtils;
import pl.sda.finance_manager.entity.Category;
import pl.sda.finance_manager.entity.Expense;
import pl.sda.finance_manager.entity.Income;
import pl.sda.finance_manager.repository.ExpenseRepository;
import pl.sda.finance_manager.repository.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class ExpenseService {

    private final Repository<Expense, Long> expenseRepository;
    private final Repository<Category, Long> categoryRepository;

    public ExpenseService(Repository<Expense, Long> expenseRepository, Repository<Category, Long> categoryRepository) {
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
    }

    public void addExpense(double amount, Long selectedCategoryId, LocalDate date, String commentary) {
        Category category = categoryRepository.findById(selectedCategoryId);
        if (category == null) {
            throw new IllegalArgumentException("Category with id: " + selectedCategoryId + " does not exist!");
        }
        if (amount != 0) {
            Expense expense = new Expense(amount, category, date, commentary);
            expenseRepository.create(expense);
        } else {
            throw new IllegalArgumentException("Provided data is incorrect");
        }
    }

    public void updateExpense(Expense expenseToUpdate, double amount, Long selectedCategoryId, String date, String commentary) {
        Category category = categoryRepository.findById(selectedCategoryId);
        if (category == null) {
            throw new IllegalArgumentException("Category with id: " + selectedCategoryId + " does not exist!");
        }
        if (amount != 0) {
            expenseToUpdate.setAmount(amount);
            expenseToUpdate.setCategory(category);
            expenseToUpdate.setDate(StringUtils.isNullOrEmpty(date) ? LocalDate.now() : LocalDate.parse(date));
            expenseToUpdate.setCommentary(commentary);
            expenseRepository.update(expenseToUpdate);
        } else {
            throw new IllegalArgumentException("Provided data is incorrect");
        }
    }

    public void readAll() {
        List<Expense> expenses = expenseRepository.findAll();
        expenses.forEach(each -> System.out.println(each));
    }

    public Expense findById(Long id) {
        Expense expense = expenseRepository.findById(id);
        if (expense == null) {
            throw new IllegalArgumentException("Expense with id: " + id + " does not exist!");
        }
        return expense;
    }

    public void deleteById(Long id) {
        if (id != null) {
            expenseRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Provided data is incorrect! ");
        }
    }
    public void readExpensesFilteredByCategory(Category selectedCategory){
        if (selectedCategory != null){
            List<Expense> expensesGroupedByCategory = ((ExpenseRepository) expenseRepository).findExpensesFilteredByCategory(selectedCategory);
            for (Expense e : expensesGroupedByCategory){
                System.out.println(e);
            }
        }else{
            throw new IllegalArgumentException("Invalid category id. Try again.");
        }
    }
    public void readExpensesFilteredByCategory(Long selectedId, CategoryService categoryService){
        Category selectedCategory = categoryService.findById(selectedId);
        if (selectedCategory != null){
            List<Expense> expensesGroupedByCategory = ((ExpenseRepository) expenseRepository).findExpensesFilteredByCategory(selectedCategory);
            for (Expense e : expensesGroupedByCategory){
                System.out.println(e);
            }
        }else{
            throw new IllegalArgumentException("Invalid category id. Try again.");
        }
    }
    public double sumAllExpensesAmountInTimeRange(String startDate, String endDate){
        if (!StringUtils.isNullOrEmpty(startDate)){
            LocalDate startDateParse = LocalDate.parse(startDate);
            LocalDate endDateParse;
            if (StringUtils.isNullOrEmpty(endDate)){
                endDateParse = LocalDate.now();
            }else{
                endDateParse = LocalDate.parse(endDate);
            }
            double result = ((ExpenseRepository) expenseRepository).sumAllExpensesAmountInTimeRange(startDateParse, endDateParse);
            return result;
        } else{
            throw new IllegalArgumentException("Invalid data provided! ");
        }
    }
}
