package ru.msu.cmc.webprac.dao;

import ru.msu.cmc.webprac.entities.Car;
import ru.msu.cmc.webprac.entities.CarBrand;
import ru.msu.cmc.webprac.enums.TransmissionType;

import java.math.BigDecimal;
import java.util.List;

public interface CarDAO extends GenericDAO<Car> {
    Car getByRegistrationNumber(String registrationNumber);
    List<Car> getByBrand(CarBrand brand);
    List<Car> getByFilter(CarFilter filter);

    class CarFilter {
        private CarBrand brand;
        private BigDecimal minPrice;
        private BigDecimal maxPrice;
        private TransmissionType transmissionType;
        private String color;
        private Integer minEnginePower;
        private BigDecimal minEngineVolume;
        private String requiredFuel;

        public CarBrand getBrand() { return brand; }
        public void setBrand(CarBrand brand) { this.brand = brand; }
        public BigDecimal getMinPrice() { return minPrice; }
        public void setMinPrice(BigDecimal minPrice) { this.minPrice = minPrice; }
        public BigDecimal getMaxPrice() { return maxPrice; }
        public void setMaxPrice(BigDecimal maxPrice) { this.maxPrice = maxPrice; }
        public TransmissionType getTransmissionType() { return transmissionType; }
        public void setTransmissionType(TransmissionType transmissionType) { this.transmissionType = transmissionType; }
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
        public Integer getMinEnginePower() { return minEnginePower; }
        public void setMinEnginePower(Integer minEnginePower) { this.minEnginePower = minEnginePower; }
        public BigDecimal getMinEngineVolume() { return minEngineVolume; }
        public void setMinEngineVolume(BigDecimal minEngineVolume) { this.minEngineVolume = minEngineVolume; }
        public String getRequiredFuel() { return requiredFuel; }
        public void setRequiredFuel(String requiredFuel) { this.requiredFuel = requiredFuel; }
    }
}
