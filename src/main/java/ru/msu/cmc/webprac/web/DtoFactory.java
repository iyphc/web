package ru.msu.cmc.webprac.web;

import ru.msu.cmc.webprac.entities.Car;
import ru.msu.cmc.webprac.entities.CarBrand;
import ru.msu.cmc.webprac.entities.Client;
import ru.msu.cmc.webprac.entities.Order;
import ru.msu.cmc.webprac.entities.OrderRequirement;
import ru.msu.cmc.webprac.entities.TestDrive;

import java.util.LinkedHashMap;
import java.util.Map;

final class DtoFactory {

    private DtoFactory() {
    }

    static Map<String, Object> brand(CarBrand brand) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", brand.getBrandId());
        dto.put("brandName", brand.getBrandName());
        dto.put("manufacturerName", brand.getManufacturerName());
        return dto;
    }

    static Map<String, Object> car(Car car) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", car.getCarId());
        dto.put("brand", brand(car.getBrand()));
        dto.put("registrationNumber", car.getRegistrationNumber());
        dto.put("engineVolumeL", car.getEngineVolumeL());
        dto.put("enginePowerHp", car.getEnginePowerHp());
        dto.put("fuelConsumptionL100km", car.getFuelConsumptionL100km());
        dto.put("doorsCount", car.getDoorsCount());
        dto.put("seatsCount", car.getSeatsCount());
        dto.put("trunkCapacityL", car.getTrunkCapacityL());
        dto.put("transmissionType", car.getTransmissionType());
        dto.put("hasCruiseControl", car.getHasCruiseControl());
        dto.put("requiredFuel", car.getRequiredFuel());
        dto.put("hasAirConditioner", car.getHasAirConditioner());
        dto.put("hasRadio", car.getHasRadio());
        dto.put("hasVideoSystem", car.getHasVideoSystem());
        dto.put("hasGps", car.getHasGps());
        dto.put("interiorTrim", car.getInteriorTrim());
        dto.put("color", car.getColor());
        dto.put("mileageKm", car.getMileageKm());
        dto.put("lastServiceDate", car.getLastServiceDate());
        dto.put("price", car.getPrice());
        return dto;
    }

    static Map<String, Object> client(Client client) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", client.getClientId());
        dto.put("fullName", client.getFullName());
        dto.put("address", client.getAddress());
        dto.put("phone", client.getPhone());
        dto.put("email", client.getEmail());
        return dto;
    }

    static Map<String, Object> order(Order order) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", order.getOrderId());
        dto.put("orderedAt", order.getOrderedAt());
        dto.put("client", client(order.getClient()));
        dto.put("car", order.getCar() == null ? null : car(order.getCar()));
        dto.put("needTestDrive", order.getNeedTestDrive());
        dto.put("status", order.getStatus());
        dto.put("requirements", order.getRequirements() == null ? null : requirement(order.getRequirements()));
        return dto;
    }

    static Map<String, Object> requirement(OrderRequirement requirement) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("orderId", requirement.getOrderId());
        dto.put("desiredBrand", requirement.getDesiredBrand() == null ? null : brand(requirement.getDesiredBrand()));
        dto.put("desiredEngineVolumeMin", requirement.getDesiredEngineVolumeMin());
        dto.put("desiredEnginePowerMin", requirement.getDesiredEnginePowerMin());
        dto.put("desiredTransmissionType", requirement.getDesiredTransmissionType());
        dto.put("desiredRequiredFuel", requirement.getDesiredRequiredFuel());
        dto.put("desiredColor", requirement.getDesiredColor());
        dto.put("desiredPriceMax", requirement.getDesiredPriceMax());
        dto.put("commentText", requirement.getCommentText());
        return dto;
    }

    static Map<String, Object> testDrive(TestDrive testDrive) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", testDrive.getTestDriveId());
        dto.put("client", client(testDrive.getClient()));
        dto.put("car", car(testDrive.getCar()));
        dto.put("testDriveAt", testDrive.getTestDriveAt());
        dto.put("notes", testDrive.getNotes());
        return dto;
    }
}
