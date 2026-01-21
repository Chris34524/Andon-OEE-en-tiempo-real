package com.ldr.andon;

import com.ldr.andon.dto.CloseEventRequest;
import com.ldr.andon.dto.CreateEventRequest;
import com.ldr.andon.dto.StationStateResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private static final String DEFAULT_STATION = "STATION-01";
    private static final List<EventType> STATE_TYPES = List.of(EventType.RUN, EventType.STOP);

    private final EventRepository repo;

    public EventService(EventRepository repo) {
        this.repo = repo;
    }

    // -------- CRUD real (BD) --------

    public Event createEvent(CreateEventRequest req) {
        if (req == null) throw new IllegalArgumentException("Body requerido");
        if (req.getEventType() == null) throw new IllegalArgumentException("eventType es requerido");

        String station = (req.getStation() == null || req.getStation().isBlank())
                ? DEFAULT_STATION
                : req.getStation().trim();

        OffsetDateTime start = (req.getStartedAt() != null) ? req.getStartedAt() : OffsetDateTime.now();
        EventType type = req.getEventType();

        int qty;
        if (type == EventType.GOOD_PART || type == EventType.BAD_PART) {
            qty = (req.getQuantity() == null || req.getQuantity() <= 0) ? 1 : req.getQuantity();
        } else {
            qty = (req.getQuantity() == null) ? 0 : Math.max(req.getQuantity(), 0);
        }

        // RUN/STOP: cerrar estado abierto anterior si es diferente
        if (type == EventType.RUN || type == EventType.STOP) {
            repo.findFirstByStationAndEventTypeInAndEndedAtIsNullOrderByStartedAtDesc(station, STATE_TYPES)
                    .ifPresent(open -> {
                        if (open.getEventType() != type) {
                            open.setEndedAt(start);
                            open.setStatus("CLOSED");
                            repo.save(open);
                        }
                    });
        }

        Event e = new Event();
        e.setStation(station);
        e.setEventType(type);
        e.setQuantity(qty);
        e.setNote(req.getNote());
        e.setStartedAt(start);

        // GOOD/BAD instantáneos; RUN/STOP abiertos
        if (type == EventType.GOOD_PART || type == EventType.BAD_PART) {
            e.setEndedAt(start);
            e.setStatus("CLOSED");
        } else {
            e.setEndedAt(null);
            e.setStatus("OPEN");
        }

        return repo.save(e);
    }

    public Event closeEvent(Long id, CloseEventRequest req) {
        Event e = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe event id=" + id));

        if ("CLOSED".equalsIgnoreCase(e.getStatus()) || e.getEndedAt() != null) {
            throw new IllegalArgumentException("El evento ya está cerrado id=" + id);
        }

        OffsetDateTime end = (req != null && req.getEndedAt() != null) ? req.getEndedAt() : OffsetDateTime.now();

        if (e.getStartedAt() != null && end.isBefore(e.getStartedAt())) {
            throw new IllegalArgumentException("endedAt no puede ser menor que startedAt");
        }

        e.setEndedAt(end);
        e.setStatus("CLOSED");

        if (req != null && req.getNote() != null && !req.getNote().isBlank()) {
            e.setNote(req.getNote());
        }

        return repo.save(e);
    }

    public List<Event> getEvents(Optional<String> station, OffsetDateTime from, OffsetDateTime to, int page, int size) {
        validateRange(from, to);

        String st = station.filter(s -> !s.isBlank()).orElse(null);
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 200));

        return repo.findOverlapping(st, from, to, pageable).getContent();
    }

    public void clearEvents() {
        repo.deleteAll();
    }

    // -------- Seed (datos demo) --------

    public int seedMockEvents() {
        repo.deleteAll();

        OffsetDateTime base = OffsetDateTime.now().minusHours(8);

        repo.save(buildTimed(DEFAULT_STATION, EventType.RUN, 0,
                base.plusMinutes(0), base.plusMinutes(120), "RUN 2h"));

        repo.save(buildTimed(DEFAULT_STATION, EventType.GOOD_PART, 80,
                base.plusMinutes(30), null, "80 buenas"));
        repo.save(buildTimed(DEFAULT_STATION, EventType.BAD_PART, 5,
                base.plusMinutes(50), null, "5 malas"));

        repo.save(buildTimed(DEFAULT_STATION, EventType.STOP, 0,
                base.plusMinutes(120), base.plusMinutes(150), "STOP 30m"));

        repo.save(buildTimed(DEFAULT_STATION, EventType.RUN, 0,
                base.plusMinutes(150), base.plusMinutes(400), "RUN 250m"));

        repo.save(buildTimed(DEFAULT_STATION, EventType.GOOD_PART, 15,
                base.plusMinutes(200), null, "15 buenas"));

        return (int) repo.count();
    }

    private Event buildTimed(String station, EventType type, int qty,
                             OffsetDateTime start, OffsetDateTime end, String note) {
        Event e = new Event();
        e.setStation(station);
        e.setEventType(type);
        e.setQuantity(qty);
        e.setStartedAt(start);
        e.setEndedAt(end);
        e.setNote(note);

        if (end != null) {
            e.setStatus("CLOSED");
        } else {
            if (type == EventType.GOOD_PART || type == EventType.BAD_PART) {
                e.setEndedAt(start);
                e.setStatus("CLOSED");
            } else {
                e.setStatus("OPEN");
            }
        }
        return e;
    }

    // -------- OEE (por rango) --------

    public OeeSummary calculateOee(Optional<String> stationOpt, OffsetDateTime from, OffsetDateTime to) {
        validateRange(from, to);

        String st = stationOpt.filter(s -> !s.isBlank()).orElse(null);

        List<Event> events = repo.findOverlapping(st, from, to, PageRequest.of(0, 5000)).getContent();

        int good = 0, bad = 0;
        long runMinutes = 0;

        for (Event e : events) {
            if (e.getStartedAt() != null && !e.getStartedAt().isBefore(from) && !e.getStartedAt().isAfter(to)) {
                if (e.getEventType() == EventType.GOOD_PART) good += e.getQuantity();
                if (e.getEventType() == EventType.BAD_PART) bad += e.getQuantity();
            }

            if (e.getEventType() == EventType.RUN && e.getStartedAt() != null) {
                OffsetDateTime s = e.getStartedAt().isBefore(from) ? from : e.getStartedAt();
                OffsetDateTime rawEnd = (e.getEndedAt() == null) ? to : e.getEndedAt();
                OffsetDateTime end = rawEnd.isAfter(to) ? to : rawEnd;

                if (end.isAfter(s)) {
                    runMinutes += Duration.between(s, end).toMinutes();
                }
            }
        }

        int total = good + bad;
        double plannedMinutes = Duration.between(from, to).toMinutes();

        double availability = plannedMinutes == 0 ? 0.0 : (runMinutes / plannedMinutes);
        double performance = 1.0; // MVP: documentado
        double quality = total == 0 ? 0.0 : ((double) good / total);
        double oee = availability * performance * quality;

        OeeSummary s = new OeeSummary();
        s.setAvailability(availability);
        s.setPerformance(performance);
        s.setQuality(quality);
        s.setOee(oee);
        return s;
    }

    // -------- Station State (Andon) --------

    public StationStateResponse getStationState(String station, long stopThresholdMinutes) {
        String st = (station == null || station.isBlank()) ? DEFAULT_STATION : station.trim();

        Event state = repo.findFirstByStationAndEventTypeInAndEndedAtIsNullOrderByStartedAtDesc(st, STATE_TYPES)
                .orElseGet(() -> repo.findFirstByStationAndEventTypeInOrderByStartedAtDesc(st, STATE_TYPES).orElse(null));

        if (state == null) {
            return new StationStateResponse(st, "UNKNOWN", "GRAY", null, null, null);
        }

        if (state.getEventType() == EventType.RUN) {
            return new StationStateResponse(st, "RUN", "GREEN", state.getStartedAt(), 0L, state.getId());
        }

        long stopMinutes = (state.getStartedAt() == null)
                ? 0
                : Duration.between(state.getStartedAt(), OffsetDateTime.now()).toMinutes();

        String color = (stopMinutes > stopThresholdMinutes) ? "RED" : "YELLOW";
        return new StationStateResponse(st, "STOP", color, state.getStartedAt(), stopMinutes, state.getId());
    }

    private void validateRange(OffsetDateTime from, OffsetDateTime to) {
        if (from == null || to == null) throw new IllegalArgumentException("from y to son requeridos");
        if (!to.isAfter(from)) throw new IllegalArgumentException("to debe ser mayor que from");
    }
}
