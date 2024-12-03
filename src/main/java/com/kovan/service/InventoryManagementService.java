package com.kovan.service;

import com.kovan.entities.Book;
import com.kovan.entities.InventoryManagement;
import com.kovan.repository.BookRepository;
import com.kovan.repository.InventoryManagementRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class InventoryManagementService {

    private final BookRepository bookRepository;

    private final InventoryManagementRepository inventoryManagementRepository;

    public InventoryManagementService(BookRepository bookRepository, InventoryManagementRepository inventoryManagementRepository) {
        this.bookRepository = bookRepository;
        this.inventoryManagementRepository = inventoryManagementRepository;
    }

    public InventoryManagement updateInventory(Long transactionId, InventoryManagement inventoryManagement) {

        Book book = bookRepository.findById(inventoryManagement.getBook().getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found!"));

        if (inventoryManagement.getTransactionType().equalsIgnoreCase("Purchase") && book.getStockQuantity() < inventoryManagement.getQuantity()) {
            throw new RuntimeException("Insufficient stock!");
        }

        switch (inventoryManagement.getTransactionType().toLowerCase()) {
            case "purchase":
                book.setStockQuantity(book.getStockQuantity() - inventoryManagement.getQuantity());
                break;
            case "restock":
                book.setStockQuantity(book.getStockQuantity() + inventoryManagement.getQuantity());
                break;
            default:
                throw new IllegalArgumentException("Invalid transaction type!");
        }

        bookRepository.save(book);

        InventoryManagement transaction = getTransactionById(transactionId);
              transaction.setBook(book);
              transaction.setQuantity(inventoryManagement.getQuantity());
              transaction.setTransactionType(inventoryManagement.getTransactionType());
              transaction.setTransactionDate(LocalDate.now());
              transaction.setNotes(inventoryManagement.getNotes());

       return inventoryManagementRepository.save(transaction);
    }
    public InventoryManagement addTransaction(Long bookId, String transactionType, int quantity, String notes) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        InventoryManagement transaction = InventoryManagement.builder()
                .book(book)
                .quantity(quantity)
                .transactionType(transactionType)
                .transactionDate(LocalDate.now())
                .notes(notes).build();

        return inventoryManagementRepository.save(transaction);
    }

    public List<InventoryManagement> getAllTransactions() {
        return inventoryManagementRepository.findAll();
    }

    public InventoryManagement getTransactionById(Long transactionId) {
        return inventoryManagementRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
    }

    public void deleteTransaction(Long transactionId) {
        if (!inventoryManagementRepository.existsById(transactionId)) {
            throw new IllegalArgumentException("Transaction not found");
        }
        inventoryManagementRepository.deleteById(transactionId);
    }
}

