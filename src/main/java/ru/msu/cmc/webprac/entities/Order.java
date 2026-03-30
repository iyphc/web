package ru.msu.cmc.webprac.entities;

import ru.msu.cmc.webprac.enums.OrderStatus;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "ordered_at", nullable = false)
    private LocalDateTime orderedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id")
    private Car car;

    @Column(name = "need_test_drive", nullable = false)
    private Boolean needTestDrive;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 24)
    private OrderStatus status;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true,
              fetch = FetchType.LAZY)
    private OrderRequirement requirements;

    public Order() {}

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public LocalDateTime getOrderedAt() { return orderedAt; }
    public void setOrderedAt(LocalDateTime orderedAt) { this.orderedAt = orderedAt; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public Car getCar() { return car; }
    public void setCar(Car car) { this.car = car; }

    public Boolean getNeedTestDrive() { return needTestDrive; }
    public void setNeedTestDrive(Boolean needTestDrive) { this.needTestDrive = needTestDrive; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public OrderRequirement getRequirements() { return requirements; }
    public void setRequirements(OrderRequirement requirements) {
        this.requirements = requirements;
        if (requirements != null) {
            requirements.setOrder(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order that = (Order) o;
        return Objects.equals(orderId, that.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }
}
