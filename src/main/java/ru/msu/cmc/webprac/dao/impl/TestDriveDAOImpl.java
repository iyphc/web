package ru.msu.cmc.webprac.dao.impl;

import org.hibernate.Session;
import ru.msu.cmc.webprac.dao.TestDriveDAO;
import ru.msu.cmc.webprac.entities.Car;
import ru.msu.cmc.webprac.entities.Client;
import ru.msu.cmc.webprac.entities.TestDrive;

import java.util.List;

public class TestDriveDAOImpl extends GenericDAOImpl<TestDrive> implements TestDriveDAO {

    public TestDriveDAOImpl() {
        super(TestDrive.class);
    }

    @Override
    public List<TestDrive> getByClient(Client client) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM TestDrive td WHERE td.client = :client ORDER BY td.testDriveAt DESC",
                            TestDrive.class)
                    .setParameter("client", client)
                    .list();
        }
    }

    @Override
    public List<TestDrive> getByCar(Car car) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM TestDrive td WHERE td.car = :car ORDER BY td.testDriveAt DESC",
                            TestDrive.class)
                    .setParameter("car", car)
                    .list();
        }
    }

    @Override
    public List<TestDrive> getByClientAndCar(Client client, Car car) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM TestDrive td WHERE td.client = :client AND td.car = :car " +
                                    "ORDER BY td.testDriveAt DESC",
                            TestDrive.class)
                    .setParameter("client", client)
                    .setParameter("car", car)
                    .list();
        }
    }
}
