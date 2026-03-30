package ru.msu.cmc.webprac.dao;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.msu.cmc.webprac.dao.impl.ClientDAOImpl;
import ru.msu.cmc.webprac.dao.impl.CarBrandDAOImpl;
import ru.msu.cmc.webprac.dao.impl.CarDAOImpl;
import ru.msu.cmc.webprac.dao.impl.OrderDAOImpl;
import ru.msu.cmc.webprac.entities.*;
import ru.msu.cmc.webprac.enums.OrderStatus;
import ru.msu.cmc.webprac.enums.TransmissionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.testng.Assert.*;

public class ClientDAOTest extends BaseDAOTest {

    private ClientDAO clientDAO;
    private OrderDAO orderDAO;
    private CarDAO carDAO;
    private CarBrandDAO brandDAO;

    @BeforeMethod
    public void setUp() {
        clientDAO = new ClientDAOImpl();
        orderDAO = new OrderDAOImpl();
        carDAO = new CarDAOImpl();
        brandDAO = new CarBrandDAOImpl();

        Client c1 = new Client("Ivanov Ivan Ivanovich", "+79990000001");
        c1.setAddress("Moscow");
        c1.setEmail("ivanov@example.com");
        clientDAO.save(c1);

        Client c2 = new Client("Petrov Petr Sergeevich", "+79990000002");
        c2.setEmail("petrov@example.com");
        clientDAO.save(c2);

        Client c3 = new Client("Sidorova Anna Viktorovna", "+79990000003");
        c3.setEmail("sidorova@example.com");
        clientDAO.save(c3);

        // Create a brand+car and order for status filtering test
        CarBrand brand = new CarBrand("Toyota", "Toyota Motor Corporation");
        brandDAO.save(brand);

        Car car = new Car();
        car.setBrand(brand);
        car.setRegistrationNumber("TEST01");
        car.setEngineVolumeL(new BigDecimal("2.0"));
        car.setEnginePowerHp(150);
        car.setTransmissionType(TransmissionType.AT);
        car.setRequiredFuel("AI-95");
        car.setHasCruiseControl(false);
        car.setHasAirConditioner(false);
        car.setHasRadio(false);
        car.setHasVideoSystem(false);
        car.setHasGps(false);
        car.setMileageKm(0);
        car.setPrice(new BigDecimal("1000000"));
        car.setDoorsCount((short) 4);
        car.setSeatsCount((short) 5);
        car.setTrunkCapacityL(400);
        car.setFuelConsumptionL100km(new BigDecimal("7.0"));
        car.setLastServiceDate(LocalDate.of(2026, 1, 1));
        carDAO.save(car);

        Order o = new Order();
        o.setOrderedAt(LocalDateTime.of(2026, 2, 20, 11, 0));
        o.setClient(c1);
        o.setCar(car);
        o.setNeedTestDrive(true);
        o.setStatus(OrderStatus.IN_PROGRESS);
        orderDAO.save(o);
    }

    @AfterMethod
    public void tearDown() {
        orderDAO.getAll().forEach(orderDAO::delete);
        carDAO.getAll().forEach(carDAO::delete);
        brandDAO.getAll().forEach(brandDAO::delete);
        clientDAO.getAll().forEach(clientDAO::delete);
    }

    @Test
    public void testGetAll() {
        assertEquals(clientDAO.getAll().size(), 3);
    }

    @Test
    public void testGetById_found() {
        Client any = clientDAO.getAll().get(0);
        Client found = clientDAO.getById(any.getClientId());
        assertNotNull(found);
        assertEquals(found.getClientId(), any.getClientId());
        assertEquals(found.getFullName(), any.getFullName());
        assertEquals(found.getPhone(), any.getPhone());
    }

    @Test
    public void testGetById_notFound() {
        assertNull(clientDAO.getById(-999L));
    }

    @Test
    public void testGetByPhone_found() {
        Client found = clientDAO.getByPhone("+79990000001");
        assertNotNull(found);
        assertEquals(found.getFullName(), "Ivanov Ivan Ivanovich");
        assertEquals(found.getAddress(), "Moscow");
        assertEquals(found.getEmail(), "ivanov@example.com");
    }

    @Test
    public void testGetByPhone_notFound() {
        assertNull(clientDAO.getByPhone("+70000000000"));
    }

    @Test
    public void testGetByEmail_found() {
        Client found = clientDAO.getByEmail("petrov@example.com");
        assertNotNull(found);
        assertEquals(found.getFullName(), "Petrov Petr Sergeevich");
    }

    @Test
    public void testGetByEmail_notFound() {
        assertNull(clientDAO.getByEmail("unknown@example.com"));
    }

    @Test
    public void testGetByName_found() {
        List<Client> result = clientDAO.getByName("ivanov");
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getFullName(), "Ivanov Ivan Ivanovich");
    }

    @Test
    public void testGetByName_partial() {
        List<Client> result = clientDAO.getByName("ov");
        assertEquals(result.size(), 3); // Ivanov, Petrov, Sidorova
    }

    @Test
    public void testGetByName_notFound() {
        List<Client> result = clientDAO.getByName("zzzzzzz");
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetClientsByOrderStatus_found() {
        List<Client> result = clientDAO.getClientsByOrderStatus(OrderStatus.IN_PROGRESS);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getFullName(), "Ivanov Ivan Ivanovich");
    }

    @Test
    public void testGetClientsByOrderStatus_noMatch() {
        List<Client> result = clientDAO.getClientsByOrderStatus(OrderStatus.COMPLETED);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testUpdate() {
        Client c = clientDAO.getByPhone("+79990000001");
        assertNotNull(c);
        c.setAddress("Saint Petersburg");
        c.setEmail("ivanov_new@example.com");
        clientDAO.update(c);

        Client updated = clientDAO.getById(c.getClientId());
        assertEquals(updated.getAddress(), "Saint Petersburg");
        assertEquals(updated.getEmail(), "ivanov_new@example.com");
    }

    @Test
    public void testDelete() {
        Client c = clientDAO.getByPhone("+79990000003");
        assertNotNull(c);
        Long id = c.getClientId();
        clientDAO.delete(c);
        assertNull(clientDAO.getById(id));
        assertEquals(clientDAO.getAll().size(), 2);
    }
}
