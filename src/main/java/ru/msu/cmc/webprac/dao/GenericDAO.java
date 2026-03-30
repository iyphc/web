package ru.msu.cmc.webprac.dao;

import java.util.List;

public interface GenericDAO<T> {
    T getById(Long id);
    List<T> getAll();
    void save(T entity);
    void update(T entity);
    void delete(T entity);
    void deleteById(Long id);
}
