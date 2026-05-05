package ru.msu.cmc.webprac.web;

import org.hibernate.Session;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.msu.cmc.webprac.enums.OrderStatus;
import ru.msu.cmc.webprac.enums.TransmissionType;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class DashboardController extends ApiSupport {

    @GetMapping(value = "/api/dashboard", produces = JSON)
    public ResponseEntity<String> dashboard() {
        try (Session session = openSession()) {
            Map<String, Object> dto = new LinkedHashMap<>();
            dto.put("cars", session.createQuery("select count(c) from Car c", Long.class).uniqueResult());
            dto.put("brands", session.createQuery("select count(b) from CarBrand b", Long.class).uniqueResult());
            dto.put("clients", session.createQuery("select count(c) from Client c", Long.class).uniqueResult());
            dto.put("orders", session.createQuery("select count(o) from Order o", Long.class).uniqueResult());
            dto.put("testDrives", session.createQuery("select count(td) from TestDrive td", Long.class).uniqueResult());
            dto.put("orderStatuses", Arrays.asList(OrderStatus.values()));
            dto.put("transmissionTypes", Arrays.asList(TransmissionType.values()));
            return ok(dto);
        }
    }
}
