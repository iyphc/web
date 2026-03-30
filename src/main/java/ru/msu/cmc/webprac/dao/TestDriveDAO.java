package ru.msu.cmc.webprac.dao;

import ru.msu.cmc.webprac.entities.Car;
import ru.msu.cmc.webprac.entities.Client;
import ru.msu.cmc.webprac.entities.TestDrive;

import java.util.List;

public interface TestDriveDAO extends GenericDAO<TestDrive> {
    List<TestDrive> getByClient(Client client);
    List<TestDrive> getByCar(Car car);
    List<TestDrive> getByClientAndCar(Client client, Car car);
}
