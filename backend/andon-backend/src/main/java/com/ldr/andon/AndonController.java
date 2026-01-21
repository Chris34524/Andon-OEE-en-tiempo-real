package com.ldr.andon;

import com.ldr.andon.dto.CloseEventRequest;
import com.ldr.andon.dto.CreateEventRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AndonController {

    private final EventService service;

    public AndonController(EventService service) {
        this.service = service;
    }

    @PostMapping("/events")
    public ResponseEntity<Event> create(@RequestBody CreateEventRequest req) {
        Event saved = service.createEvent(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PatchMapping("/events/{id}/close")
    public Event close(@PathVariable Long id, @RequestBody(required = false) CloseEventRequest req) {
        return service.closeEvent(id, req);
    }

    @PostMapping("/events/seed")
    public Map<String, Object> seed() {
        int total = service.seedMockEvents();
        return Map.of("seeded", total);
    }

    @GetMapping("/events")
    public List<Event> list(
            @RequestParam(required = false) String station,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size
    ) {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime fromD = (from == null) ? now.minusHours(24) : from;
        OffsetDateTime toD = (to == null) ? now : to;

        return service.getEvents(Optional.ofNullable(station), fromD, toD, page, size);
    }

    @GetMapping("/stations/{station}/state")
    public com.ldr.andon.dto.StationStateResponse stationState(
            @PathVariable String station,
            @RequestParam(defaultValue = "10") long stopThresholdMinutes
    ) {
        return service.getStationState(station, stopThresholdMinutes);
    }


    @GetMapping("/oee")
    public OeeSummary oee(
            @RequestParam(required = false) String station,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to
    ) {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime fromD = (from == null) ? now.minusHours(24) : from;
        OffsetDateTime toD = (to == null) ? now : to;

        return service.calculateOee(Optional.ofNullable(station), fromD, toD);
    }
}
