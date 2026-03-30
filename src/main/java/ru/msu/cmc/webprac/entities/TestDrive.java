package ru.msu.cmc.webprac.entities;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "test_drives")
public class TestDrive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "test_drive_id")
    private Long testDriveId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @Column(name = "test_drive_at", nullable = false)
    private LocalDateTime testDriveAt;

    @Column(name = "notes", length = 300)
    private String notes;

    public TestDrive() {}

    public Long getTestDriveId() { return testDriveId; }
    public void setTestDriveId(Long testDriveId) { this.testDriveId = testDriveId; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public Car getCar() { return car; }
    public void setCar(Car car) { this.car = car; }

    public LocalDateTime getTestDriveAt() { return testDriveAt; }
    public void setTestDriveAt(LocalDateTime testDriveAt) { this.testDriveAt = testDriveAt; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestDrive)) return false;
        TestDrive that = (TestDrive) o;
        return Objects.equals(testDriveId, that.testDriveId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(testDriveId);
    }
}
