package com.kovan.integrationTestCases;

import com.kovan.entities.Book;
import com.kovan.entities.Category;
import com.kovan.entities.InventoryManagement;
import com.kovan.repository.BookRepository;
import com.kovan.repository.CategoryRepository;
import com.kovan.repository.InventoryManagementRepository;
import com.kovan.service.InventoryManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDate;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class InventoryManagementServiceIT {

    @Autowired
    private InventoryManagementService transactionService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private InventoryManagementRepository transactionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Book testBook;

    @BeforeEach
    public void setup() {
        transactionRepository.deleteAll();
        bookRepository.deleteAll();
        categoryRepository.deleteAll();

        Category testCategory = Category.builder().name("Fiction").build();
        categoryRepository.save(testCategory);

        testBook = Book.builder()
                .title("Test Book")
                .author("Test Author")
                .genre("Fiction")
                .price(20.99)
                .isbn("123-456-789")
                .publicationYear(2023)
                .publisher("Test Publisher")
                .stockQuantity(100)
                .description("A test book for integration testing")
                .coverImage("test-image.jpg")
                .category(testCategory)
                .build();
        testBook = bookRepository.save(testBook);
    }

    @Test
    void addTransactionTest() {

        InventoryManagement transaction = transactionService.addTransaction(
                testBook.getBookId(),
                "Purchase",
                10,
                "Initial stock purchase"
        );

        assertThat(transaction).isNotNull();
        assertThat(transaction.getInventoryTransactionId()).isNotNull();
        assertThat(transaction.getBook().getBookId()).isEqualTo(testBook.getBookId());
        assertThat(transaction.getTransactionType()).isEqualTo("Purchase");
        assertThat(transaction.getQuantity()).isEqualTo(10);
        assertThat(transaction.getNotes()).isEqualTo("Initial stock purchase");
        assertThat(transaction.getTransactionDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void updateTransactionTest() {

        InventoryManagement transaction = transactionService.addTransaction(
                testBook.getBookId(),
                "Purchase",
                10,
                "Initial stock purchase"
        );

        InventoryManagement updatedTransaction = InventoryManagement.builder()
                .book(testBook)
                .quantity(15)
                .transactionType("Restock")
                .notes("Added more stock")
                .build();

        InventoryManagement result = transactionService.updateInventory(
                transaction.getInventoryTransactionId(),
                updatedTransaction
        );

        assertThat(result).isNotNull();
        assertThat(result.getTransactionType()).isEqualTo("Restock");
        assertThat(result.getQuantity()).isEqualTo(15);
        assertThat(result.getNotes()).isEqualTo("Added more stock");
        assertThat(result.getTransactionDate()).isEqualTo(LocalDate.now());

    }

    @Test
    void getAllTransactionsTest() {

        transactionService.addTransaction(testBook.getBookId(), "Purchase", 10, "Initial stock purchase");
        transactionService.addTransaction(testBook.getBookId(), "Restock", 5, "Restocking inventory");

        List<InventoryManagement> transactions = transactionService.getAllTransactions();

        assertThat(transactions).hasSize(2);
        assertThat(transactions).extracting(InventoryManagement::getTransactionType)
                .containsExactlyInAnyOrder("Purchase", "Restock");
        assertThat(transactions).extracting(InventoryManagement::getQuantity)
                .containsExactlyInAnyOrder(10, 5);
    }

    @Test
    void deleteTransactionTest() {

        InventoryManagement transaction = transactionService.addTransaction(
                testBook.getBookId(),
                "Purchase",
                10,
                "Initial stock purchase"
        );

        transactionService.deleteTransaction(transaction.getInventoryTransactionId());

        assertThat(transactionRepository.existsById(transaction.getInventoryTransactionId())).isFalse();
    }
}

