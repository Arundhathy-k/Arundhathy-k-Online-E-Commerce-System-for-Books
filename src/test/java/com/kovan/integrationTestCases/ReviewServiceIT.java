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
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
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

    @BeforeEach
    @Transactional
    void setup() {
        testUser = userRepository.save(
                User.builder()
                        .firstName("Jane")
                        .lastName("Doe")
                        .email("jane@example.com")
                        .passwordHash("password")
                        .build()
        );

        Category testCategory = categoryRepository.save(
                Category.builder()
                        .name("Fiction")
                        .description("Fictional books category")
                        .build()
        );

        testBook = bookRepository.save(
                Book.builder()
                        .title("Test Book")
                        .author("Test Author")
                        .isbn("123456789")
                        .category(categoryRepository.findById(testCategory.getCategoryId()).orElseThrow())
                        .stockQuantity(100)
                        .price(19.99)
                        .description("A test book for integration testing")
                        .publicationYear(2023)
                        .publisher("Test Publisher")
                        .build()
        );
    }

    @AfterEach
    void cleanup() {
        reviewRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    @Transactional
    void testAddOrUpdateReview_AddNewReview() {
        Review review = reviewService.addOrUpdateReview(testUser.getUserId(), testBook.getBookId(), 5, "Great book!");

        assertNotNull(review.getReviewId());
        assertEquals(5, review.getRating());
        assertEquals("Great book!", review.getComment());
        assertEquals(testUser.getUserId(), review.getUser().getUserId());
        assertEquals(testBook.getBookId(), review.getBook().getBookId());
    }

    @Test
    @Transactional
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
    @Transactional
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

        reviewService.deleteReview(testUser.getUserId(), testBook.getBookId());

        assertFalse(reviewRepository.existsById(review.getReviewId()));
    }

    @Test
    @Transactional
    void testAddOrUpdateReview_UserNotFound() {
        Long nonExistentUserId = 999L;

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.addOrUpdateReview(nonExistentUserId, testBook.getBookId(), 5, "Amazing!");
        });

        assertEquals("User not found!", exception.getMessage());
    }

    @Test
    @Transactional
    void testAddOrUpdateReview_BookNotFound() {
        Long nonExistentBookId = 999L;

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.addOrUpdateReview(testUser.getUserId(), nonExistentBookId, 5, "Amazing!");
        });

        assertEquals("Book not found!", exception.getMessage());
    }

}


