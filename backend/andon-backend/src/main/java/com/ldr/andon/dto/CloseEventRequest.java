package com.ldr.andon.dto;

import java.time.OffsetDateTime;

public class CloseEventRequest {
    private String note;
    private OffsetDateTime endedAt;

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public OffsetDateTime getEndedAt() { return endedAt; }
    public void setEndedAt(OffsetDateTime endedAt) { this.endedAt = endedAt; }
}
