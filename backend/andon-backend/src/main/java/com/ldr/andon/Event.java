package com.ldr.andon;

public class Event {

    private EventType type;
    private int quantity;

    public Event() {
    }

    public Event(EventType type, int quantity) {
        this.type = type;
        this.quantity = quantity;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
