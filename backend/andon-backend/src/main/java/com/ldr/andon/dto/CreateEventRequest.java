package com.ldr.andon.dto;

import com.ldr.andon.EventType;
import java.time.OffsetDateTime;

public class CreateEventRequest {
    private String station;
    private EventType eventType;
    private Integer quantity;
    private String note;
    private OffsetDateTime startedAt;

    public String getStation() { return station; }
    public void setStation(String station) { this.station = station; }

    public EventType getEventType() { return eventType; }
    public void setEventType(EventType eventType) { this.eventType = eventType; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public OffsetDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(OffsetDateTime startedAt) { this.startedAt = startedAt; }
}
