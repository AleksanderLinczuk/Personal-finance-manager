package pl.sda.finance_manager.repository;

import jakarta.persistence.EntityManager;
import pl.sda.finance_manager.DbConnection;
import pl.sda.finance_manager.entity.Expense;
import pl.sda.finance_manager.entity.Income;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ExpenseRepository implements Repository<Expense, Long> {
    @Override
    public void create(Expense object) {
        EntityManager entityManager = DbConnection.getEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(object);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @Override
    public List<Expense> findAll() {
        EntityManager entityManager = DbConnection.getEntityManager();
        List<Expense> incomeList = entityManager.createQuery("FROM Expense", Expense.class).getResultList();
        entityManager.close();
        return incomeList;
    }

    @Override
    public Expense findById(Long id) {
        EntityManager entityManager = DbConnection.getEntityManager();
        Expense expense = entityManager.find(Expense.class, id);
        entityManager.close();
        return expense;
    }

    @Override
    public void update(Expense object) {
        EntityManager entityManager = DbConnection.getEntityManager();
        entityManager.getTransaction().begin();
        entityManager.merge(object);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @Override
    public void delete(Expense object) {
        EntityManager entityManager = DbConnection.getEntityManager();
        entityManager.getTransaction().begin();
        entityManager.remove(object);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @Override
    public void deleteById(Long id) {
        EntityManager entityManager = DbConnection.getEntityManager();
        entityManager.getTransaction().begin();
        Optional<Expense> expense = Optional.ofNullable(entityManager.find(Expense.class, id));
        expense.ifPresent(e -> entityManager.remove(e));
        entityManager.getTransaction().commit();
        entityManager.close();
    }

}
