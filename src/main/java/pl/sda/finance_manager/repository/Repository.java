package pl.sda.finance_manager.repository;

import java.util.Set;

public interface Repository<T, IdType> {

    void create(T object);

    Set<T> findAll();

    T findById(IdType id);

    void update(T object);

    void delete(T object);

    void deleteById(IdType id);


}
