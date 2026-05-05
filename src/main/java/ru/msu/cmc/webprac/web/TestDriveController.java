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
import ru.msu.cmc.webprac.entities.Client;
import ru.msu.cmc.webprac.entities.TestDrive;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class TestDriveController extends ApiSupport {

    @GetMapping(value = "/api/test-drives", produces = JSON)
    public ResponseEntity<String> list(@RequestParam Map<String, String> params) {
        try (Session session = openSession()) {
            Long clientId = longValue(params, "clientId");
            Long carId = longValue(params, "carId");
            StringBuilder hql = new StringBuilder(
                    "SELECT td FROM TestDrive td JOIN FETCH td.client JOIN FETCH td.car c JOIN FETCH c.brand WHERE 1=1");
            if (clientId != null) {
                hql.append(" AND td.client.clientId = :clientId");
            }
            if (carId != null) {
                hql.append(" AND c.carId = :carId");
            }
            hql.append(" ORDER BY td.testDriveAt DESC");
            org.hibernate.query.Query<TestDrive> query = session.createQuery(hql.toString(), TestDrive.class);
            if (clientId != null) {
                query.setParameter("clientId", clientId);
            }
            if (carId != null) {
                query.setParameter("carId", carId);
            }
            List<Map<String, Object>> dto = new ArrayList<>();
            for (TestDrive testDrive : query.list()) {
                dto.add(DtoFactory.testDrive(testDrive));
            }
            return ok(dto);
        }
    }

    @PostMapping(value = "/api/test-drives", produces = JSON)
    public ResponseEntity<String> create(@RequestParam Map<String, String> form) {
        try (Session session = openSession()) {
            Transaction tx = session.beginTransaction();
            Client client = session.get(Client.class, longValue(form, "clientId"));
            Car car = session.createQuery(
                            "SELECT c FROM Car c JOIN FETCH c.brand WHERE c.carId = :id", Car.class)
                    .setParameter("id", longValue(form, "carId"))
                    .uniqueResultOptional()
                    .orElse(null);
            if (client == null || car == null) {
                throw new IllegalArgumentException("Выберите клиента и автомобиль");
            }
            TestDrive testDrive = new TestDrive();
            testDrive.setClient(client);
            testDrive.setCar(car);
            LocalDateTime at = dateTimeValue(form, "testDriveAt");
            testDrive.setTestDriveAt(at == null ? LocalDateTime.now() : at);
            testDrive.setNotes(text(form, "notes"));
            session.save(testDrive);
            tx.commit();
            return created(result("Тест-драйв зафиксирован", DtoFactory.testDrive(testDrive)));
        } catch (IllegalArgumentException ex) {
            return badRequest(ex.getMessage());
        } catch (RuntimeException ex) {
            return conflict("Не удалось зафиксировать тест-драйв");
        }
    }

    @PostMapping(value = "/api/test-drives/{id}/delete", produces = JSON)
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try (Session session = openSession()) {
            Transaction tx = session.beginTransaction();
            TestDrive testDrive = session.get(TestDrive.class, id);
            if (testDrive == null) {
                tx.rollback();
                return notFound("Тест-драйв не найден");
            }
            session.delete(testDrive);
            tx.commit();
            return ok(result("Тест-драйв удален", id));
        } catch (RuntimeException ex) {
            return conflict("Не удалось удалить тест-драйв");
        }
    }
}
