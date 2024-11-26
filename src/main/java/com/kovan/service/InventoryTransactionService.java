package com.kovan.service;

import com.kovan.entities.Book;
import com.kovan.entities.InventoryTransaction;
import com.kovan.repository.BookRepository;
import com.kovan.repository.InventoryTransactionRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class InventoryTransactionService {

    private final BookRepository bookRepository;

    private final InventoryTransactionRepository inventoryTransactionRepository;

    public InventoryTransactionService(BookRepository bookRepository, InventoryTransactionRepository inventoryTransactionRepository) {
        this.bookRepository = bookRepository;
        this.inventoryTransactionRepository = inventoryTransactionRepository;
    }

    public void updateInventory(Long bookId, int quantity, String transactionType, String notes) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found!"));

        if (transactionType.equalsIgnoreCase("Purchase") && book.getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock!");
        }

        switch (transactionType.toLowerCase()) {
            case "purchase":
                book.setStockQuantity(book.getStockQuantity() - quantity);
                break;
            case "restock":
                book.setStockQuantity(book.getStockQuantity() + quantity);
                break;
            default:
                throw new IllegalArgumentException("Invalid transaction type!");
        }

        bookRepository.save(book);

        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setBook(book);
        transaction.setQuantity(quantity);
        transaction.setTransactionType(transactionType);
        transaction.setTransactionDate(LocalDate.now());
        transaction.setNotes(notes);

        inventoryTransactionRepository.save(transaction);
    }
    public InventoryTransaction addTransaction(Long bookId, String transactionType, int quantity, String notes) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setBook(book);
        transaction.setTransactionType(transactionType);
        transaction.setQuantity(quantity);
        transaction.setTransactionDate(LocalDate.now());
        transaction.setNotes(notes);

        return inventoryTransactionRepository.save(transaction);
    }

    public List<InventoryTransaction> getAllTransactions() {
        return inventoryTransactionRepository.findAll();
    }

    public InventoryTransaction getTransactionById(Long transactionId) {
        return inventoryTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
    }

    public void deleteTransaction(Long transactionId) {
        if (!inventoryTransactionRepository.existsById(transactionId)) {
            throw new IllegalArgumentException("Transaction not found");
        }
        inventoryTransactionRepository.deleteById(transactionId);
    }
}

