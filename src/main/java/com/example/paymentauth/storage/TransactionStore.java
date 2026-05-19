package com.example.paymentauth.storage;

import com.example.paymentauth.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * In-Memory Transaction Store
 * Uses HashMap to store transactions during active session
 * Data is cleared when the application restarts
 */
@Component
@Slf4j
public class TransactionStore {

    private static final Map<UUID, Transaction> store = new HashMap<>();

    /**
     * Save a transaction to the in-memory store
     */
    public Transaction save(Transaction transaction) {
        log.debug("Saving transaction to in-memory store - ID: {}", transaction.getTransactionId());
        store.put(transaction.getTransactionId(), transaction);
        log.info("Transaction saved successfully - ID: {}, Total transactions in store: {}",
                transaction.getTransactionId(), store.size());
        return transaction;
    }

    /**
     * Find a transaction by UUID
     */
    public Optional<Transaction> findById(UUID transactionId) {
        log.debug("Fetching transaction from in-memory store - ID: {}", transactionId);
        Optional<Transaction> transaction = Optional.ofNullable(store.get(transactionId));
        if (transaction.isPresent()) {
            log.info("Transaction found in store - ID: {}", transactionId);
        } else {
            log.warn("Transaction not found in store - ID: {}", transactionId);
        }
        return transaction;
    }

    /**
     * Delete a transaction from the store
     */
    public void delete(UUID transactionId) {
        log.debug("Deleting transaction from in-memory store - ID: {}", transactionId);
        if (store.containsKey(transactionId)) {
            store.remove(transactionId);
            log.info("Transaction deleted successfully - ID: {}, Remaining transactions: {}",
                    transactionId, store.size());
        } else {
            log.warn("Attempted to delete non-existent transaction - ID: {}", transactionId);
        }
    }

    /**
     * Get the total number of transactions in the store
     */
    public int size() {
        return store.size();
    }

    /**
     * Check if a transaction exists
     */
    public boolean exists(UUID transactionId) {
        return store.containsKey(transactionId);
    }

    /**
     * Get store information for logging/monitoring
     */
    public String getStoreInfo() {
        return String.format("In-Memory Store - Total transactions: %d", store.size());
    }

}
