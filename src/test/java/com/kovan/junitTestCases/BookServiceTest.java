package com.kovan.junitTestCases;

import com.kovan.entities.Book;
import com.kovan.entities.Category;
import com.kovan.repository.BookRepository;
import com.kovan.service.BookService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private final Category category = Category.builder().categoryId(1L).name("fiction").build();

    private final Book book = Book.builder()
                .bookId(1L)
                .title("Effective Java")
                .author("Joshua Bloch")
                .price(200.00)
                .category(category)
                .isbn("978-0134685991")
                .publicationYear(2018)
                .publisher("Addison-Wesley")
                .stockQuantity(10)
                .build();

    @Test
    void testAddBook() {

       when(bookRepository.save(book)).thenReturn(book);

        Book result = bookService.addBook(book);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(book, result);
        verify(bookRepository,times(1)).save(book);
    }

    @Test
    void testGetAllBooks() {

        List<Book> books = List.of(book);
        when(bookRepository.findAll()).thenReturn(books);

        List<Book> result = bookService.getAllBooks();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(book, result.getFirst());
        verify(bookRepository,times(1)).findAll();
    }

    @Test
    void testGetBookById() {

       when(bookRepository.findById(book.getBookId())).thenReturn(of(book));

        Book result = bookService.getBookById(book.getBookId());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(book, result);
        verify(bookRepository, times(1)).findById(book.getBookId());
    }

    @Test
    void testGetBookByIdThrowsException() {

       when(bookRepository.findById(1L)).thenReturn(empty());


        Assertions.assertThrows(RuntimeException.class, () -> bookService.getBookById(1L));
        verify(bookRepository,times(1)).findById(1L);
    }

    @Test
    void testDeleteBook() {

        bookService.deleteBook(book.getBookId());

        verify(bookRepository, times(1)).deleteById(book.getBookId());
    }

    @Test
    void testUpdateBook() {

        Category updatedCategory = Category.builder().categoryId(1L).name("Updated Category").build();
        Book updatedBook = Book.builder()
                .bookId(1L)
                .title("Updated Title")
                .author("Updated Author")
                .price(50.00)
                .category(updatedCategory)
                .isbn("123-4567890123")
                .publicationYear(2021)
                .publisher("Updated Publisher")
                .stockQuantity(20)
                .build();

        when(bookRepository.findById(book.getBookId())).thenReturn(of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(updatedBook);

        Book result = bookService.updateBook(book.getBookId(), updatedBook);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(updatedBook.getTitle(), result.getTitle());
        Assertions.assertEquals(updatedBook.getAuthor(), result.getAuthor());
        Assertions.assertEquals(updatedBook.getPrice(), result.getPrice());
        Assertions.assertEquals(updatedBook.getStockQuantity(), result.getStockQuantity());
        verify(bookRepository,times(1)).findById(book.getBookId());
        verify(bookRepository,times(1)).save(book);
    }
}

