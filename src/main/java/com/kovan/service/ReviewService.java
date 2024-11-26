package com.kovan.service;

import com.kovan.entities.Review;
import com.kovan.repository.BookRepository;
import com.kovan.repository.ReviewRepository;
import com.kovan.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.Optional;

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

    public Review addOrUpdateReview(Long userId, Long bookId, int rating, String comment) {

        Optional<Review> existingReview = reviewRepository.findByBookBookId(bookId);

        Review review = existingReview.orElseGet(() -> {
            Review newReview = new Review();
            newReview.setUser(userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found!")));
            newReview.setBook(bookRepository.findById(bookId)
                    .orElseThrow(() -> new RuntimeException("Book not found!")));
            return newReview;
        });

        review.setRating(rating);
        review.setComment(comment);
        review.setReviewDate(LocalDate.now());

        return reviewRepository.save(review);
    }

    public void deleteReview(Long userId, Long bookId) {
        Review review = reviewRepository.findByBookBookId(userId)
                .orElseThrow(() -> new RuntimeException("Review not found!"));

        reviewRepository.delete(review);
    }
}

