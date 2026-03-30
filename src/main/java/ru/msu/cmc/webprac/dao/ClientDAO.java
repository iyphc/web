package ru.msu.cmc.webprac.dao;

import ru.msu.cmc.webprac.entities.Client;
import ru.msu.cmc.webprac.enums.OrderStatus;

import java.util.List;

public interface ClientDAO extends GenericDAO<Client> {
    Client getByPhone(String phone);
    Client getByEmail(String email);
    List<Client> getByName(String namePart);
    List<Client> getClientsByOrderStatus(OrderStatus status);
}
