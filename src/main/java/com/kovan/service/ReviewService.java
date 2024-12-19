package com.kovan.service;

import com.kovan.entities.Book;
import com.kovan.entities.Review;
import com.kovan.entities.User;
import com.kovan.repository.BookRepository;
import com.kovan.repository.ReviewRepository;
import com.kovan.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Review addOrUpdateReview(Long userId, Long bookId, int rating, String comment) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found!"));

        Review existingReview = reviewRepository.findByUserAndBook(user, book)
                .orElse(null);

        if (existingReview != null) {
            existingReview.setRating(rating);
            existingReview.setComment(comment);
            existingReview.setReviewDate(LocalDate.now());
            return reviewRepository.save(existingReview);
        } else {
            Review newReview = Review.builder()
                    .user(user)
                    .book(book)
                    .rating(rating)
                    .comment(comment)
                    .reviewDate(LocalDate.now())
                    .build();
            return reviewRepository.save(newReview);
        }
    }

    @Transactional
    public void deleteReview(Long userId, Long bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found!"));

        Review review = reviewRepository.findByUserAndBook(user, book)
                .orElseThrow(() -> new RuntimeException("Review not found!"));

        if (!review.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("You can only delete your own reviews!");
        }

        reviewRepository.delete(review);
    }
}

