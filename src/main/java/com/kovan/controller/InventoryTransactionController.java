package com.kovan.controller;

import com.kovan.entities.InventoryTransaction;
import com.kovan.service.InventoryTransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inventory-transactions")
public class InventoryTransactionController {

    private final InventoryTransactionService inventoryTransactionService;

    public InventoryTransactionController(InventoryTransactionService inventoryTransactionService) {
        this.inventoryTransactionService = inventoryTransactionService;
    }

    @PostMapping("/add")
    public ResponseEntity<InventoryTransaction> addTransaction(@RequestParam Long bookId,
                                                               @RequestParam String transactionType,
                                                               @RequestParam int quantity,
                                                               @RequestParam(required = false) String notes) {
        InventoryTransaction transaction = inventoryTransactionService.addTransaction(bookId, transactionType, quantity, notes);
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }

    @GetMapping("/fetch")
    public ResponseEntity<List<InventoryTransaction>> getAllTransactions() {
        List<InventoryTransaction> transactions = inventoryTransactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<InventoryTransaction> getTransactionById(@PathVariable Long transactionId) {
        InventoryTransaction transaction = inventoryTransactionService.getTransactionById(transactionId);
        return ResponseEntity.ok(transaction);
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long transactionId) {
        inventoryTransactionService.deleteTransaction(transactionId);
        return ResponseEntity.noContent().build();
    }
}

