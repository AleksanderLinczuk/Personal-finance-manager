package pl.sda.finance_manager.repository;

import jakarta.persistence.EntityManager;
import pl.sda.finance_manager.DbConnection;
import pl.sda.finance_manager.entity.Category;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class CategoryRepository implements Repository<Category, Long> {
    @Override
    public void create(Category category) {
        EntityManager entityManager = DbConnection.getEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(category);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @Override
    public List<Category> findAll() {
        EntityManager entityManager = DbConnection.getEntityManager();
        List<Category> categories = entityManager.createQuery("FROM Category", Category.class).getResultList();
        entityManager.close();
        return categories;
    }

    @Override
    public Category findById(Long id) {
        EntityManager entityManager = DbConnection.getEntityManager();
        Category category = entityManager.find(Category.class, id);
        entityManager.close();
        return category;
    }

    @Override
    public void update(Category category) {
        EntityManager entityManager = DbConnection.getEntityManager();
        entityManager.getTransaction().begin();
        entityManager.merge(category);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @Override
    public void delete(Category category) {
        EntityManager entityManager = DbConnection.getEntityManager();
        entityManager.getTransaction().begin();
        entityManager.remove(category);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @Override
    public void deleteById(Long id) {
        EntityManager entityManager = DbConnection.getEntityManager();
        entityManager.getTransaction().begin();
        Optional<Category> category = Optional.ofNullable(entityManager.find(Category.class, id));
        category.ifPresent(category1 -> entityManager.remove(category1));
        entityManager.getTransaction().commit();
        entityManager.close();
    }

}
