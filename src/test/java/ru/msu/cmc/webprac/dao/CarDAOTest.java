package ru.msu.cmc.webprac.dao;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.msu.cmc.webprac.dao.impl.CarBrandDAOImpl;
import ru.msu.cmc.webprac.dao.impl.CarDAOImpl;
import ru.msu.cmc.webprac.entities.Car;
import ru.msu.cmc.webprac.entities.CarBrand;
import ru.msu.cmc.webprac.enums.TransmissionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.testng.Assert.*;

public class CarDAOTest extends BaseDAOTest {

    private CarDAO carDAO;
    private CarBrandDAO brandDAO;
    private CarBrand toyota;
    private CarBrand bmw;

    @BeforeMethod
    public void setUp() {
        brandDAO = new CarBrandDAOImpl();
        carDAO = new CarDAOImpl();

        toyota = new CarBrand("Toyota", "Toyota Motor Corporation");
        bmw = new CarBrand("BMW", "Bayerische Motoren Werke AG");
        brandDAO.save(toyota);
        brandDAO.save(bmw);

        Car car1 = buildCar(toyota, "A123BC77", TransmissionType.AT, "AI-95",
                new BigDecimal("2.0"), 150, "White", 25000, new BigDecimal("3200000"));
        Car car2 = buildCar(bmw, "B456CD77", TransmissionType.AMT, "AI-98",
                new BigDecimal("3.0"), 245, "Black", 12000, new BigDecimal("6100000"));
        Car car3 = buildCar(toyota, "C789EF16", TransmissionType.MT, "AI-92",
                new BigDecimal("1.6"), 106, "Blue", 42000, new BigDecimal("1450000"));
        carDAO.save(car1);
        carDAO.save(car2);
        carDAO.save(car3);
    }

    @AfterMethod
    public void tearDown() {
        carDAO.getAll().forEach(carDAO::delete);
        brandDAO.getAll().forEach(brandDAO::delete);
    }

    private Car buildCar(CarBrand brand, String regNum, TransmissionType tt,
                         String fuel, BigDecimal volume, int power,
                         String color, int mileage, BigDecimal price) {
        Car c = new Car();
        c.setBrand(brand);
        c.setRegistrationNumber(regNum);
        c.setTransmissionType(tt);
        c.setRequiredFuel(fuel);
        c.setEngineVolumeL(volume);
        c.setEnginePowerHp(power);
        c.setColor(color);
        c.setMileageKm(mileage);
        c.setPrice(price);
        c.setHasCruiseControl(true);
        c.setHasAirConditioner(true);
        c.setHasRadio(true);
        c.setHasVideoSystem(false);
        c.setHasGps(false);
        c.setDoorsCount((short) 4);
        c.setSeatsCount((short) 5);
        c.setTrunkCapacityL(480);
        c.setFuelConsumptionL100km(new BigDecimal("8.0"));
        c.setLastServiceDate(LocalDate.of(2026, 2, 10));
        return c;
    }

    @Test
    public void testGetAll() {
        assertEquals(carDAO.getAll().size(), 3);
    }

    @Test
    public void testGetById_found() {
        Car any = carDAO.getAll().get(0);
        Car found = carDAO.getById(any.getCarId());
        assertNotNull(found);
        assertEquals(found.getCarId(), any.getCarId());
        assertEquals(found.getRegistrationNumber(), any.getRegistrationNumber());
    }

    @Test
    public void testGetById_notFound() {
        assertNull(carDAO.getById(-999L));
    }

    @Test
    public void testGetByRegistrationNumber_found() {
        Car found = carDAO.getByRegistrationNumber("A123BC77");
        assertNotNull(found);
        assertEquals(found.getRegistrationNumber(), "A123BC77");
        assertEquals(found.getColor(), "White");
        assertEquals(found.getEnginePowerHp().intValue(), 150);
        assertEquals(found.getTransmissionType(), TransmissionType.AT);
    }

    @Test
    public void testGetByRegistrationNumber_notFound() {
        assertNull(carDAO.getByRegistrationNumber("ZZZZZ"));
    }

    @Test
    public void testGetByBrand() {
        List<Car> toyotaCars = carDAO.getByBrand(toyota);
        assertEquals(toyotaCars.size(), 2);
        for (Car c : toyotaCars) {
            assertEquals(c.getBrand().getBrandId(), toyota.getBrandId());
        }
    }

    @Test
    public void testGetByBrand_empty() {
        CarBrand empty = new CarBrand("Fake", "FakeInc");
        brandDAO.save(empty);
        List<Car> cars = carDAO.getByBrand(empty);
        assertTrue(cars.isEmpty());
    }

    @Test
    public void testGetByFilter_brand() {
        CarDAO.CarFilter f = new CarDAO.CarFilter();
        f.setBrand(toyota);
        List<Car> result = carDAO.getByFilter(f);
        assertEquals(result.size(), 2);
    }

    @Test
    public void testGetByFilter_priceRange() {
        CarDAO.CarFilter f = new CarDAO.CarFilter();
        f.setMinPrice(new BigDecimal("2000000"));
        f.setMaxPrice(new BigDecimal("4000000"));
        List<Car> result = carDAO.getByFilter(f);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getRegistrationNumber(), "A123BC77");
    }

    @Test
    public void testGetByFilter_transmission() {
        CarDAO.CarFilter f = new CarDAO.CarFilter();
        f.setTransmissionType(TransmissionType.MT);
        List<Car> result = carDAO.getByFilter(f);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getTransmissionType(), TransmissionType.MT);
    }

    @Test
    public void testGetByFilter_color() {
        CarDAO.CarFilter f = new CarDAO.CarFilter();
        f.setColor("black");
        List<Car> result = carDAO.getByFilter(f);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getColor(), "Black");
    }

    @Test
    public void testGetByFilter_enginePower() {
        CarDAO.CarFilter f = new CarDAO.CarFilter();
        f.setMinEnginePower(200);
        List<Car> result = carDAO.getByFilter(f);
        assertEquals(result.size(), 1);
        assertTrue(result.get(0).getEnginePowerHp() >= 200);
    }

    @Test
    public void testGetByFilter_noResults() {
        CarDAO.CarFilter f = new CarDAO.CarFilter();
        f.setColor("pink");
        List<Car> result = carDAO.getByFilter(f);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetByFilter_engineVolume() {
        CarDAO.CarFilter f = new CarDAO.CarFilter();
        f.setMinEngineVolume(new BigDecimal("2.5"));
        List<Car> result = carDAO.getByFilter(f);
        assertEquals(result.size(), 1);
        assertTrue(result.get(0).getEngineVolumeL().compareTo(new BigDecimal("2.5")) >= 0);
    }

    @Test
    public void testGetByFilter_requiredFuel() {
        CarDAO.CarFilter f = new CarDAO.CarFilter();
        f.setRequiredFuel("AI-95");
        List<Car> result = carDAO.getByFilter(f);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getRequiredFuel(), "AI-95");
    }

    @Test
    public void testGetByFilter_multipleFilters() {
        CarDAO.CarFilter f = new CarDAO.CarFilter();
        f.setBrand(toyota);
        f.setMinEnginePower(100);
        f.setMaxPrice(new BigDecimal("2000000"));
        List<Car> result = carDAO.getByFilter(f);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getRegistrationNumber(), "C789EF16");
    }

    @Test
    public void testUpdate() {
        Car car = carDAO.getByRegistrationNumber("A123BC77");
        assertNotNull(car);
        car.setMileageKm(30000);
        car.setPrice(new BigDecimal("3000000"));
        carDAO.update(car);

        Car updated = carDAO.getById(car.getCarId());
        assertEquals(updated.getMileageKm().intValue(), 30000);
        assertEquals(updated.getPrice().compareTo(new BigDecimal("3000000")), 0);
    }

    @Test
    public void testDelete() {
        Car car = carDAO.getByRegistrationNumber("B456CD77");
        assertNotNull(car);
        carDAO.delete(car);
        assertNull(carDAO.getByRegistrationNumber("B456CD77"));
        assertEquals(carDAO.getAll().size(), 2);
    }
}
