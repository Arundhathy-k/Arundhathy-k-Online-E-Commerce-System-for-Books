package com.kovan.service;

import com.kovan.entities.Book;
import com.kovan.repository.BookRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    public Book getBookById(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book not found!"));
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
    public Book updateBook(Long id, Book updatedBook) {
        Book book = getBookById(id);
        book.setTitle(updatedBook.getTitle());
        book.setAuthor(updatedBook.getAuthor());
        book.setPrice(updatedBook.getPrice());
        book.setCategory(updatedBook.getCategory());
        book.setCoverImage(updatedBook.getCoverImage());
        book.setDescription(updatedBook.getDescription());
        book.setGenre(updatedBook.getGenre());
        book.setInventoryTransactions(updatedBook.getInventoryTransactions());
        book.setIsbn(updatedBook.getIsbn());
        book.setPublicationYear(updatedBook.getPublicationYear());
        book.setPublisher(updatedBook.getPublisher());
        book.setReviews(updatedBook.getReviews());
        book.setStockQuantity(updatedBook.getStockQuantity());
        return bookRepository.save(book);
    }
}

