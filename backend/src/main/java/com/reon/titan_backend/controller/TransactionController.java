package com.reon.titan_backend.controller;

import com.reon.titan_backend.dto.TransactionRequest;
import com.reon.titan_backend.dto.response.TransactionResponse;
import com.reon.titan_backend.dto.response.TransactionStatusResponse;
import com.reon.titan_backend.exception.response.ApiResponse;
import com.reon.titan_backend.service.TransactionService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
@Slf4j
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TransactionResponse>> createNewTransaction(@Valid @RequestBody TransactionRequest request) {
        TransactionResponse response = transactionService.generateNewTransaction(request);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(new ApiResponse<>(
                        true,
                        "Transaction accepted into processing pipeline.",
                        response
                ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionStatusResponse>> getTransactionStatusUpdate(@PathVariable String id) {
        TransactionStatusResponse transactionStatus = transactionService.getTransactionStatus(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(
                        true,
                        "Transaction fetched",
                        transactionStatus
                ));
    }
}
