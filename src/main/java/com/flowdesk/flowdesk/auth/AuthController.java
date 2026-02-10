package com.flowdesk.flowdesk.auth;

import com.flowdesk.flowdesk.auth.dto.AuthResponse;
import com.flowdesk.flowdesk.auth.dto.LoginRequest;
import com.flowdesk.flowdesk.auth.dto.RegisterRequest;
import com.flowdesk.flowdesk.security.JwtService;
import com.flowdesk.flowdesk.user.Role;
import com.flowdesk.flowdesk.user.User;
import com.flowdesk.flowdesk.user.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UserRepository users, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        String email = request.email().trim().toLowerCase();

        if (users.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }

        String hash = passwordEncoder.encode(request.password());
        User user = new User(email, hash, Role.MEMBER);
        users.save(user);

        String token = jwtService.generateToken(user.getEmail(), user.getRole());
        return new AuthResponse(token);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        String email = request.email().trim().toLowerCase();

        User user = users.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole());
        return new AuthResponse(token);
    }
}
