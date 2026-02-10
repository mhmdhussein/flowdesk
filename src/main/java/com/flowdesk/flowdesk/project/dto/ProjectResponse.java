package com.flowdesk.flowdesk.project.dto;

import java.time.Instant;

public record ProjectResponse(
        Long id,
        String name,
        Instant createdAt
) {}
