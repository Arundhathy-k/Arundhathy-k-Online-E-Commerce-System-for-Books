package com.kovan.service;

import com.kovan.entities.Order;
import com.kovan.entities.Payment;
import com.kovan.repository.OrderRepository;
import com.kovan.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    private final OrderRepository orderRepository;

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    public Payment processPayment(Long orderId, Payment paymentDetails) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found!"));

        if (!order.getOrderStatus().equals("PENDING")) {
            throw new RuntimeException("Payment can only be processed for pending orders!");
        }

        paymentDetails.setOrder(order);
        paymentDetails.setPaymentDate(LocalDate.now());

        Payment savedPayment = paymentRepository.save(paymentDetails);

        if (paymentDetails.getPaymentStatus().equals("COMPLETED")) {
            order.setOrderStatus("SHIPPED");
            orderRepository.save(order);
        }

        return savedPayment;
    }
}

