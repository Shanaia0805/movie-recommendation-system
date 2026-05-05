package com.lexiao.assignment2.controllers;

import com.lexiao.assignment2.entities.User;
import com.lexiao.assignment2.services.UserService;
import com.lexiao.assignment2.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> request) {
        String email = request.getOrDefault("email", "").trim();
        String password = request.getOrDefault("password", "").trim();

        if (email.isEmpty() || password.isEmpty()) {
            return createErrorResponse(HttpStatus.BAD_REQUEST, "Missing email or password");
        }

        if (!userService.registerUser(email, password)) {
            return createErrorResponse(HttpStatus.CONFLICT, "User already exists");
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> request) {
        // Check if email and password are present and not empty
        if (!request.containsKey("email") || !request.containsKey("password") ||
            request.get("email") == null || request.get("password") == null) {
            
            // Return 400 Bad Request for missing or null values
            return createErrorResponse(HttpStatus.BAD_REQUEST, "Missing email or password");
        }
    
        String email = request.get("email").trim();
        String password = request.get("password").trim();
    
        // If the values are present but empty strings, return 401 Unauthorized
        if (email.isEmpty() || password.isEmpty()) {
            return createErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
    
        User user = userService.authenticate(email, password);
        if (user == null) {
            return createErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
    
        Map<String, String> response = new HashMap<>();
        response.put("token", jwtUtil.generateToken(user.getEmail()));
    
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, String>> createErrorResponse(HttpStatus status, String message) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", message);
        return ResponseEntity.status(status).body(errorResponse);
    }
}
