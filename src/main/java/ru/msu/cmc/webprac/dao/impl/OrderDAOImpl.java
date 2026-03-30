package ru.msu.cmc.webprac.dao.impl;

import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.msu.cmc.webprac.dao.OrderDAO;
import ru.msu.cmc.webprac.entities.Car;
import ru.msu.cmc.webprac.entities.Client;
import ru.msu.cmc.webprac.entities.Order;
import ru.msu.cmc.webprac.enums.OrderStatus;

import java.util.List;

public class OrderDAOImpl extends GenericDAOImpl<Order> implements OrderDAO {

    public OrderDAOImpl() {
        super(Order.class);
    }

    @Override
    public List<Order> getByClient(Client client) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Order o WHERE o.client = :client ORDER BY o.orderedAt DESC",
                            Order.class)
                    .setParameter("client", client)
                    .list();
        }
    }

    @Override
    public List<Order> getByCar(Car car) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Order o WHERE o.car = :car ORDER BY o.orderedAt DESC",
                            Order.class)
                    .setParameter("car", car)
                    .list();
        }
    }

    @Override
    public List<Order> getByStatus(OrderStatus status) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Order o WHERE o.status = :status ORDER BY o.orderedAt",
                            Order.class)
                    .setParameter("status", status)
                    .list();
        }
    }

    @Override
    public void updateStatus(Long orderId, OrderStatus newStatus) {
        try (Session session = getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Order order = session.get(Order.class, orderId);
            if (order != null) {
                order.setStatus(newStatus);
                session.update(order);
            }
            tx.commit();
        }
    }
}
