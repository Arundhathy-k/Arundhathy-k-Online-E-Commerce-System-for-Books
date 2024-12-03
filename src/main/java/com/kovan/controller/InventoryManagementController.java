package com.kovan.controller;

import com.kovan.entities.InventoryManagement;
import com.kovan.service.InventoryManagementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inventory-transactions")
public class InventoryManagementController {

    private final InventoryManagementService inventoryManagementService;

    public InventoryManagementController(InventoryManagementService inventoryManagementService) {
        this.inventoryManagementService = inventoryManagementService;
    }

    @PostMapping("/add")
    public ResponseEntity<InventoryManagement> addTransaction(@RequestParam Long bookId,
                                                              @RequestParam String transactionType,
                                                              @RequestParam int quantity,
                                                              @RequestParam(required = false) String notes) {
        InventoryManagement transaction = inventoryManagementService.addTransaction(bookId, transactionType, quantity, notes);
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }

    @GetMapping("/fetch")
    public ResponseEntity<List<InventoryManagement>> getAllTransactions() {
        List<InventoryManagement> transactions = inventoryManagementService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<InventoryManagement> getTransactionById(@PathVariable Long transactionId) {
        InventoryManagement transaction = inventoryManagementService.getTransactionById(transactionId);
        return ResponseEntity.ok(transaction);
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long transactionId) {
        inventoryManagementService.deleteTransaction(transactionId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryManagement> updateTransaction(@PathVariable Long id, @RequestBody InventoryManagement inventoryManagement) {
        InventoryManagement updatedInventoryManagement = inventoryManagementService.updateInventory(id, inventoryManagement);
        return ResponseEntity.ok(updatedInventoryManagement);
    }
}

