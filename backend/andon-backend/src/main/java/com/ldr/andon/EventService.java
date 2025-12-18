package com.ldr.andon;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EventService {

    private final List<Event> events = new ArrayList<>();

    public void clearEvents() {
        events.clear();
    }

    public void addEvent(Event event) {
        events.add(event);
    }

    public List<Event> getEvents() {
        return events;
    }

    /**
     * Carga algunos eventos de ejemplo para un turno.
     * (simula producción y piezas buenas/malas)
     */
    public void seedMockEvents() {
        events.clear();

        // Simulamos que la línea corre, produce piezas buenas y algunas malas
        events.add(new Event(EventType.RUN, 0));
        events.add(new Event(EventType.GOOD_PART, 80));
        events.add(new Event(EventType.BAD_PART, 5));
        events.add(new Event(EventType.GOOD_PART, 15));
        events.add(new Event(EventType.STOP, 0));
    }

    public OeeSummary calculateOee() {
        int goodParts = 0;
        int badParts = 0;

        for (Event event : events) {
            if (event.getType() == EventType.GOOD_PART) {
                goodParts += event.getQuantity();
            } else if (event.getType() == EventType.BAD_PART) {
                badParts += event.getQuantity();
            }
        }

        int totalParts = goodParts + badParts;

        // Parámetros simplificados (MVP)
        double plannedProduction = 120.0;    // piezas planificadas para el turno
        double plannedTimeMinutes = 480.0;   // 8 horas
        double runtimeMinutes = 400.0;       // suponer 400 min en RUN

        double availability = runtimeMinutes / plannedTimeMinutes;
        double performance = totalParts == 0 ? 0.0 : (double) totalParts / plannedProduction;
        double quality = totalParts == 0 ? 0.0 : (double) goodParts / totalParts;
        double oee = availability * performance * quality;

        OeeSummary summary = new OeeSummary();
        summary.setAvailability(availability);
        summary.setPerformance(performance);
        summary.setQuality(quality);
        summary.setOee(oee);

        return summary;
    }
}
