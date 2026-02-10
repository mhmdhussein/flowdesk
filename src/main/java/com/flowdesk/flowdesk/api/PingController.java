package com.flowdesk.flowdesk.api;

import com.flowdesk.flowdesk.api.dto.PingRequest;
import com.flowdesk.flowdesk.api.dto.PingResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/ping", produces = MediaType.APPLICATION_JSON_VALUE)
public class PingController {

    @GetMapping
    public PingResponse get() {
        return new PingResponse("pong");
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public PingResponse post(@Valid @RequestBody PingRequest request) {
        // just echo a normalized "pong" for now
        return new PingResponse("pong:" + request.message().trim());
    }

    @GetMapping("/secure")
    public PingResponse secure(org.springframework.security.core.Authentication auth) {
        return new PingResponse("secure pong for " + auth.getName());
    }
}
