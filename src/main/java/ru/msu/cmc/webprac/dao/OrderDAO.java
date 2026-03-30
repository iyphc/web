package ru.msu.cmc.webprac.dao;

import ru.msu.cmc.webprac.entities.Car;
import ru.msu.cmc.webprac.entities.Client;
import ru.msu.cmc.webprac.entities.Order;
import ru.msu.cmc.webprac.enums.OrderStatus;

import java.util.List;

public interface OrderDAO extends GenericDAO<Order> {
    List<Order> getByClient(Client client);
    List<Order> getByCar(Car car);
    List<Order> getByStatus(OrderStatus status);
    void updateStatus(Long orderId, OrderStatus newStatus);
}
