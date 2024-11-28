package com.kovan.integrationTestCases;

import com.kovan.entities.Book;
import com.kovan.entities.Category;
import com.kovan.entities.Review;
import com.kovan.entities.User;
import com.kovan.repository.BookRepository;
import com.kovan.repository.CategoryRepository;
import com.kovan.repository.ReviewRepository;
import com.kovan.repository.UserRepository;
import com.kovan.service.ReviewService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReviewServiceIT {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Book testBook;
    private User testUser;

    @BeforeAll
    void setup() {

        testUser = userRepository.save(
                User.builder()
                        .firstName("Jane")
                        .email("jane@example.com")
                        .build()
        );
        Category testCategory = Category.builder().name("Fiction").build();
        categoryRepository.save(testCategory);

        testBook = bookRepository.save(
                Book.builder()
                        .title("Test Book")
                        .author("Test Author")
                        .isbn("123456789")
                        .category(testCategory)
                        .build()
        );
    }

    @AfterEach
    void cleanup() {
        reviewRepository.deleteAll();
    }

    @AfterAll
    void teardown() {
        bookRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void testAddOrUpdateReview_AddNewReview() {

        Review review = reviewService.addOrUpdateReview(testUser.getUserId(), testBook.getBookId(), 5, "Great book!");

        assertNotNull(review.getReviewId());
        assertEquals(5, review.getRating());
        assertEquals("Great book!", review.getComment());
        assertEquals(testUser.getUserId(), review.getUser().getUserId());
        assertEquals(testBook.getBookId(), review.getBook().getBookId());
    }

    @Test
    void testAddOrUpdateReview_UpdateExistingReview() {

        Review existingReview = reviewRepository.save(
                Review.builder()
                        .book(testBook)
                        .user(testUser)
                        .rating(3)
                        .comment("Good book")
                        .reviewDate(LocalDate.now())
                        .build()
        );


        Review updatedReview = reviewService.addOrUpdateReview(testUser.getUserId(), testBook.getBookId(), 4, "Very enjoyable!");

        assertEquals(existingReview.getReviewId(), updatedReview.getReviewId());
        assertEquals(4, updatedReview.getRating());
        assertEquals("Very enjoyable!", updatedReview.getComment());
    }

    @Test
    void testDeleteReview() {

        Review review = reviewRepository.save(
                Review.builder()
                        .book(testBook)
                        .user(testUser)
                        .rating(4)
                        .comment("Good read")
                        .reviewDate(LocalDate.now())
                        .build()
        );


        reviewService.deleteReview(testBook.getBookId());

        assertFalse(reviewRepository.existsById(review.getReviewId()));
    }

    @Test
    void testAddOrUpdateReview_UserNotFound() {

        Long nonExistentUserId = 999L;

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.addOrUpdateReview(nonExistentUserId, testBook.getBookId(), 5, "Amazing!");
        });

        assertEquals("User not found!", exception.getMessage());
    }

    @Test
    void testAddOrUpdateReview_BookNotFound() {

        Long nonExistentBookId = 999L;

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.addOrUpdateReview(testUser.getUserId(), nonExistentBookId, 5, "Amazing!");
        });

        assertEquals("Book not found!", exception.getMessage());
    }

    @Test
    void testDeleteReview_ReviewNotFound() {

        Long nonExistentBookId = 999L;

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.deleteReview(nonExistentBookId);
        });

        assertEquals("Review not found!", exception.getMessage());
    }
}

