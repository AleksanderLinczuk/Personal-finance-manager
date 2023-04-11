package pl.sda.finance_manager.service;

import pl.sda.finance_manager.entity.Income;
import pl.sda.finance_manager.repository.Repository;

import java.time.LocalDate;
import java.util.Set;

public class IncomeService {

    private final Repository<Income, Long> incomeRepository;

    public IncomeService(Repository<Income, Long> incomeRepository) {
        this.incomeRepository = incomeRepository;
    }

    public void addIncome(double amount, String date, String commentary) {
        if (amount != 0) {
            Income income = new Income();
            income.setAmount(amount);
            if (date != null) {
                income.setDate(LocalDate.parse(date));
            } else {
                income.setDate(LocalDate.now());
            }
            income.setCommentary(commentary);
        } else {
            throw new IllegalArgumentException("Provided data is incorrect");
        }
    }

    public void updateIncome(Income income, double amount, String date, String commentary) {
        if (amount != 0) {
            income.setAmount(amount);
            if (date != null) {
                income.setDate(LocalDate.parse(date));
            } else {
                income.setDate(LocalDate.now());
            }
            income.setCommentary(commentary);
        } else {
            throw new IllegalArgumentException("Provided data is incorrect");
        }

    }

    public void readAll() {
        Set<Income> incomes = incomeRepository.findAll();
        incomes.forEach(each -> System.out.println(each));
    }

    public Income findById(Long id) {
        if (id != null) {
            return incomeRepository.findById(id);
        } else {
            throw new IllegalArgumentException("Provided data is incorrect! ");
        }
    }

    public void deleteById(Long id) {
        if (id != null) {
            incomeRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Provided data is incorrect! ");
        }
    }
}
