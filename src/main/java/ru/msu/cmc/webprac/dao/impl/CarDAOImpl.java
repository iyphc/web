package ru.msu.cmc.webprac.dao.impl;

import org.hibernate.Session;
import org.hibernate.query.Query;
import ru.msu.cmc.webprac.dao.CarDAO;
import ru.msu.cmc.webprac.entities.Car;
import ru.msu.cmc.webprac.entities.CarBrand;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarDAOImpl extends GenericDAOImpl<Car> implements CarDAO {

    public CarDAOImpl() {
        super(Car.class);
    }

    @Override
    public Car getByRegistrationNumber(String registrationNumber) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Car WHERE registrationNumber = :regNum", Car.class)
                    .setParameter("regNum", registrationNumber)
                    .uniqueResultOptional()
                    .orElse(null);
        }
    }

    @Override
    public List<Car> getByBrand(CarBrand brand) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Car c WHERE c.brand = :brand ORDER BY c.price", Car.class)
                    .setParameter("brand", brand)
                    .list();
        }
    }

    @Override
    public List<Car> getByFilter(CarFilter filter) {
        try (Session session = getSessionFactory().openSession()) {
            StringBuilder hql = new StringBuilder("FROM Car c WHERE 1=1");
            Map<String, Object> params = new HashMap<>();

            if (filter.getBrand() != null) {
                hql.append(" AND c.brand = :brand");
                params.put("brand", filter.getBrand());
            }
            if (filter.getMinPrice() != null) {
                hql.append(" AND c.price >= :minPrice");
                params.put("minPrice", filter.getMinPrice());
            }
            if (filter.getMaxPrice() != null) {
                hql.append(" AND c.price <= :maxPrice");
                params.put("maxPrice", filter.getMaxPrice());
            }
            if (filter.getTransmissionType() != null) {
                hql.append(" AND c.transmissionType = :transmission");
                params.put("transmission", filter.getTransmissionType());
            }
            if (filter.getColor() != null) {
                hql.append(" AND lower(c.color) = :color");
                params.put("color", filter.getColor().toLowerCase());
            }
            if (filter.getMinEnginePower() != null) {
                hql.append(" AND c.enginePowerHp >= :minPower");
                params.put("minPower", filter.getMinEnginePower());
            }
            if (filter.getMinEngineVolume() != null) {
                hql.append(" AND c.engineVolumeL >= :minVolume");
                params.put("minVolume", filter.getMinEngineVolume());
            }
            if (filter.getRequiredFuel() != null) {
                hql.append(" AND c.requiredFuel = :fuel");
                params.put("fuel", filter.getRequiredFuel());
            }

            hql.append(" ORDER BY c.price");

            Query<Car> query = session.createQuery(hql.toString(), Car.class);
            params.forEach(query::setParameter);
            return query.list();
        }
    }
}
