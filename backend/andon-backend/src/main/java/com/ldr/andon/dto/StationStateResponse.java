package com.ldr.andon.dto;

import java.time.OffsetDateTime;

public record StationStateResponse(
        String station,
        String currentState,
        String andonColor,
        OffsetDateTime since,
        Long stopMinutes,
        Long openEventId
) {}
