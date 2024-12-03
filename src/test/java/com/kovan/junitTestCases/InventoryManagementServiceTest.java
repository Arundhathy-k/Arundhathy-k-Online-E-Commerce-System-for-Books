package com.kovan.junitTestCases;

import com.kovan.entities.Book;
import com.kovan.entities.InventoryManagement;
import com.kovan.repository.BookRepository;
import com.kovan.repository.InventoryManagementRepository;
import com.kovan.service.InventoryManagementService;
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
class InventoryManagementServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private InventoryManagementRepository inventoryManagementRepository;

    @InjectMocks
    private InventoryManagementService inventoryManagementService;

    private final Book book = Book.builder()
            .bookId(1L)
            .title("Test Book")
            .stockQuantity(50)
            .build();

    private final InventoryManagement transaction = InventoryManagement.builder()
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
        when(inventoryManagementRepository.save(any(InventoryManagement.class))).thenReturn(transaction);

        InventoryManagement result = inventoryManagementService.addTransaction(book.getBookId(), "Purchase", 10, "Test Transaction");

        assertNotNull(result);
        assertEquals(transaction.getInventoryTransactionId(), result.getInventoryTransactionId());
        assertEquals(transaction.getTransactionType(), result.getTransactionType());
        verify(bookRepository, times(1)).findById(book.getBookId());
        verify(inventoryManagementRepository, times(1)).save(any(InventoryManagement.class));
    }

    @Test
    void testUpdateInventory() {

        when(bookRepository.findById(book.getBookId())).thenReturn(of(book));
        when(inventoryManagementRepository.findById(transaction.getInventoryTransactionId())).thenReturn(of(transaction));
        when(inventoryManagementRepository.save(any(InventoryManagement.class))).thenReturn(transaction);

        InventoryManagement result = inventoryManagementService.updateInventory(transaction.getInventoryTransactionId(), transaction);

        assertNotNull(result);
        assertEquals(transaction.getTransactionType(), result.getTransactionType());
        verify(bookRepository, times(1)).findById(book.getBookId());
        verify(inventoryManagementRepository, times(1)).findById(transaction.getInventoryTransactionId());
        verify(bookRepository, times(1)).save(book);
        verify(inventoryManagementRepository, times(1)).save(any(InventoryManagement.class));
    }

    @Test
    void testUpdateInventoryThrowsExceptionForInvalidTransactionType() {

        transaction.setTransactionType("InvalidType");
        when(bookRepository.findById(book.getBookId())).thenReturn(of(book));

        assertThrows(IllegalArgumentException.class,
                () -> inventoryManagementService.updateInventory(transaction.getInventoryTransactionId(), transaction));
    }

    @Test
    void testGetAllTransactions() {

        List<InventoryManagement> transactions = List.of(transaction);
        when(inventoryManagementRepository.findAll()).thenReturn(transactions);

        List<InventoryManagement> result = inventoryManagementService.getAllTransactions();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(transaction, result.get(0));
        verify(inventoryManagementRepository, times(1)).findAll();
    }

    @Test
    void testGetTransactionById() {

        when(inventoryManagementRepository.findById(transaction.getInventoryTransactionId())).thenReturn(of(transaction));

        InventoryManagement result = inventoryManagementService.getTransactionById(transaction.getInventoryTransactionId());

        assertNotNull(result);
        assertEquals(transaction.getInventoryTransactionId(), result.getInventoryTransactionId());
        verify(inventoryManagementRepository, times(1)).findById(transaction.getInventoryTransactionId());
    }

    @Test
    void testGetTransactionByIdThrowsExceptionWhenNotFound() {

        when(inventoryManagementRepository.findById(transaction.getInventoryTransactionId())).thenReturn(java.util.Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> inventoryManagementService.getTransactionById(transaction.getInventoryTransactionId()));
        verify(inventoryManagementRepository, times(1)).findById(transaction.getInventoryTransactionId());
    }

    @Test
    void testDeleteTransaction() {

        when(inventoryManagementRepository.existsById(transaction.getInventoryTransactionId())).thenReturn(true);

        inventoryManagementService.deleteTransaction(transaction.getInventoryTransactionId());

        verify(inventoryManagementRepository, times(1)).existsById(transaction.getInventoryTransactionId());
        verify(inventoryManagementRepository, times(1)).deleteById(transaction.getInventoryTransactionId());
    }

    @Test
    void testDeleteTransactionThrowsExceptionWhenNotFound() {

        when(inventoryManagementRepository.existsById(transaction.getInventoryTransactionId())).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> inventoryManagementService.deleteTransaction(transaction.getInventoryTransactionId()));
        verify(inventoryManagementRepository, times(1)).existsById(transaction.getInventoryTransactionId());
        verify(inventoryManagementRepository, never()).deleteById(anyLong());
    }
}

