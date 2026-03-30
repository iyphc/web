package ru.msu.cmc.webprac.entities;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;

    @Column(name = "address", length = 300)
    private String address;

    @Column(name = "phone", nullable = false, unique = true, length = 32)
    private String phone;

    @Column(name = "email", unique = true, length = 120)
    private String email;

    @OneToMany(mappedBy = "client")
    private Set<Order> orders = new HashSet<>();

    @OneToMany(mappedBy = "client")
    private Set<TestDrive> testDrives = new HashSet<>();

    public Client() {}

    public Client(String fullName, String phone) {
        this.fullName = fullName;
        this.phone = phone;
    }

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Set<Order> getOrders() { return orders; }
    public void setOrders(Set<Order> orders) { this.orders = orders; }

    public Set<TestDrive> getTestDrives() { return testDrives; }
    public void setTestDrives(Set<TestDrive> testDrives) { this.testDrives = testDrives; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client)) return false;
        Client that = (Client) o;
        return Objects.equals(clientId, that.clientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId);
    }
}
