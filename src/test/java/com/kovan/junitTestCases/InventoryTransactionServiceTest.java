package com.kovan.junitTestCases;

import com.kovan.entities.Book;
import com.kovan.entities.InventoryTransaction;
import com.kovan.repository.BookRepository;
import com.kovan.repository.InventoryTransactionRepository;
import com.kovan.service.InventoryTransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static java.util.Optional.of;

@ExtendWith(MockitoExtension.class)
class InventoryTransactionServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private InventoryTransactionRepository inventoryTransactionRepository;

    @InjectMocks
    private InventoryTransactionService inventoryTransactionService;

    private final Book book = Book.builder()
            .bookId(1L)
            .title("Test Book")
            .stockQuantity(50)
            .build();

    private final InventoryTransaction transaction = InventoryTransaction.builder()
                .inventoryTransactionId(1L)
                .book(book)
                .transactionType("Purchase")
                .quantity(10)
                .transactionDate(LocalDate.now())
                .notes("Test Transaction")
                .build();

    @Test
    void testAddTransaction() {
        when(bookRepository.findById(book.getBookId())).thenReturn(of(book));
        when(inventoryTransactionRepository.save(any(InventoryTransaction.class))).thenReturn(transaction);

        InventoryTransaction result = inventoryTransactionService.addTransaction(book.getBookId(), "Purchase", 10, "Test Transaction");

        assertNotNull(result);
        assertEquals(transaction.getInventoryTransactionId(), result.getInventoryTransactionId());
        assertEquals(transaction.getTransactionType(), result.getTransactionType());
        verify(bookRepository, times(1)).findById(book.getBookId());
        verify(inventoryTransactionRepository, times(1)).save(any(InventoryTransaction.class));
    }

    @Test
    void testUpdateInventory() {

        when(bookRepository.findById(book.getBookId())).thenReturn(of(book));
        when(inventoryTransactionRepository.findById(transaction.getInventoryTransactionId())).thenReturn(of(transaction));
        when(inventoryTransactionRepository.save(any(InventoryTransaction.class))).thenReturn(transaction);

        InventoryTransaction result = inventoryTransactionService.updateInventory(transaction.getInventoryTransactionId(), transaction);

        assertNotNull(result);
        assertEquals(transaction.getTransactionType(), result.getTransactionType());
        verify(bookRepository, times(1)).findById(book.getBookId());
        verify(inventoryTransactionRepository, times(1)).findById(transaction.getInventoryTransactionId());
        verify(bookRepository, times(1)).save(book);
        verify(inventoryTransactionRepository, times(1)).save(any(InventoryTransaction.class));
    }

    @Test
    void testUpdateInventoryThrowsExceptionForInvalidTransactionType() {

        transaction.setTransactionType("InvalidType");
        when(bookRepository.findById(book.getBookId())).thenReturn(of(book));

        assertThrows(IllegalArgumentException.class,
                () -> inventoryTransactionService.updateInventory(transaction.getInventoryTransactionId(), transaction));
    }

    @Test
    void testGetAllTransactions() {

        List<InventoryTransaction> transactions = List.of(transaction);
        when(inventoryTransactionRepository.findAll()).thenReturn(transactions);

        List<InventoryTransaction> result = inventoryTransactionService.getAllTransactions();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(transaction, result.get(0));
        verify(inventoryTransactionRepository, times(1)).findAll();
    }

    @Test
    void testGetTransactionById() {

        when(inventoryTransactionRepository.findById(transaction.getInventoryTransactionId())).thenReturn(of(transaction));

        InventoryTransaction result = inventoryTransactionService.getTransactionById(transaction.getInventoryTransactionId());

        assertNotNull(result);
        assertEquals(transaction.getInventoryTransactionId(), result.getInventoryTransactionId());
        verify(inventoryTransactionRepository, times(1)).findById(transaction.getInventoryTransactionId());
    }

    @Test
    void testGetTransactionByIdThrowsExceptionWhenNotFound() {

        when(inventoryTransactionRepository.findById(transaction.getInventoryTransactionId())).thenReturn(java.util.Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> inventoryTransactionService.getTransactionById(transaction.getInventoryTransactionId()));
        verify(inventoryTransactionRepository, times(1)).findById(transaction.getInventoryTransactionId());
    }

    @Test
    void testDeleteTransaction() {

        when(inventoryTransactionRepository.existsById(transaction.getInventoryTransactionId())).thenReturn(true);

        inventoryTransactionService.deleteTransaction(transaction.getInventoryTransactionId());

        verify(inventoryTransactionRepository, times(1)).existsById(transaction.getInventoryTransactionId());
        verify(inventoryTransactionRepository, times(1)).deleteById(transaction.getInventoryTransactionId());
    }

    @Test
    void testDeleteTransactionThrowsExceptionWhenNotFound() {

        when(inventoryTransactionRepository.existsById(transaction.getInventoryTransactionId())).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> inventoryTransactionService.deleteTransaction(transaction.getInventoryTransactionId()));
        verify(inventoryTransactionRepository, times(1)).existsById(transaction.getInventoryTransactionId());
        verify(inventoryTransactionRepository, never()).deleteById(anyLong());
    }
}

