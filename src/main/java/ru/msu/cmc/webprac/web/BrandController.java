package ru.msu.cmc.webprac.web;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.msu.cmc.webprac.entities.CarBrand;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class BrandController extends ApiSupport {

    @GetMapping(value = "/api/brands", produces = JSON)
    public ResponseEntity<String> list(@RequestParam Map<String, String> params) {
        try (Session session = openSession()) {
            String manufacturer = text(params, "manufacturer");
            List<CarBrand> brands;
            if (manufacturer == null) {
                brands = session.createQuery("FROM CarBrand b ORDER BY b.brandName", CarBrand.class).list();
            } else {
                brands = session.createQuery(
                                "FROM CarBrand b WHERE lower(b.manufacturerName) LIKE :m ORDER BY b.brandName",
                                CarBrand.class)
                        .setParameter("m", "%" + manufacturer.toLowerCase() + "%")
                        .list();
            }
            List<Map<String, Object>> dto = new ArrayList<>();
            for (CarBrand brand : brands) {
                dto.add(DtoFactory.brand(brand));
            }
            return ok(dto);
        }
    }

    @GetMapping(value = "/api/brands/{id}", produces = JSON)
    public ResponseEntity<String> details(@PathVariable Long id) {
        try (Session session = openSession()) {
            CarBrand brand = session.get(CarBrand.class, id);
            return brand == null ? notFound("Марка не найдена") : ok(DtoFactory.brand(brand));
        }
    }

    @PostMapping(value = "/api/brands", produces = JSON)
    public ResponseEntity<String> create(@RequestParam Map<String, String> form) {
        try {
            CarBrand brand = new CarBrand(
                    requiredText(form, "brandName", "Марка"),
                    requiredText(form, "manufacturerName", "Производитель"));
            try (Session session = openSession()) {
                Transaction tx = session.beginTransaction();
                session.save(brand);
                tx.commit();
                return created(result("Марка добавлена", DtoFactory.brand(brand)));
            }
        } catch (IllegalArgumentException ex) {
            return badRequest(ex.getMessage());
        } catch (RuntimeException ex) {
            return conflict("Не удалось сохранить марку: проверьте уникальность названия и производителя");
        }
    }

    @PostMapping(value = "/api/brands/{id}", produces = JSON)
    public ResponseEntity<String> update(@PathVariable Long id, @RequestParam Map<String, String> form) {
        try (Session session = openSession()) {
            Transaction tx = session.beginTransaction();
            CarBrand brand = session.get(CarBrand.class, id);
            if (brand == null) {
                tx.rollback();
                return notFound("Марка не найдена");
            }
            brand.setBrandName(requiredText(form, "brandName", "Марка"));
            brand.setManufacturerName(requiredText(form, "manufacturerName", "Производитель"));
            tx.commit();
            return ok(result("Марка обновлена", DtoFactory.brand(brand)));
        } catch (IllegalArgumentException ex) {
            return badRequest(ex.getMessage());
        } catch (RuntimeException ex) {
            return conflict("Не удалось обновить марку");
        }
    }

    @PostMapping(value = "/api/brands/{id}/delete", produces = JSON)
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try (Session session = openSession()) {
            Transaction tx = session.beginTransaction();
            CarBrand brand = session.get(CarBrand.class, id);
            if (brand == null) {
                tx.rollback();
                return notFound("Марка не найдена");
            }
            session.delete(brand);
            tx.commit();
            return ok(result("Марка удалена", id));
        } catch (RuntimeException ex) {
            return conflict("Марку нельзя удалить, пока к ней привязаны автомобили");
        }
    }
}
