package com.kovan.junitTestCases;

import com.kovan.entities.Book;
import com.kovan.entities.Review;
import com.kovan.entities.User;
import com.kovan.repository.BookRepository;
import com.kovan.repository.ReviewRepository;
import com.kovan.repository.UserRepository;
import com.kovan.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReviewService reviewService;

    private final User user = User.builder()
            .userId(1L)
            .build();

    private final Book book = Book.builder()
            .bookId(1L)
            .build();

    private final Review review = Review.builder()
            .reviewId(1L)
            .user(user)
            .book(book)
            .rating(5)
            .comment("Great book!")
            .reviewDate(LocalDate.now())
            .build();

    @Test
    void testAddOrUpdateReview_NewReview() {
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(bookRepository.findById(book.getBookId())).thenReturn(Optional.of(book));
        when(reviewRepository.findByUserAndBook(user, book)).thenReturn(Optional.empty());
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        Review result = reviewService.addOrUpdateReview(user.getUserId(), book.getBookId(), 5, "Great book!");

        assertNotNull(result);
        assertEquals(review.getReviewId(), result.getReviewId());
        assertEquals(5, result.getRating());
        assertEquals("Great book!", result.getComment());
        verify(userRepository, times(1)).findById(user.getUserId());
        verify(bookRepository, times(1)).findById(book.getBookId());
        verify(reviewRepository, times(1)).findByUserAndBook(user, book);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void testAddOrUpdateReview_UpdateExistingReview() {
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(bookRepository.findById(book.getBookId())).thenReturn(Optional.of(book));
        when(reviewRepository.findByUserAndBook(user, book)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        Review result = reviewService.addOrUpdateReview(user.getUserId(), book.getBookId(), 4, "Good book!");

        assertNotNull(result);
        assertEquals(review.getReviewId(), result.getReviewId());
        assertEquals(4, result.getRating());
        assertEquals("Good book!", result.getComment());
        verify(userRepository, times(1)).findById(user.getUserId());
        verify(bookRepository, times(1)).findById(book.getBookId());
        verify(reviewRepository, times(1)).findByUserAndBook(user, book);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void testAddOrUpdateReview_UserNotFound() {
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reviewService.addOrUpdateReview(user.getUserId(), book.getBookId(), 5, "Great book!");
        });

        assertEquals("User not found!", exception.getMessage());
        verify(userRepository, times(1)).findById(user.getUserId());
        verifyNoMoreInteractions(bookRepository);
        verifyNoInteractions(reviewRepository);
    }

    @Test
    void testAddOrUpdateReview_BookNotFound() {
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(bookRepository.findById(book.getBookId())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reviewService.addOrUpdateReview(user.getUserId(), book.getBookId(), 5, "Great book!");
        });

        assertEquals("Book not found!", exception.getMessage());
        verify(userRepository, times(1)).findById(user.getUserId());
        verify(bookRepository, times(1)).findById(book.getBookId());
        verifyNoInteractions(reviewRepository);
    }

    @Test
    void testDeleteReview() {
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(bookRepository.findById(book.getBookId())).thenReturn(Optional.of(book));
        when(reviewRepository.findByUserAndBook(user, book)).thenReturn(Optional.of(review));

        reviewService.deleteReview(user.getUserId(), book.getBookId());

        verify(userRepository, times(1)).findById(user.getUserId());
        verify(bookRepository, times(1)).findById(book.getBookId());
        verify(reviewRepository, times(1)).findByUserAndBook(user, book);
        verify(reviewRepository, times(1)).delete(review);
    }

    @Test
    void testDeleteReview_ThrowsExceptionWhenNotFound() {
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(bookRepository.findById(book.getBookId())).thenReturn(Optional.of(book));
        when(reviewRepository.findByUserAndBook(user, book)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reviewService.deleteReview(user.getUserId(), book.getBookId());
        });

        assertEquals("Review not found!", exception.getMessage());
        verify(userRepository, times(1)).findById(user.getUserId());
        verify(bookRepository, times(1)).findById(book.getBookId());
        verify(reviewRepository, times(1)).findByUserAndBook(user, book);
        verify(reviewRepository, never()).delete(any());
    }
}


