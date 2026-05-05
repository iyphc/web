package ru.msu.cmc.webprac.web;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.msu.cmc.webprac.entities.Client;
import ru.msu.cmc.webprac.entities.Order;
import ru.msu.cmc.webprac.entities.TestDrive;
import ru.msu.cmc.webprac.enums.OrderStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class ClientController extends ApiSupport {

    @GetMapping(value = "/api/clients", produces = JSON)
    public ResponseEntity<String> list(@RequestParam Map<String, String> params) {
        try (Session session = openSession()) {
            String query = text(params, "q");
            String status = text(params, "status");
            List<Client> clients;
            if (status != null) {
                clients = session.createQuery(
                                "SELECT DISTINCT c FROM Client c JOIN c.orders o " +
                                        "WHERE o.status = :status ORDER BY c.fullName",
                                Client.class)
                        .setParameter("status", OrderStatus.valueOf(status))
                        .list();
            } else if (query != null) {
                clients = session.createQuery(
                                "FROM Client c WHERE lower(c.fullName) LIKE :q OR c.phone LIKE :q OR lower(c.email) LIKE :q " +
                                        "ORDER BY c.fullName",
                                Client.class)
                        .setParameter("q", "%" + query.toLowerCase() + "%")
                        .list();
            } else {
                clients = session.createQuery("FROM Client c ORDER BY c.fullName", Client.class).list();
            }
            List<Map<String, Object>> dto = new ArrayList<>();
            for (Client client : clients) {
                dto.add(DtoFactory.client(client));
            }
            return ok(dto);
        } catch (IllegalArgumentException ex) {
            return badRequest("Некорректный статус заказа");
        }
    }

    @GetMapping(value = "/api/clients/{id}", produces = JSON)
    public ResponseEntity<String> details(@PathVariable Long id) {
        try (Session session = openSession()) {
            Client client = session.get(Client.class, id);
            if (client == null) {
                return notFound("Клиент не найден");
            }
            Map<String, Object> dto = DtoFactory.client(client);
            List<Order> orders = session.createQuery(
                            "SELECT o FROM Order o JOIN FETCH o.client LEFT JOIN FETCH o.car c LEFT JOIN FETCH c.brand " +
                                    "LEFT JOIN FETCH o.requirements r LEFT JOIN FETCH r.desiredBrand " +
                                    "WHERE o.client.clientId = :id ORDER BY o.orderedAt DESC",
                            Order.class)
                    .setParameter("id", id)
                    .list();
            List<Map<String, Object>> orderDto = new ArrayList<>();
            for (Order order : orders) {
                orderDto.add(DtoFactory.order(order));
            }
            dto.put("orders", orderDto);
            List<TestDrive> drives = session.createQuery(
                            "SELECT td FROM TestDrive td JOIN FETCH td.client JOIN FETCH td.car c JOIN FETCH c.brand " +
                                    "WHERE td.client.clientId = :id ORDER BY td.testDriveAt DESC",
                            TestDrive.class)
                    .setParameter("id", id)
                    .list();
            List<Map<String, Object>> driveDto = new ArrayList<>();
            for (TestDrive testDrive : drives) {
                driveDto.add(DtoFactory.testDrive(testDrive));
            }
            dto.put("testDrives", driveDto);
            return ok(dto);
        }
    }

    @PostMapping(value = "/api/clients", produces = JSON)
    public ResponseEntity<String> create(@RequestParam Map<String, String> form) {
        return save(null, form);
    }

    @PostMapping(value = "/api/clients/{id}", produces = JSON)
    public ResponseEntity<String> update(@PathVariable Long id, @RequestParam Map<String, String> form) {
        return save(id, form);
    }

    @PostMapping(value = "/api/clients/{id}/delete", produces = JSON)
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try (Session session = openSession()) {
            Transaction tx = session.beginTransaction();
            Client client = session.get(Client.class, id);
            if (client == null) {
                tx.rollback();
                return notFound("Клиент не найден");
            }
            session.delete(client);
            tx.commit();
            return ok(result("Клиент удален", id));
        } catch (RuntimeException ex) {
            return conflict("Клиента нельзя удалить, пока у него есть заказы или тест-драйвы");
        }
    }

    private ResponseEntity<String> save(Long id, Map<String, String> form) {
        try (Session session = openSession()) {
            Transaction tx = session.beginTransaction();
            Client client = id == null ? new Client() : session.get(Client.class, id);
            if (client == null) {
                tx.rollback();
                return notFound("Клиент не найден");
            }
            client.setFullName(requiredText(form, "fullName", "ФИО"));
            client.setAddress(text(form, "address"));
            client.setPhone(requiredText(form, "phone", "Телефон"));
            client.setEmail(text(form, "email"));
            if (id == null) {
                session.save(client);
            }
            tx.commit();
            return id == null
                    ? created(result("Клиент добавлен", DtoFactory.client(client)))
                    : ok(result("Клиент обновлен", DtoFactory.client(client)));
        } catch (IllegalArgumentException ex) {
            return badRequest(ex.getMessage());
        } catch (RuntimeException ex) {
            return conflict("Не удалось сохранить клиента: телефон и e-mail должны быть уникальными");
        }
    }
}
