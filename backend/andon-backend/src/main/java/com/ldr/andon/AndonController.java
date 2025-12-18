package com.ldr.andon;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AndonController {

    private final EventService eventService;

    public AndonController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/events/mock")
    public List<Event> loadMockEvents() {
        eventService.seedMockEvents();
        return eventService.getEvents();
    }

    @GetMapping("/oee/summary")
    public OeeSummary getOeeSummary() {
        return eventService.calculateOee();
    }
}
