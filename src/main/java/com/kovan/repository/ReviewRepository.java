package com.kovan.repository;

import com.kovan.entities.Book;
import com.kovan.entities.Review;
import com.kovan.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Long> {

    Optional<Review> findByUserAndBook(User user, Book book);
}
