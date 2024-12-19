package com.kovan.service;

import com.kovan.entities.Book;
import com.kovan.entities.Order;
import com.kovan.entities.Payment;
import com.kovan.repository.BookRepository;
import com.kovan.repository.OrderRepository;
import com.kovan.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository, BookRepository bookRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public Payment processPayment(Long orderId, Payment paymentDetails) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found!"));

        if (!order.getOrderStatus().equalsIgnoreCase("PENDING")) {
            throw new RuntimeException("Payment can only be processed for pending orders!");
        }

        paymentDetails.setPaymentDate(LocalDate.now());
        paymentDetails.setTransactionType("Purchase");
        Payment savedPayment = paymentRepository.save(paymentDetails);

        order.setPayment(savedPayment);

        if ("COMPLETED".equalsIgnoreCase(savedPayment.getPaymentStatus())) {
            order.setOrderStatus("SHIPPED");
            decreaseStock(order);
        } else if ("FAILED".equalsIgnoreCase(savedPayment.getPaymentStatus())) {
            order.setOrderStatus("PENDING");
        }

        orderRepository.save(order);
        return savedPayment;
    }

    public Payment getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found!"));
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Transactional
    public Payment updatePayment(Long paymentId, Payment updatedPaymentDetails) {
        Payment existingPayment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found!"));

        existingPayment.setPaymentMethod(updatedPaymentDetails.getPaymentMethod());
        existingPayment.setPaymentStatus(updatedPaymentDetails.getPaymentStatus());
        existingPayment.setAmount(updatedPaymentDetails.getAmount());
        existingPayment.setPaymentReferenceNumber(updatedPaymentDetails.getPaymentReferenceNumber());
        existingPayment.setPaymentDate(LocalDate.now());

        Payment savedPayment = paymentRepository.save(existingPayment);

        Order order = orderRepository.findByPayment(savedPayment)
                .orElseThrow(() -> new RuntimeException("Order not found for payment!"));

        if ("COMPLETED".equalsIgnoreCase(savedPayment.getPaymentStatus())) {
            order.setOrderStatus("SHIPPED");
            decreaseStock(order);
        } else if ("FAILED".equalsIgnoreCase(savedPayment.getPaymentStatus())) {
            order.setOrderStatus("PENDING");
        }
        orderRepository.save(order);

        return savedPayment;
    }

    public void deletePayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found!"));

        if ("COMPLETED".equalsIgnoreCase(payment.getPaymentStatus())) {
            throw new RuntimeException("Cannot delete a completed payment associated with an order!");
        }

        paymentRepository.deleteById(paymentId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }

    private void decreaseStock(Order order) {
        order.getOrderItems().forEach(orderItem -> {
            Book book = bookRepository.findById(orderItem.getBook().getBookId())
                    .orElseThrow(() -> new RuntimeException("Book not found!"));
            if (book.getStockQuantity() < orderItem.getQuantity()) {
                throw new RuntimeException("Insufficient stock for book: " + book.getTitle());
            }
            book.setStockQuantity(book.getStockQuantity() - orderItem.getQuantity());
            bookRepository.save(book);
        });
    }
}

