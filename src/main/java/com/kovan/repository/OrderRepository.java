package com.kovan.repository;

import com.kovan.entities.Order;
import com.kovan.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
   Optional<Order> findByPayment(Payment savedPayment);
}
