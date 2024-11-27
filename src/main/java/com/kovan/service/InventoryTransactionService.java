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

    public InventoryTransaction updateInventory(Long transactionId, InventoryTransaction inventoryTransaction) {

        Book book = bookRepository.findById(inventoryTransaction.getBook().getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found!"));

        if (inventoryTransaction.getTransactionType().equalsIgnoreCase("Purchase") && book.getStockQuantity() < inventoryTransaction.getQuantity()) {
            throw new RuntimeException("Insufficient stock!");
        }

        switch (inventoryTransaction.getTransactionType().toLowerCase()) {
            case "purchase":
                book.setStockQuantity(book.getStockQuantity() - inventoryTransaction.getQuantity());
                break;
            case "restock":
                book.setStockQuantity(book.getStockQuantity() + inventoryTransaction.getQuantity());
                break;
            default:
                throw new IllegalArgumentException("Invalid transaction type!");
        }

        bookRepository.save(book);

        InventoryTransaction transaction = getTransactionById(transactionId);
              transaction.setBook(book);
              transaction.setQuantity(inventoryTransaction.getQuantity());
              transaction.setTransactionType(inventoryTransaction.getTransactionType());
              transaction.setTransactionDate(LocalDate.now());
              transaction.setNotes(inventoryTransaction.getNotes());

       return inventoryTransactionRepository.save(transaction);
    }
    public InventoryTransaction addTransaction(Long bookId, String transactionType, int quantity, String notes) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        InventoryTransaction transaction = InventoryTransaction.builder()
                .book(book)
                .quantity(quantity)
                .transactionType(transactionType)
                .transactionDate(LocalDate.now())
                .notes(notes).build();

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

