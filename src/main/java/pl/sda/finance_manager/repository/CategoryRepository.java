package pl.sda.finance_manager.repository;

import jakarta.persistence.EntityManager;
import pl.sda.finance_manager.DbConnection;
import pl.sda.finance_manager.entity.Category;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CategoryRepository {
    public void create(Category category){
        EntityManager entityManager = DbConnection.getEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(category);
        entityManager.getTransaction().commit();
        entityManager.close();
    }
    public Set<Category> findAll(){
        EntityManager entityManager = DbConnection.getEntityManager();
        List<Category> categories = entityManager.createQuery("SELECT FROM Category", Category.class).getResultList();
        entityManager.close();
        return new HashSet<>(categories);
    }

}
