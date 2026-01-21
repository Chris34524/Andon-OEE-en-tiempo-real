package com.ldr.andon;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "andon_event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String station;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 40)
    private EventType eventType;

    @Column(nullable = false, length = 20)
    private String status; // "OPEN" / "CLOSED"

    @Column(name = "started_at", nullable = false, columnDefinition = "timestamptz")
    private OffsetDateTime startedAt;

    @Column(name = "ended_at", columnDefinition = "timestamptz")
    private OffsetDateTime endedAt;

    @Column(nullable = false)
    private Integer quantity;

    @Column(length = 255)
    private String note;

    public Event() {}

    // ----- Getters / Setters -----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStation() { return station; }
    public void setStation(String station) { this.station = station; }

    public EventType getEventType() { return eventType; }
    public void setEventType(EventType eventType) { this.eventType = eventType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public OffsetDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(OffsetDateTime startedAt) { this.startedAt = startedAt; }

    public OffsetDateTime getEndedAt() { return endedAt; }
    public void setEndedAt(OffsetDateTime endedAt) { this.endedAt = endedAt; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
