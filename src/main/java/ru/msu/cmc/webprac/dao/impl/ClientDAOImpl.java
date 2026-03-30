package ru.msu.cmc.webprac.dao.impl;

import org.hibernate.Session;
import ru.msu.cmc.webprac.dao.ClientDAO;
import ru.msu.cmc.webprac.entities.Client;
import ru.msu.cmc.webprac.enums.OrderStatus;

import java.util.List;

public class ClientDAOImpl extends GenericDAOImpl<Client> implements ClientDAO {

    public ClientDAOImpl() {
        super(Client.class);
    }

    @Override
    public Client getByPhone(String phone) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Client WHERE phone = :phone", Client.class)
                    .setParameter("phone", phone)
                    .uniqueResultOptional()
                    .orElse(null);
        }
    }

    @Override
    public Client getByEmail(String email) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Client WHERE email = :email", Client.class)
                    .setParameter("email", email)
                    .uniqueResultOptional()
                    .orElse(null);
        }
    }

    @Override
    public List<Client> getByName(String namePart) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Client WHERE lower(fullName) LIKE :name ORDER BY fullName",
                            Client.class)
                    .setParameter("name", "%" + namePart.toLowerCase() + "%")
                    .list();
        }
    }

    @Override
    public List<Client> getClientsByOrderStatus(OrderStatus status) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT DISTINCT c FROM Client c JOIN c.orders o " +
                                    "WHERE o.status = :status ORDER BY c.fullName",
                            Client.class)
                    .setParameter("status", status)
                    .list();
        }
    }
}
