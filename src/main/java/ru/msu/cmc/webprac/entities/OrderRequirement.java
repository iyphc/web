package ru.msu.cmc.webprac.entities;

import ru.msu.cmc.webprac.enums.TransmissionType;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "order_requirements")
public class OrderRequirement {

    @Id
    @Column(name = "order_id")
    private Long orderId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "desired_brand_id")
    private CarBrand desiredBrand;

    @Column(name = "desired_engine_volume_min", precision = 3, scale = 1)
    private BigDecimal desiredEngineVolumeMin;

    @Column(name = "desired_engine_power_min")
    private Integer desiredEnginePowerMin;

    @Enumerated(EnumType.STRING)
    @Column(name = "desired_transmission_type", length = 16)
    private TransmissionType desiredTransmissionType;

    @Column(name = "desired_required_fuel", length = 20)
    private String desiredRequiredFuel;

    @Column(name = "desired_color", length = 50)
    private String desiredColor;

    @Column(name = "desired_price_max", precision = 12, scale = 2)
    private BigDecimal desiredPriceMax;

    @Column(name = "comment_text", length = 65535)
    private String commentText;

    public OrderRequirement() {}

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public CarBrand getDesiredBrand() { return desiredBrand; }
    public void setDesiredBrand(CarBrand desiredBrand) { this.desiredBrand = desiredBrand; }

    public BigDecimal getDesiredEngineVolumeMin() { return desiredEngineVolumeMin; }
    public void setDesiredEngineVolumeMin(BigDecimal v) { this.desiredEngineVolumeMin = v; }

    public Integer getDesiredEnginePowerMin() { return desiredEnginePowerMin; }
    public void setDesiredEnginePowerMin(Integer v) { this.desiredEnginePowerMin = v; }

    public TransmissionType getDesiredTransmissionType() { return desiredTransmissionType; }
    public void setDesiredTransmissionType(TransmissionType v) { this.desiredTransmissionType = v; }

    public String getDesiredRequiredFuel() { return desiredRequiredFuel; }
    public void setDesiredRequiredFuel(String v) { this.desiredRequiredFuel = v; }

    public String getDesiredColor() { return desiredColor; }
    public void setDesiredColor(String v) { this.desiredColor = v; }

    public BigDecimal getDesiredPriceMax() { return desiredPriceMax; }
    public void setDesiredPriceMax(BigDecimal v) { this.desiredPriceMax = v; }

    public String getCommentText() { return commentText; }
    public void setCommentText(String commentText) { this.commentText = commentText; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderRequirement)) return false;
        OrderRequirement that = (OrderRequirement) o;
        return Objects.equals(orderId, that.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }
}
