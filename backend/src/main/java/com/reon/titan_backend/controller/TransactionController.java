package com.reon.titan_backend.controller;

import com.reon.titan_backend.document.User;
import com.reon.titan_backend.dto.TransactionRequest;
import com.reon.titan_backend.dto.response.TransactionResponse;
import com.reon.titan_backend.dto.response.TransactionStatusResponse;
import com.reon.titan_backend.dto.response.ApiResponse;
import com.reon.titan_backend.service.TransactionService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@Slf4j
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TransactionResponse>> createNewTransaction(
            @Valid @RequestBody TransactionRequest request,
            @AuthenticationPrincipal User user) {
        TransactionResponse response = transactionService.generateNewTransaction(request, user.getId());
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(new ApiResponse<>(
                        true,
                        "Transaction accepted into processing pipeline.",
                        response
                ));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getMyTransactions(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<TransactionResponse> transactions = transactionService.getUserTransactions(user.getId(), page, size);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(
                        true,
                        "Your transactions fetched",
                        transactions
                ));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getUserTransactions(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<TransactionResponse> transactions = transactionService.getUserTransactions(userId, page, size);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(
                        true,
                        "User transactions fetched",
                        transactions
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
