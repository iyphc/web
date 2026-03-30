package ru.msu.cmc.webprac.dao;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.msu.cmc.webprac.dao.impl.*;
import ru.msu.cmc.webprac.entities.*;
import ru.msu.cmc.webprac.enums.TransmissionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.testng.Assert.*;

public class TestDriveDAOTest extends BaseDAOTest {

    private TestDriveDAO tdDAO;
    private ClientDAO clientDAO;
    private CarDAO carDAO;
    private CarBrandDAO brandDAO;

    private Client client1;
    private Client client2;
    private Car car1;
    private Car car2;

    @BeforeMethod
    public void setUp() {
        tdDAO = new TestDriveDAOImpl();
        clientDAO = new ClientDAOImpl();
        carDAO = new CarDAOImpl();
        brandDAO = new CarBrandDAOImpl();

        CarBrand brand = new CarBrand("Toyota", "TMC");
        brandDAO.save(brand);

        car1 = buildCar(brand, "TD_A01");
        car2 = buildCar(brand, "TD_A02");
        carDAO.save(car1);
        carDAO.save(car2);

        client1 = new Client("TD Client 1", "+79002220001");
        client1.setEmail("td1@example.com");
        client2 = new Client("TD Client 2", "+79002220002");
        client2.setEmail("td2@example.com");
        clientDAO.save(client1);
        clientDAO.save(client2);

        TestDrive td1 = makeTD(client1, car1, LocalDateTime.of(2026, 2, 20, 12, 0), "Great");
        TestDrive td2 = makeTD(client1, car2, LocalDateTime.of(2026, 2, 21, 14, 0), "Good");
        TestDrive td3 = makeTD(client2, car1, LocalDateTime.of(2026, 2, 22, 10, 0), "OK");
        tdDAO.save(td1);
        tdDAO.save(td2);
        tdDAO.save(td3);
    }

    @AfterMethod
    public void tearDown() {
        tdDAO.getAll().forEach(tdDAO::delete);
        carDAO.getAll().forEach(carDAO::delete);
        brandDAO.getAll().forEach(brandDAO::delete);
        clientDAO.getAll().forEach(clientDAO::delete);
    }

    private Car buildCar(CarBrand brand, String regNum) {
        Car c = new Car();
        c.setBrand(brand);
        c.setRegistrationNumber(regNum);
        c.setEngineVolumeL(new BigDecimal("2.0"));
        c.setEnginePowerHp(150);
        c.setTransmissionType(TransmissionType.AT);
        c.setRequiredFuel("AI-95");
        c.setHasCruiseControl(false);
        c.setHasAirConditioner(false);
        c.setHasRadio(false);
        c.setHasVideoSystem(false);
        c.setHasGps(false);
        c.setMileageKm(0);
        c.setPrice(new BigDecimal("1000000"));
        c.setDoorsCount((short) 4);
        c.setSeatsCount((short) 5);
        c.setTrunkCapacityL(400);
        c.setFuelConsumptionL100km(new BigDecimal("7.0"));
        c.setLastServiceDate(LocalDate.of(2026, 1, 1));
        return c;
    }

    private TestDrive makeTD(Client client, Car car, LocalDateTime at, String notes) {
        TestDrive td = new TestDrive();
        td.setClient(client);
        td.setCar(car);
        td.setTestDriveAt(at);
        td.setNotes(notes);
        return td;
    }

    @Test
    public void testGetAll() {
        assertEquals(tdDAO.getAll().size(), 3);
    }

    @Test
    public void testGetById_found() {
        TestDrive any = tdDAO.getAll().get(0);
        TestDrive found = tdDAO.getById(any.getTestDriveId());
        assertNotNull(found);
        assertEquals(found.getTestDriveId(), any.getTestDriveId());
        assertNotNull(found.getNotes());
    }

    @Test
    public void testGetById_notFound() {
        assertNull(tdDAO.getById(-999L));
    }

    @Test
    public void testGetByClient() {
        List<TestDrive> result = tdDAO.getByClient(client1);
        assertEquals(result.size(), 2);
        for (TestDrive td : result) {
            assertEquals(td.getClient().getClientId(), client1.getClientId());
        }
    }

    @Test
    public void testGetByClient_noResults() {
        Client lonely = new Client("NoTD", "+70000088888");
        clientDAO.save(lonely);
        List<TestDrive> result = tdDAO.getByClient(lonely);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetByCar() {
        List<TestDrive> result = tdDAO.getByCar(car1);
        assertEquals(result.size(), 2);
        for (TestDrive td : result) {
            assertEquals(td.getCar().getCarId(), car1.getCarId());
        }
    }

    @Test
    public void testGetByCar_noResults() {
        Car lonely = buildCar(car1.getBrand(), "TD_LONELY");
        carDAO.save(lonely);
        List<TestDrive> result = tdDAO.getByCar(lonely);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetByClientAndCar_found() {
        List<TestDrive> result = tdDAO.getByClientAndCar(client1, car1);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getClient().getClientId(), client1.getClientId());
        assertEquals(result.get(0).getCar().getCarId(), car1.getCarId());
        assertEquals(result.get(0).getNotes(), "Great");
    }

    @Test
    public void testGetByClientAndCar_notFound() {
        List<TestDrive> result = tdDAO.getByClientAndCar(client2, car2);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testSave() {
        TestDrive td = makeTD(client2, car2, LocalDateTime.of(2026, 3, 1, 10, 0), "New one");
        tdDAO.save(td);
        assertNotNull(td.getTestDriveId());

        TestDrive loaded = tdDAO.getById(td.getTestDriveId());
        assertNotNull(loaded);
        assertEquals(loaded.getNotes(), "New one");
        assertEquals(loaded.getClient().getClientId(), client2.getClientId());
        assertEquals(loaded.getCar().getCarId(), car2.getCarId());
    }

    @Test
    public void testUpdate() {
        TestDrive td = tdDAO.getByClient(client1).get(0);
        td.setNotes("Updated note");
        tdDAO.update(td);

        TestDrive updated = tdDAO.getById(td.getTestDriveId());
        assertEquals(updated.getNotes(), "Updated note");
    }

    @Test
    public void testDelete() {
        int before = tdDAO.getAll().size();
        TestDrive td = tdDAO.getAll().get(0);
        Long id = td.getTestDriveId();
        tdDAO.delete(td);
        assertNull(tdDAO.getById(id));
        assertEquals(tdDAO.getAll().size(), before - 1);
    }
}
