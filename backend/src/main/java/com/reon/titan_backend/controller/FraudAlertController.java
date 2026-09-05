package com.reon.titan_backend.controller;

import com.reon.titan_backend.dto.response.ApiResponse;
import com.reon.titan_backend.dto.response.FraudAlertResponse;
import com.reon.titan_backend.service.FraudAlertService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fraud-alerts")
@Slf4j
public class FraudAlertController {
    private final FraudAlertService fraudAlertService;

    public FraudAlertController(FraudAlertService fraudAlertService) {
        this.fraudAlertService = fraudAlertService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FraudAlertResponse>>> getAllAlerts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<FraudAlertResponse> alerts = fraudAlertService.getAllAlerts(page, size);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(
                        true,
                        "Fraud alerts fetched",
                        alerts
                ));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<FraudAlertResponse>>> getUserAlerts(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<FraudAlertResponse> alerts = fraudAlertService.getUserAlerts(userId, page, size);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(
                        true,
                        "User fraud alerts fetched",
                        alerts
                ));
    }
}
