package ru.msu.cmc.webprac.dao.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import ru.msu.cmc.webprac.dao.GenericDAO;
import ru.msu.cmc.webprac.utils.HibernateUtil;

import java.util.List;

public abstract class GenericDAOImpl<T> implements GenericDAO<T> {

    private final Class<T> entityClass;

    protected GenericDAOImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected SessionFactory getSessionFactory() {
        return HibernateUtil.getSessionFactory();
    }

    @Override
    public T getById(Long id) {
        try (Session session = getSessionFactory().openSession()) {
            return session.get(entityClass, id);
        }
    }

    @Override
    public List<T> getAll() {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery("FROM " + entityClass.getSimpleName(), entityClass).list();
        }
    }

    @Override
    public void save(T entity) {
        try (Session session = getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.save(entity);
            tx.commit();
        }
    }

    @Override
    public void update(T entity) {
        try (Session session = getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.update(entity);
            tx.commit();
        }
    }

    @Override
    public void delete(T entity) {
        try (Session session = getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.delete(entity);
            tx.commit();
        }
    }

    @Override
    public void deleteById(Long id) {
        try (Session session = getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            T entity = session.get(entityClass, id);
            if (entity != null) {
                session.delete(entity);
            }
            tx.commit();
        }
    }
}
