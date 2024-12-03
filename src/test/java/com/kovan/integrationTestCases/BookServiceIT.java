package com.kovan.integrationTestCases;

import com.kovan.entities.Book;
import com.kovan.entities.Category;
import com.kovan.repository.BookRepository;
import com.kovan.repository.CategoryRepository;
import com.kovan.service.BookService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class BookServiceIT {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BookService bookService;

    @AfterEach
    void cleanUp() {
        bookRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    Category testCategory = Category.builder().name("Fiction").build();

    Book book = Book.builder()
            .title("Test Book")
            .author("Test Author")
            .price(19.99)
            .isbn("1234567890123")
            .publicationYear(2023)
            .stockQuantity(10)
            .category(testCategory)
            .build();

    @Test
    void addBookTest() {
        categoryRepository.save(testCategory);
        Book savedBook = bookService.addBook(book);

        assertThat(savedBook).isNotNull();
        assertThat(savedBook.getBookId()).isNotNull();

        Optional<Book> foundBook = bookRepository.findById(savedBook.getBookId());
        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getTitle()).isEqualTo("Test Book");
    }

    @Test
    void getAllBooksTest() {
        categoryRepository.save(testCategory);
        bookRepository.save(Book.builder().title("Book 1").author("Author 1").price(15.99).isbn("1234567890456").publicationYear(2023).stockQuantity(5).category(testCategory).build());
        bookRepository.save(Book.builder().title("Book 2").author("Author 2").price(25.99).isbn("1234567890789").publicationYear(2021).stockQuantity(3).category(testCategory).build());

        List<Book> books = bookService.getAllBooks();

        assertThat(books).hasSize(2);
        assertThat(books).extracting(Book::getTitle).contains("Book 1", "Book 2");
    }

    @Test
    void getBookByIdTest() {
        categoryRepository.save(testCategory);
        bookRepository.save(book);

        Book fetchedBook = bookService.getBookById(book.getBookId());

        assertThat(fetchedBook).isNotNull();
        assertThat(fetchedBook.getTitle()).isEqualTo("Test Book");
    }

    @Test
    void updateBookTest() {
        categoryRepository.save(testCategory);
        bookRepository.save(book);

        Book updatedBook = Book.builder()
                .title("Updated Title")
                .author("Updated Author")
                .price(12.99)
                .isbn("1234567890789")
                .publicationYear(2021)
                .stockQuantity(5)
                .category(testCategory)
                .build();

        Book result = bookService.updateBook(book.getBookId(), updatedBook);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Updated Title");
        assertThat(result.getAuthor()).isEqualTo("Updated Author");
        assertThat(result.getPrice()).isEqualTo(12.99);
    }

    @Test
    void deleteBookTest() {

        categoryRepository.save(testCategory);
        bookRepository.save(book);

        bookService.deleteBook(book.getBookId());

        Optional<Book> deletedBook = bookRepository.findById(book.getBookId());
        assertThat(deletedBook).isNotPresent();
    }
}

