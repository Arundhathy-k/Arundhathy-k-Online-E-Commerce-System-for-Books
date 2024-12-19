package com.kovan.repository;

import com.kovan.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Long> {

    Optional<CartItem> findByCartCartId(Long cartId);
}
