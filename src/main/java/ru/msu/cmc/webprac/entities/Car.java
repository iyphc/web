package ru.msu.cmc.webprac.entities;

import ru.msu.cmc.webprac.enums.TransmissionType;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "car_id")
    private Long carId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private CarBrand brand;

    @Column(name = "registration_number", nullable = false, unique = true, length = 20)
    private String registrationNumber;

    @Column(name = "engine_volume_l", nullable = false, precision = 3, scale = 1)
    private BigDecimal engineVolumeL;

    @Column(name = "engine_power_hp", nullable = false)
    private Integer enginePowerHp;

    @Column(name = "fuel_consumption_l_100km", precision = 4, scale = 1)
    private BigDecimal fuelConsumptionL100km;

    @Column(name = "doors_count")
    private Short doorsCount;

    @Column(name = "seats_count")
    private Short seatsCount;

    @Column(name = "trunk_capacity_l")
    private Integer trunkCapacityL;

    @Enumerated(EnumType.STRING)
    @Column(name = "transmission_type", nullable = false, length = 16)
    private TransmissionType transmissionType;

    @Column(name = "has_cruise_control", nullable = false)
    private Boolean hasCruiseControl;

    @Column(name = "required_fuel", nullable = false, length = 20)
    private String requiredFuel;

    @Column(name = "has_air_conditioner", nullable = false)
    private Boolean hasAirConditioner;

    @Column(name = "has_radio", nullable = false)
    private Boolean hasRadio;

    @Column(name = "has_video_system", nullable = false)
    private Boolean hasVideoSystem;

    @Column(name = "has_gps", nullable = false)
    private Boolean hasGps;

    @Column(name = "interior_trim", length = 80)
    private String interiorTrim;

    @Column(name = "color", length = 50)
    private String color;

    @Column(name = "mileage_km", nullable = false)
    private Integer mileageKm;

    @Column(name = "last_service_date")
    private LocalDate lastServiceDate;

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @OneToMany(mappedBy = "car")
    private Set<TestDrive> testDrives = new HashSet<>();

    public Car() {}

    public Long getCarId() { return carId; }
    public void setCarId(Long carId) { this.carId = carId; }

    public CarBrand getBrand() { return brand; }
    public void setBrand(CarBrand brand) { this.brand = brand; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public BigDecimal getEngineVolumeL() { return engineVolumeL; }
    public void setEngineVolumeL(BigDecimal engineVolumeL) { this.engineVolumeL = engineVolumeL; }

    public Integer getEnginePowerHp() { return enginePowerHp; }
    public void setEnginePowerHp(Integer enginePowerHp) { this.enginePowerHp = enginePowerHp; }

    public BigDecimal getFuelConsumptionL100km() { return fuelConsumptionL100km; }
    public void setFuelConsumptionL100km(BigDecimal fuelConsumptionL100km) { this.fuelConsumptionL100km = fuelConsumptionL100km; }

    public Short getDoorsCount() { return doorsCount; }
    public void setDoorsCount(Short doorsCount) { this.doorsCount = doorsCount; }

    public Short getSeatsCount() { return seatsCount; }
    public void setSeatsCount(Short seatsCount) { this.seatsCount = seatsCount; }

    public Integer getTrunkCapacityL() { return trunkCapacityL; }
    public void setTrunkCapacityL(Integer trunkCapacityL) { this.trunkCapacityL = trunkCapacityL; }

    public TransmissionType getTransmissionType() { return transmissionType; }
    public void setTransmissionType(TransmissionType transmissionType) { this.transmissionType = transmissionType; }

    public Boolean getHasCruiseControl() { return hasCruiseControl; }
    public void setHasCruiseControl(Boolean hasCruiseControl) { this.hasCruiseControl = hasCruiseControl; }

    public String getRequiredFuel() { return requiredFuel; }
    public void setRequiredFuel(String requiredFuel) { this.requiredFuel = requiredFuel; }

    public Boolean getHasAirConditioner() { return hasAirConditioner; }
    public void setHasAirConditioner(Boolean hasAirConditioner) { this.hasAirConditioner = hasAirConditioner; }

    public Boolean getHasRadio() { return hasRadio; }
    public void setHasRadio(Boolean hasRadio) { this.hasRadio = hasRadio; }

    public Boolean getHasVideoSystem() { return hasVideoSystem; }
    public void setHasVideoSystem(Boolean hasVideoSystem) { this.hasVideoSystem = hasVideoSystem; }

    public Boolean getHasGps() { return hasGps; }
    public void setHasGps(Boolean hasGps) { this.hasGps = hasGps; }

    public String getInteriorTrim() { return interiorTrim; }
    public void setInteriorTrim(String interiorTrim) { this.interiorTrim = interiorTrim; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public Integer getMileageKm() { return mileageKm; }
    public void setMileageKm(Integer mileageKm) { this.mileageKm = mileageKm; }

    public LocalDate getLastServiceDate() { return lastServiceDate; }
    public void setLastServiceDate(LocalDate lastServiceDate) { this.lastServiceDate = lastServiceDate; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Set<TestDrive> getTestDrives() { return testDrives; }
    public void setTestDrives(Set<TestDrive> testDrives) { this.testDrives = testDrives; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Car)) return false;
        Car that = (Car) o;
        return Objects.equals(carId, that.carId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(carId);
    }
}
