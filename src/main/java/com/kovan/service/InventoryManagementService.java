package com.kovan.service;

import com.kovan.entities.Book;
import com.kovan.entities.InventoryTransaction;
import com.kovan.repository.BookRepository;
import com.kovan.repository.InventoryTransactionRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class InventoryManagementService {

    private final BookRepository bookRepository;

    private final InventoryTransactionRepository inventoryTransactionRepository;

    public InventoryManagementService(BookRepository bookRepository, InventoryTransactionRepository inventoryTransactionRepository) {
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
}

