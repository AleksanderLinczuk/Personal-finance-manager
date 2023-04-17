package pl.sda.finance_manager.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import pl.sda.finance_manager.DbConnection;
import pl.sda.finance_manager.entity.Income;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class IncomeRepository implements Repository<Income, Long> {
    @Override
    public void create(Income object) {
        EntityManager entityManager = DbConnection.getEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(object);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @Override
    public List<Income> findAll() {
        EntityManager entityManager = DbConnection.getEntityManager();
        List<Income> incomeList = entityManager.createQuery("FROM Income", Income.class).getResultList();
        entityManager.close();
        return incomeList;
    }

    @Override
    public Income findById(Long id) {
        EntityManager entityManager = DbConnection.getEntityManager();
        Income income = entityManager.find(Income.class, id);
        entityManager.close();
        return income;
    }

    @Override
    public void update(Income object) {
        EntityManager entityManager = DbConnection.getEntityManager();
        entityManager.getTransaction().begin();
        entityManager.merge(object);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @Override
    public void delete(Income object) {
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
        Optional<Income> income = Optional.ofNullable(entityManager.find(Income.class, id));
        income.ifPresent(income1 -> entityManager.remove(income1));
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public double sumAllIncomesAmount() {
        EntityManager entityManager = DbConnection.getEntityManager();
        TypedQuery<Double> query = entityManager.createQuery("SELECT SUM(amount) FROM Income", double.class);
        return query.getSingleResult();
    }

    public double sumAllIncomesAmountInTimeRange(LocalDate startDate, LocalDate endDate) {
        EntityManager entityManager = DbConnection.getEntityManager();
        TypedQuery<Double> query = entityManager.createQuery("SELECT SUM(amount) FROM Income WHERE date BETWEEN :startDate AND :endDate", double.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getSingleResult();
    }
}
