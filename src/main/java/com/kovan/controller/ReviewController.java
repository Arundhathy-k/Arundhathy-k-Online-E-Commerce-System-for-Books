package com.kovan.controller;

import com.kovan.entities.Review;
import com.kovan.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/add")
    public ResponseEntity<Review> addOrUpdateReview(@RequestParam Long userId,
                                                    @RequestParam Long bookId,
                                                    @RequestParam int rating,
                                                    @RequestParam(required = false) String comment) {
        Review review = reviewService.addOrUpdateReview(userId, bookId, rating, comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Void> deleteReview(@RequestParam Long userId,
                                             @RequestParam Long bookId) {
        reviewService.deleteReview(userId);
        return ResponseEntity.noContent().build();
    }
}

