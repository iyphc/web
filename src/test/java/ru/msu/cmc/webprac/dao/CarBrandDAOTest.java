package ru.msu.cmc.webprac.dao;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.msu.cmc.webprac.dao.impl.CarBrandDAOImpl;
import ru.msu.cmc.webprac.entities.CarBrand;

import java.util.List;

import static org.testng.Assert.*;

public class CarBrandDAOTest extends BaseDAOTest {

    private CarBrandDAO dao;

    @BeforeMethod
    public void setUp() {
        dao = new CarBrandDAOImpl();
        dao.save(new CarBrand("Toyota", "Toyota Motor Corporation"));
        dao.save(new CarBrand("BMW", "Bayerische Motoren Werke AG"));
        dao.save(new CarBrand("LADA", "AvtoVAZ"));
    }

    @AfterMethod
    public void tearDown() {
        dao.getAll().forEach(dao::delete);
    }

    @Test
    public void testGetAll() {
        List<CarBrand> all = dao.getAll();
        assertEquals(all.size(), 3);
    }

    @Test
    public void testGetById_found() {
        CarBrand saved = dao.getAll().get(0);
        CarBrand found = dao.getById(saved.getBrandId());
        assertNotNull(found);
        assertEquals(found.getBrandId(), saved.getBrandId());
        assertEquals(found.getBrandName(), saved.getBrandName());
        assertEquals(found.getManufacturerName(), saved.getManufacturerName());
    }

    @Test
    public void testGetById_notFound() {
        CarBrand found = dao.getById(-999L);
        assertNull(found);
    }

    @Test
    public void testGetByName_found() {
        CarBrand found = dao.getByName("Toyota");
        assertNotNull(found);
        assertEquals(found.getBrandName(), "Toyota");
        assertEquals(found.getManufacturerName(), "Toyota Motor Corporation");
    }

    @Test
    public void testGetByName_notFound() {
        CarBrand found = dao.getByName("NonExistent");
        assertNull(found);
    }

    @Test
    public void testGetByManufacturer_found() {
        List<CarBrand> found = dao.getByManufacturer("toyota");
        assertEquals(found.size(), 1);
        assertEquals(found.get(0).getBrandName(), "Toyota");
    }

    @Test
    public void testGetByManufacturer_notFound() {
        List<CarBrand> found = dao.getByManufacturer("noone");
        assertTrue(found.isEmpty());
    }

    @Test
    public void testSaveAndGetById() {
        CarBrand brand = new CarBrand("Hyundai", "Hyundai Motor Company");
        dao.save(brand);
        assertNotNull(brand.getBrandId());

        CarBrand loaded = dao.getById(brand.getBrandId());
        assertNotNull(loaded);
        assertEquals(loaded.getBrandName(), "Hyundai");
        assertEquals(loaded.getManufacturerName(), "Hyundai Motor Company");
    }

    @Test
    public void testUpdate() {
        CarBrand brand = dao.getByName("Toyota");
        assertNotNull(brand);
        brand.setManufacturerName("TMC");
        dao.update(brand);

        CarBrand updated = dao.getById(brand.getBrandId());
        assertEquals(updated.getManufacturerName(), "TMC");
        assertEquals(updated.getBrandName(), "Toyota");
    }

    @Test
    public void testDelete() {
        CarBrand brand = dao.getByName("BMW");
        assertNotNull(brand);
        Long id = brand.getBrandId();

        dao.delete(brand);
        assertNull(dao.getById(id));
        assertEquals(dao.getAll().size(), 2);
    }

    @Test
    public void testDeleteById() {
        CarBrand brand = dao.getByName("LADA");
        assertNotNull(brand);
        Long id = brand.getBrandId();

        dao.deleteById(id);
        assertNull(dao.getById(id));
    }
}
