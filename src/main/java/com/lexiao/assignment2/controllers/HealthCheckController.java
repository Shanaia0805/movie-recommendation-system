package com.lexiao.assignment2.controllers;

import com.lexiao.assignment2.services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1")
public class HealthCheckController {

    @Autowired
    private MovieService movieService;

    @GetMapping("/healthcheck")
    public ResponseEntity<Void> healthCheck(@RequestParam Map<String, String> params) {
        if (!params.isEmpty()) {
            return badRequestResponse();
        }

        if (!movieService.isDatabaseConnected()) {
            return serviceUnavailableResponse();
        }

        return successResponse();
    }

    @RequestMapping(value = "/healthcheck",
            method = {RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH})
    public ResponseEntity<Void> handleInvalidRequests(@RequestParam Map<String, String> params) {
        return badRequestResponse();
    }

    private ResponseEntity<Void> successResponse() {
        HttpHeaders headers = commonHeaders();
        return ResponseEntity.status(HttpStatus.OK).headers(headers).build();
    }

    private ResponseEntity<Void> badRequestResponse() {
        HttpHeaders headers = commonHeaders();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(headers).build();
    }

    private ResponseEntity<Void> serviceUnavailableResponse() {
        HttpHeaders headers = commonHeaders();
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).headers(headers).build();
    }

    private HttpHeaders commonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("X-Content-Type-Options", "nosniff");
        return headers;
    }
}