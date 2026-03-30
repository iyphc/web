package ru.msu.cmc.webprac.dao;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.msu.cmc.webprac.dao.impl.*;
import ru.msu.cmc.webprac.entities.*;
import ru.msu.cmc.webprac.enums.OrderStatus;
import ru.msu.cmc.webprac.enums.TransmissionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.testng.Assert.*;

public class OrderDAOTest extends BaseDAOTest {

    private OrderDAO orderDAO;
    private ClientDAO clientDAO;
    private CarDAO carDAO;
    private CarBrandDAO brandDAO;

    private Client client1;
    private Client client2;
    private Car car1;
    private Car car2;

    @BeforeMethod
    public void setUp() {
        orderDAO = new OrderDAOImpl();
        clientDAO = new ClientDAOImpl();
        carDAO = new CarDAOImpl();
        brandDAO = new CarBrandDAOImpl();

        CarBrand brand = new CarBrand("Toyota", "TMC");
        brandDAO.save(brand);

        car1 = buildCar(brand, "ORD_A01", new BigDecimal("2000000"));
        car2 = buildCar(brand, "ORD_A02", new BigDecimal("3000000"));
        carDAO.save(car1);
        carDAO.save(car2);

        client1 = new Client("Order Client 1", "+79001110001");
        client1.setEmail("ord1@example.com");
        client2 = new Client("Order Client 2", "+79001110002");
        client2.setEmail("ord2@example.com");
        clientDAO.save(client1);
        clientDAO.save(client2);

        Order o1 = makeOrder(client1, car1, OrderStatus.IN_PROGRESS, true,
                LocalDateTime.of(2026, 2, 20, 11, 0));
        Order o2 = makeOrder(client1, null, OrderStatus.WAITING_SUPPLY, false,
                LocalDateTime.of(2026, 2, 21, 14, 30));
        Order o3 = makeOrder(client2, car2, OrderStatus.COMPLETED, false,
                LocalDateTime.of(2026, 2, 22, 16, 45));
        orderDAO.save(o1);
        orderDAO.save(o2);
        orderDAO.save(o3);
    }

    @AfterMethod
    public void tearDown() {
        orderDAO.getAll().forEach(orderDAO::delete);
        carDAO.getAll().forEach(carDAO::delete);
        brandDAO.getAll().forEach(brandDAO::delete);
        clientDAO.getAll().forEach(clientDAO::delete);
    }

    private Car buildCar(CarBrand brand, String regNum, BigDecimal price) {
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
        c.setPrice(price);
        c.setDoorsCount((short) 4);
        c.setSeatsCount((short) 5);
        c.setTrunkCapacityL(400);
        c.setFuelConsumptionL100km(new BigDecimal("7.0"));
        c.setLastServiceDate(LocalDate.of(2026, 1, 1));
        return c;
    }

    private Order makeOrder(Client client, Car car, OrderStatus status,
                            boolean needTD, LocalDateTime at) {
        Order o = new Order();
        o.setClient(client);
        o.setCar(car);
        o.setStatus(status);
        o.setNeedTestDrive(needTD);
        o.setOrderedAt(at);
        return o;
    }

    @Test
    public void testGetAll() {
        assertEquals(orderDAO.getAll().size(), 3);
    }

    @Test
    public void testGetById_found() {
        Order any = orderDAO.getAll().get(0);
        Order found = orderDAO.getById(any.getOrderId());
        assertNotNull(found);
        assertEquals(found.getOrderId(), any.getOrderId());
        assertNotNull(found.getStatus());
    }

    @Test
    public void testGetById_notFound() {
        assertNull(orderDAO.getById(-999L));
    }

    @Test
    public void testGetByClient() {
        List<Order> orders = orderDAO.getByClient(client1);
        assertEquals(orders.size(), 2);
        for (Order o : orders) {
            assertEquals(o.getClient().getClientId(), client1.getClientId());
        }
    }

    @Test
    public void testGetByClient_noOrders() {
        Client lonely = new Client("Lonely", "+70000099999");
        clientDAO.save(lonely);
        List<Order> orders = orderDAO.getByClient(lonely);
        assertTrue(orders.isEmpty());
    }

    @Test
    public void testGetByCar() {
        List<Order> orders = orderDAO.getByCar(car1);
        assertEquals(orders.size(), 1);
        assertEquals(orders.get(0).getCar().getCarId(), car1.getCarId());
    }

    @Test
    public void testGetByStatus_found() {
        List<Order> result = orderDAO.getByStatus(OrderStatus.IN_PROGRESS);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getStatus(), OrderStatus.IN_PROGRESS);
        assertTrue(result.get(0).getNeedTestDrive());
    }

    @Test
    public void testGetByStatus_noMatch() {
        List<Order> result = orderDAO.getByStatus(OrderStatus.TEST_DRIVE);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testUpdateStatus() {
        Order o = orderDAO.getByStatus(OrderStatus.IN_PROGRESS).get(0);
        Long id = o.getOrderId();

        orderDAO.updateStatus(id, OrderStatus.IN_SHOWROOM);

        Order updated = orderDAO.getById(id);
        assertEquals(updated.getStatus(), OrderStatus.IN_SHOWROOM);
    }

    @Test
    public void testUpdate() {
        Order o = orderDAO.getByStatus(OrderStatus.COMPLETED).get(0);
        o.setNeedTestDrive(true);
        orderDAO.update(o);

        Order updated = orderDAO.getById(o.getOrderId());
        assertTrue(updated.getNeedTestDrive());
        assertEquals(updated.getStatus(), OrderStatus.COMPLETED);
    }

    @Test
    public void testDelete() {
        int before = orderDAO.getAll().size();
        Order o = orderDAO.getAll().get(0);
        orderDAO.delete(o);
        assertEquals(orderDAO.getAll().size(), before - 1);
    }

    @Test
    public void testOrderWithRequirements() {
        Order o = new Order();
        o.setClient(client2);
        o.setOrderedAt(LocalDateTime.now());
        o.setNeedTestDrive(false);
        o.setStatus(OrderStatus.IN_PROGRESS);

        OrderRequirement req = new OrderRequirement();
        req.setDesiredColor("Red");
        req.setDesiredPriceMax(new BigDecimal("5000000"));
        req.setDesiredTransmissionType(TransmissionType.CVT);
        req.setCommentText("Test comment");
        o.setRequirements(req);

        orderDAO.save(o);
        assertNotNull(o.getOrderId());

        Order loaded = orderDAO.getById(o.getOrderId());
        assertNotNull(loaded);
        assertNotNull(loaded.getRequirements());
        assertEquals(loaded.getRequirements().getDesiredColor(), "Red");
        assertEquals(loaded.getRequirements().getDesiredPriceMax().compareTo(new BigDecimal("5000000")), 0);
        assertEquals(loaded.getRequirements().getDesiredTransmissionType(), TransmissionType.CVT);
        assertEquals(loaded.getRequirements().getCommentText(), "Test comment");
    }
}
