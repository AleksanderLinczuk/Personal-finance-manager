package pl.sda.finance_manager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class DbConnection {
    private static final EntityManagerFactory entityManagerFactory = Persistence.
            createEntityManagerFactory("finance_manager");

    public static EntityManager getEntityManager(){
        return entityManagerFactory.createEntityManager();
    }

    public static  void closeEntityManagerFactory(){
        entityManagerFactory.close();
    }

}
