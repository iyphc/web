package ru.msu.cmc.webprac.entities;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "car_brands")
public class CarBrand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "brand_id")
    private Long brandId;

    @Column(name = "brand_name", nullable = false, length = 100)
    private String brandName;

    @Column(name = "manufacturer_name", nullable = false, length = 120)
    private String manufacturerName;

    @OneToMany(mappedBy = "brand")
    private Set<Car> cars = new HashSet<>();

    public CarBrand() {}

    public CarBrand(String brandName, String manufacturerName) {
        this.brandName = brandName;
        this.manufacturerName = manufacturerName;
    }

    public Long getBrandId() { return brandId; }
    public void setBrandId(Long brandId) { this.brandId = brandId; }

    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }

    public String getManufacturerName() { return manufacturerName; }
    public void setManufacturerName(String manufacturerName) { this.manufacturerName = manufacturerName; }

    public Set<Car> getCars() { return cars; }
    public void setCars(Set<Car> cars) { this.cars = cars; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CarBrand)) return false;
        CarBrand that = (CarBrand) o;
        return Objects.equals(brandId, that.brandId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(brandId);
    }
}
