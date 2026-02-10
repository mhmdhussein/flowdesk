package com.flowdesk.flowdesk.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PingRequest(
        @NotBlank(message = "message is required")
        @Size(max = 50, message = "message must be at most 50 characters")
        String message
) {}
