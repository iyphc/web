package ru.msu.cmc.webprac.web;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.msu.cmc.webprac.entities.Car;
import ru.msu.cmc.webprac.entities.CarBrand;
import ru.msu.cmc.webprac.entities.Client;
import ru.msu.cmc.webprac.entities.Order;
import ru.msu.cmc.webprac.entities.OrderRequirement;
import ru.msu.cmc.webprac.enums.OrderStatus;
import ru.msu.cmc.webprac.enums.TransmissionType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class OrderController extends ApiSupport {

    @GetMapping(value = "/api/orders", produces = JSON)
    public ResponseEntity<String> list(@RequestParam Map<String, String> params) {
        try (Session session = openSession()) {
            String status = text(params, "status");
            String hql = "SELECT DISTINCT o FROM Order o JOIN FETCH o.client " +
                    "LEFT JOIN FETCH o.car c LEFT JOIN FETCH c.brand " +
                    "LEFT JOIN FETCH o.requirements r LEFT JOIN FETCH r.desiredBrand";
            List<Order> orders;
            if (status == null) {
                orders = session.createQuery(hql + " ORDER BY o.orderedAt DESC", Order.class).list();
            } else {
                orders = session.createQuery(hql + " WHERE o.status = :status ORDER BY o.orderedAt DESC", Order.class)
                        .setParameter("status", OrderStatus.valueOf(status))
                        .list();
            }
            List<Map<String, Object>> dto = new ArrayList<>();
            for (Order order : orders) {
                dto.add(DtoFactory.order(order));
            }
            return ok(dto);
        } catch (IllegalArgumentException ex) {
            return badRequest("Некорректный статус заказа");
        }
    }

    @GetMapping(value = "/api/orders/{id}", produces = JSON)
    public ResponseEntity<String> details(@PathVariable Long id) {
        try (Session session = openSession()) {
            Order order = session.createQuery(
                            "SELECT o FROM Order o JOIN FETCH o.client " +
                                    "LEFT JOIN FETCH o.car c LEFT JOIN FETCH c.brand " +
                                    "LEFT JOIN FETCH o.requirements r LEFT JOIN FETCH r.desiredBrand " +
                                    "WHERE o.orderId = :id",
                            Order.class)
                    .setParameter("id", id)
                    .uniqueResultOptional()
                    .orElse(null);
            return order == null ? notFound("Заказ не найден") : ok(DtoFactory.order(order));
        }
    }

    @PostMapping(value = "/api/orders", produces = JSON)
    public ResponseEntity<String> create(@RequestParam Map<String, String> form) {
        try (Session session = openSession()) {
            Transaction tx = session.beginTransaction();
            Client client = session.get(Client.class, longValue(form, "clientId"));
            if (client == null) {
                throw new IllegalArgumentException("Выберите клиента");
            }
            Car car = null;
            Long carId = longValue(form, "carId");
            if (carId != null) {
                car = session.get(Car.class, carId);
                if (car == null) {
                    throw new IllegalArgumentException("Выбранный автомобиль не найден");
                }
            }
            Order order = new Order();
            order.setOrderedAt(LocalDateTime.now());
            order.setClient(client);
            order.setCar(car);
            order.setNeedTestDrive(boolValue(form, "needTestDrive"));
            order.setStatus(OrderStatus.IN_PROGRESS);
            OrderRequirement requirement = buildRequirement(session, form);
            if (requirement != null) {
                order.setRequirements(requirement);
            }
            session.save(order);
            tx.commit();
            return created(result("Заказ создан", DtoFactory.order(order)));
        } catch (IllegalArgumentException ex) {
            return badRequest(ex.getMessage());
        } catch (RuntimeException ex) {
            return conflict("Не удалось создать заказ");
        }
    }

    @PostMapping(value = "/api/orders/{id}/status", produces = JSON)
    public ResponseEntity<String> updateStatus(@PathVariable Long id, @RequestParam Map<String, String> form) {
        try (Session session = openSession()) {
            Transaction tx = session.beginTransaction();
            Order order = session.createQuery(
                            "SELECT o FROM Order o JOIN FETCH o.client LEFT JOIN FETCH o.car c LEFT JOIN FETCH c.brand " +
                                    "LEFT JOIN FETCH o.requirements r LEFT JOIN FETCH r.desiredBrand WHERE o.orderId = :id",
                            Order.class)
                    .setParameter("id", id)
                    .uniqueResultOptional()
                    .orElse(null);
            if (order == null) {
                tx.rollback();
                return notFound("Заказ не найден");
            }
            order.setStatus(OrderStatus.valueOf(requiredText(form, "status", "Статус")));
            tx.commit();
            return ok(result("Статус заказа обновлен", DtoFactory.order(order)));
        } catch (IllegalArgumentException ex) {
            return badRequest("Некорректный статус заказа");
        }
    }

    @PostMapping(value = "/api/orders/{id}/delete", produces = JSON)
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try (Session session = openSession()) {
            Transaction tx = session.beginTransaction();
            Order order = session.get(Order.class, id);
            if (order == null) {
                tx.rollback();
                return notFound("Заказ не найден");
            }
            session.delete(order);
            tx.commit();
            return ok(result("Заказ удален", id));
        } catch (RuntimeException ex) {
            return conflict("Не удалось удалить заказ");
        }
    }

    private OrderRequirement buildRequirement(Session session, Map<String, String> form) {
        Long desiredBrandId = longValue(form, "desiredBrandId");
        OrderRequirement requirement = new OrderRequirement();
        boolean hasAny = false;
        if (desiredBrandId != null) {
            CarBrand brand = session.get(CarBrand.class, desiredBrandId);
            if (brand == null) {
                throw new IllegalArgumentException("Желаемая марка не найдена");
            }
            requirement.setDesiredBrand(brand);
            hasAny = true;
        }
        if (decimalValue(form, "desiredEngineVolumeMin") != null) {
            requirement.setDesiredEngineVolumeMin(decimalValue(form, "desiredEngineVolumeMin"));
            hasAny = true;
        }
        if (intValue(form, "desiredEnginePowerMin") != null) {
            requirement.setDesiredEnginePowerMin(intValue(form, "desiredEnginePowerMin"));
            hasAny = true;
        }
        String transmission = text(form, "desiredTransmissionType");
        if (transmission != null) {
            requirement.setDesiredTransmissionType(TransmissionType.valueOf(transmission));
            hasAny = true;
        }
        if (text(form, "desiredRequiredFuel") != null) {
            requirement.setDesiredRequiredFuel(text(form, "desiredRequiredFuel"));
            hasAny = true;
        }
        if (text(form, "desiredColor") != null) {
            requirement.setDesiredColor(text(form, "desiredColor"));
            hasAny = true;
        }
        if (decimalValue(form, "desiredPriceMax") != null) {
            requirement.setDesiredPriceMax(decimalValue(form, "desiredPriceMax"));
            hasAny = true;
        }
        if (text(form, "commentText") != null) {
            requirement.setCommentText(text(form, "commentText"));
            hasAny = true;
        }
        return hasAny ? requirement : null;
    }
}
