package com.example.paymentauth.service;

import com.example.paymentauth.dto.CreateTransactionRequest;
import com.example.paymentauth.dto.TransactionResponse;
import com.example.paymentauth.exception.TransactionNotFoundException;
import com.example.paymentauth.model.Transaction;
import com.example.paymentauth.storage.TransactionStore;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    private final TransactionStore transactionStore;

    /**
     * Create a new transaction
     * @param request CreateTransactionRequest containing payee, receiver, amount, and currency
     * @return TransactionResponse with auto-generated UUID
     */
    public TransactionResponse createTransaction(CreateTransactionRequest request) {
        logger.info("Creating new transaction - Payee: {}, Receiver: {}, Amount: {}, Currency: {}",
                request.getPayeeName(), request.getReceiverName(), request.getAmount(), request.getCurrency());

        try {
            UUID generatedId = UUID.randomUUID();
            LocalDateTime now = LocalDateTime.now();

            Transaction transaction = Transaction.builder()
                    .transactionId(generatedId)
                    .payeeName(request.getPayeeName())
                    .receiverName(request.getReceiverName())
                    .amount(request.getAmount())
                    .currency(request.getCurrency())
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

            Transaction savedTransaction = transactionStore.save(transaction);
            logger.info("Transaction created successfully with ID: {}, Store info: {}",
                    savedTransaction.getTransactionId(), transactionStore.getStoreInfo());

            return mapToResponse(savedTransaction);
        } catch (Exception e) {
            logger.error("Error creating transaction - Payee: {}, Receiver: {}", 
                    request.getPayeeName(), request.getReceiverName(), e);
            throw e;
        }
    }

    /**
     * Fetch transaction by UUID
     * @param transactionId UUID of the transaction
     * @return TransactionResponse
     */
    public TransactionResponse getTransactionById(UUID transactionId) {
        logger.debug("Fetching transaction with ID: {}", transactionId);

        try {
            Transaction transaction = transactionStore.findById(transactionId)
                    .orElseThrow(() -> {
                        logger.warn("Transaction not found with ID: {}", transactionId);
                        return new TransactionNotFoundException("Transaction not found with ID: " + transactionId);
                    });

            logger.info("Transaction fetched successfully - ID: {}, Payee: {}, Receiver: {}",
                    transaction.getTransactionId(), transaction.getPayeeName(), transaction.getReceiverName());

            return mapToResponse(transaction);
        } catch (TransactionNotFoundException e) {
            logger.error("Transaction fetch failed - ID: {}", transactionId);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error fetching transaction with ID: {}", transactionId, e);
            throw e;
        }
    }

    /**
     * Delete transaction by UUID
     * @param transactionId UUID of the transaction to delete
     */
    public void deleteTransaction(UUID transactionId) {
        logger.info("Deleting transaction with ID: {}", transactionId);

        try {
            if (!transactionStore.exists(transactionId)) {
                logger.warn("Cannot delete - Transaction not found with ID: {}", transactionId);
                throw new TransactionNotFoundException("Transaction not found with ID: " + transactionId);
            }

            transactionStore.delete(transactionId);
            logger.info("Transaction deleted successfully - ID: {}, Store info: {}",
                    transactionId, transactionStore.getStoreInfo());
        } catch (TransactionNotFoundException e) {
            logger.error("Transaction deletion failed - ID: {}", transactionId);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error deleting transaction with ID: {}", transactionId, e);
            throw e;
        }
    }

    /**
     * Convert Transaction entity to TransactionResponse DTO
     */
    private TransactionResponse mapToResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .transactionId(transaction.getTransactionId())
                .payeeName(transaction.getPayeeName())
                .receiverName(transaction.getReceiverName())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }

}
