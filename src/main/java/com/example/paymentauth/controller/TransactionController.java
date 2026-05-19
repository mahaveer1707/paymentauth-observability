package com.example.paymentauth.controller;

import com.example.paymentauth.dto.CreateTransactionRequest;
import com.example.paymentauth.dto.TransactionResponse;
import com.example.paymentauth.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);
    private final TransactionService transactionService;

    /**
     * Endpoint: POST /api/v1/transactions
     * Create a new transaction
     * 
     * @param request CreateTransactionRequest with payee, receiver, amount, currency
     * @return TransactionResponse with auto-generated UUID
     */
    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody CreateTransactionRequest request) {
        logger.info("Received POST request to create transaction - Payee: {}, Receiver: {}",
                request.getPayeeName(), request.getReceiverName());

        TransactionResponse response = transactionService.createTransaction(request);
        
        logger.info("Transaction creation endpoint - Response sent with transaction ID: {}",
                response.getTransactionId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint: GET /api/v1/transactions/{transactionId}
     * Fetch transaction by UUID
     * 
     * @param transactionId UUID of the transaction
     * @return TransactionResponse
     */
    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransaction(@PathVariable UUID transactionId) {
        logger.info("Received GET request to fetch transaction with ID: {}", transactionId);

        TransactionResponse response = transactionService.getTransactionById(transactionId);

        logger.info("Transaction fetch endpoint - Response sent for transaction ID: {}", transactionId);

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint: DELETE /api/v1/transactions/{transactionId}
     * Delete transaction by UUID
     * 
     * @param transactionId UUID of the transaction to delete
     * @return No content response
     */
    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable UUID transactionId) {
        logger.info("Received DELETE request to delete transaction with ID: {}", transactionId);

        transactionService.deleteTransaction(transactionId);

        logger.info("Transaction deletion endpoint - Transaction deleted successfully with ID: {}", transactionId);

        return ResponseEntity.noContent().build();
    }

}
