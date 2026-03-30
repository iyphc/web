package ru.msu.cmc.webprac.dao;

import ru.msu.cmc.webprac.entities.CarBrand;

import java.util.List;

public interface CarBrandDAO extends GenericDAO<CarBrand> {
    CarBrand getByName(String brandName);
    List<CarBrand> getByManufacturer(String manufacturerName);
}
