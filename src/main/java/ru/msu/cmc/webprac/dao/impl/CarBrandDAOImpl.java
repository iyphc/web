package ru.msu.cmc.webprac.dao.impl;

import org.hibernate.Session;
import ru.msu.cmc.webprac.dao.CarBrandDAO;
import ru.msu.cmc.webprac.entities.CarBrand;

import java.util.List;

public class CarBrandDAOImpl extends GenericDAOImpl<CarBrand> implements CarBrandDAO {

    public CarBrandDAOImpl() {
        super(CarBrand.class);
    }

    @Override
    public CarBrand getByName(String brandName) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM CarBrand WHERE brandName = :name", CarBrand.class)
                    .setParameter("name", brandName)
                    .uniqueResultOptional()
                    .orElse(null);
        }
    }

    @Override
    public List<CarBrand> getByManufacturer(String manufacturerName) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM CarBrand WHERE lower(manufacturerName) LIKE :mfr ORDER BY brandName",
                            CarBrand.class)
                    .setParameter("mfr", "%" + manufacturerName.toLowerCase() + "%")
                    .list();
        }
    }
}
