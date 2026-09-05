package com.reon.titan_backend.controller;

import com.reon.titan_backend.dto.response.ApiResponse;
import com.reon.titan_backend.dto.response.FailedTransactionResponse;
import com.reon.titan_backend.service.DeadLetterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/failed-transactions")
@Slf4j
public class FailedTransactionController {
    private final DeadLetterService deadLetterService;

    public FailedTransactionController(DeadLetterService deadLetterService) {
        this.deadLetterService = deadLetterService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FailedTransactionResponse>>> getFailedTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<FailedTransactionResponse> failedTransactions = deadLetterService.getFailedTransactions(page, size);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(
                        true,
                        "Failed transactions fetched",
                        failedTransactions
                ));
    }

    @PostMapping("/{id}/replay")
    public ResponseEntity<ApiResponse<String>> replayFailedTransaction(@PathVariable String id) {
        deadLetterService.replayFailedTransaction(id);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(new ApiResponse<>(
                        true,
                        "Failed transaction sent back into the pipeline.",
                        id
                ));
    }
}
