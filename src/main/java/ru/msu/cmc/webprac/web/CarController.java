package ru.msu.cmc.webprac.web;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.msu.cmc.webprac.entities.Car;
import ru.msu.cmc.webprac.entities.CarBrand;
import ru.msu.cmc.webprac.entities.TestDrive;
import ru.msu.cmc.webprac.enums.TransmissionType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
public class CarController extends ApiSupport {

    @GetMapping(value = "/api/cars", produces = JSON)
    public ResponseEntity<String> list(@RequestParam Map<String, String> params) {
        try (Session session = openSession()) {
            StringBuilder hql = new StringBuilder("SELECT c FROM Car c JOIN FETCH c.brand b WHERE 1=1");
            Map<String, Object> queryParams = new LinkedHashMap<>();
            Long brandId = longValue(params, "brandId");
            if (brandId != null) {
                hql.append(" AND b.brandId = :brandId");
                queryParams.put("brandId", brandId);
            }
            String registration = text(params, "registrationNumber");
            if (registration != null) {
                hql.append(" AND lower(c.registrationNumber) LIKE :registration");
                queryParams.put("registration", "%" + registration.toLowerCase() + "%");
            }
            if (decimalValue(params, "minPrice") != null) {
                hql.append(" AND c.price >= :minPrice");
                queryParams.put("minPrice", decimalValue(params, "minPrice"));
            }
            if (decimalValue(params, "maxPrice") != null) {
                hql.append(" AND c.price <= :maxPrice");
                queryParams.put("maxPrice", decimalValue(params, "maxPrice"));
            }
            String transmission = text(params, "transmissionType");
            if (transmission != null) {
                hql.append(" AND c.transmissionType = :transmission");
                queryParams.put("transmission", TransmissionType.valueOf(transmission));
            }
            String color = text(params, "color");
            if (color != null) {
                hql.append(" AND lower(c.color) LIKE :color");
                queryParams.put("color", "%" + color.toLowerCase() + "%");
            }
            hql.append(" ORDER BY c.price");
            Query<Car> query = session.createQuery(hql.toString(), Car.class);
            for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
            List<Map<String, Object>> dto = new ArrayList<>();
            for (Car car : query.list()) {
                dto.add(DtoFactory.car(car));
            }
            return ok(dto);
        } catch (IllegalArgumentException ex) {
            return badRequest("Некорректные параметры фильтра автомобилей");
        }
    }

    @GetMapping(value = "/api/cars/{id}", produces = JSON)
    public ResponseEntity<String> details(@PathVariable Long id) {
        try (Session session = openSession()) {
            Car car = session.createQuery(
                            "SELECT c FROM Car c JOIN FETCH c.brand WHERE c.carId = :id", Car.class)
                    .setParameter("id", id)
                    .uniqueResultOptional()
                    .orElse(null);
            if (car == null) {
                return notFound("Автомобиль не найден");
            }
            Map<String, Object> dto = DtoFactory.car(car);
            List<TestDrive> testDrives = session.createQuery(
                            "SELECT td FROM TestDrive td JOIN FETCH td.client JOIN FETCH td.car c JOIN FETCH c.brand " +
                                    "WHERE c.carId = :id ORDER BY td.testDriveAt DESC",
                            TestDrive.class)
                    .setParameter("id", id)
                    .list();
            List<Map<String, Object>> driveDto = new ArrayList<>();
            for (TestDrive testDrive : testDrives) {
                driveDto.add(DtoFactory.testDrive(testDrive));
            }
            dto.put("testDrives", driveDto);
            return ok(dto);
        }
    }

    @PostMapping(value = "/api/cars", produces = JSON)
    public ResponseEntity<String> create(@RequestParam Map<String, String> form) {
        return save(null, form);
    }

    @PostMapping(value = "/api/cars/{id}", produces = JSON)
    public ResponseEntity<String> update(@PathVariable Long id, @RequestParam Map<String, String> form) {
        return save(id, form);
    }

    @PostMapping(value = "/api/cars/{id}/delete", produces = JSON)
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try (Session session = openSession()) {
            Transaction tx = session.beginTransaction();
            Car car = session.get(Car.class, id);
            if (car == null) {
                tx.rollback();
                return notFound("Автомобиль не найден");
            }
            session.delete(car);
            tx.commit();
            return ok(result("Автомобиль удален", id));
        } catch (RuntimeException ex) {
            return conflict("Автомобиль нельзя удалить, пока он используется в заказах или тест-драйвах");
        }
    }

    private ResponseEntity<String> save(Long id, Map<String, String> form) {
        try (Session session = openSession()) {
            Transaction tx = session.beginTransaction();
            Car car = id == null ? new Car() : session.get(Car.class, id);
            if (car == null) {
                tx.rollback();
                return notFound("Автомобиль не найден");
            }
            Long brandId = longValue(form, "brandId");
            if (brandId == null) {
                throw new IllegalArgumentException("Поле \"Марка\" обязательно");
            }
            CarBrand brand = session.get(CarBrand.class, brandId);
            if (brand == null) {
                throw new IllegalArgumentException("Выбранная марка не найдена");
            }
            fillCar(car, brand, form);
            if (id == null) {
                session.save(car);
            }
            tx.commit();
            return id == null
                    ? created(result("Автомобиль добавлен", DtoFactory.car(car)))
                    : ok(result("Автомобиль обновлен", DtoFactory.car(car)));
        } catch (IllegalArgumentException ex) {
            return badRequest(ex.getMessage());
        } catch (RuntimeException ex) {
            return conflict("Не удалось сохранить автомобиль: проверьте уникальность госномера и обязательные поля");
        }
    }

    private void fillCar(Car car, CarBrand brand, Map<String, String> form) {
        car.setBrand(brand);
        car.setRegistrationNumber(requiredText(form, "registrationNumber", "Госномер"));
        car.setEngineVolumeL(decimalValue(form, "engineVolumeL"));
        car.setEnginePowerHp(intValue(form, "enginePowerHp"));
        car.setFuelConsumptionL100km(decimalValue(form, "fuelConsumptionL100km"));
        car.setDoorsCount(shortValue(form, "doorsCount"));
        car.setSeatsCount(shortValue(form, "seatsCount"));
        car.setTrunkCapacityL(intValue(form, "trunkCapacityL"));
        car.setTransmissionType(TransmissionType.valueOf(requiredText(form, "transmissionType", "КПП")));
        car.setHasCruiseControl(boolValue(form, "hasCruiseControl"));
        car.setRequiredFuel(requiredText(form, "requiredFuel", "Топливо"));
        car.setHasAirConditioner(boolValue(form, "hasAirConditioner"));
        car.setHasRadio(boolValue(form, "hasRadio"));
        car.setHasVideoSystem(boolValue(form, "hasVideoSystem"));
        car.setHasGps(boolValue(form, "hasGps"));
        car.setInteriorTrim(text(form, "interiorTrim"));
        car.setColor(text(form, "color"));
        car.setMileageKm(intValue(form, "mileageKm"));
        car.setLastServiceDate(dateValue(form, "lastServiceDate"));
        car.setPrice(decimalValue(form, "price"));
        if (car.getEngineVolumeL() == null || car.getEnginePowerHp() == null
                || car.getMileageKm() == null || car.getPrice() == null) {
            throw new IllegalArgumentException("Заполните объем и мощность двигателя, пробег и цену");
        }
    }
}
