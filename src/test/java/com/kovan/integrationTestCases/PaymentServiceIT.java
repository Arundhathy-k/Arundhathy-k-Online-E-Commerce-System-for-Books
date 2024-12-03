package com.kovan.integrationTestCases;

import com.kovan.entities.Address;
import com.kovan.entities.Order;
import com.kovan.entities.Payment;
import com.kovan.entities.User;
import com.kovan.repository.AddressRepository;
import com.kovan.repository.OrderRepository;
import com.kovan.repository.PaymentRepository;
import com.kovan.repository.UserRepository;
import com.kovan.service.PaymentService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PaymentServiceIT {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    private Order order;
    private Payment payment;

    @BeforeEach
    @Transactional
    void setup() {
        User user = User.builder()
                .firstName("Alice")
                .email("alice@example.com")
                .build();
        userRepository.save(user);

        Address address = Address.builder()
                .street("456 Elm St")
                .city("Wonderland")
                .state("Dream")
                .build();
        addressRepository.save(address);
        payment = Payment.builder()
                .paymentMethod("Debit Card")
                .paymentStatus("PENDING")
                .amount(100.00)
                .paymentReferenceNumber("PAY124")
                .paymentDate(LocalDate.now())
                .build();
        paymentRepository.save(payment);
        order = Order.builder()
                .orderDate(LocalDate.now())
                .orderStatus("PENDING")
                .user(user)
                .shippingAddress(address)
                .payment(payment)
                .build();
        orderRepository.save(order);
    }

    @AfterEach
    void cleanup() {
        paymentRepository.deleteAll();
        orderRepository.deleteAll();
        addressRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    void testProcessPayment() {
        Payment paymentDetails = Payment.builder()
                .paymentMethod("Credit Card")
                .paymentStatus("COMPLETED")
                .amount(150.00)
                .paymentReferenceNumber("PAY123")
                .build();

        Payment savedPayment = paymentService.processPayment(order.getOrderId(), paymentDetails);

        assertNotNull(savedPayment.getPaymentId());
        assertEquals("COMPLETED", savedPayment.getPaymentStatus());

        Order updatedOrder = orderRepository.findById(order.getOrderId()).orElse(null);
        assertNotNull(updatedOrder);
        assertEquals("SHIPPED", updatedOrder.getOrderStatus());
        assertEquals(savedPayment.getPaymentId(), updatedOrder.getPayment().getPaymentId());
    }

    @Test
    @Transactional
    void testGetPaymentById() {

        Payment fetchedPayment = paymentService.getPaymentById(payment.getPaymentId());

        assertNotNull(fetchedPayment);
        assertEquals(payment.getPaymentId(), fetchedPayment.getPaymentId());
        assertEquals("PENDING", fetchedPayment.getPaymentStatus());
    }

    @Test
    @Transactional
    void testGetAllPayments() {
        Payment payment1 = Payment.builder()
                .paymentMethod("UPI")
                .paymentStatus("PENDING")
                .amount(200.00)
                .paymentReferenceNumber("PAY125")
                .build();
        Payment payment2 = Payment.builder()
                .paymentMethod("Net Banking")
                .paymentStatus("COMPLETED")
                .amount(250.00)
                .paymentReferenceNumber("PAY126")
                .build();

        paymentRepository.saveAll(List.of(payment1, payment2));

        List<Payment> payments = paymentService.getAllPayments();

        assertNotNull(payments);
        assertEquals(3, payments.size());
    }

    @Test
    @Transactional
    void testUpdatePayment() {

        Payment updatedDetails = Payment.builder()
                .paymentMethod("Card")
                .paymentStatus("COMPLETED")
                .amount(350.00)
                .paymentReferenceNumber("PAY_UPDATED")
                .build();

        Payment updatedPayment = paymentService.updatePayment(payment.getPaymentId(), updatedDetails);

        assertEquals("COMPLETED", updatedPayment.getPaymentStatus());
        assertEquals("Card", updatedPayment.getPaymentMethod());
        assertEquals(350.00, updatedPayment.getAmount());
        assertEquals("PAY_UPDATED", updatedPayment.getPaymentReferenceNumber());
    }

    @Test
    @Transactional
    void testDeletePayment() {
         payment = Payment.builder()
                .paymentMethod("Wallet")
                .paymentStatus("PENDING")
                .amount(400.00)
                .paymentReferenceNumber("PAY128")
                .paymentDate(LocalDate.now())
                .build();
        payment = paymentRepository.save(payment);

        paymentService.deletePayment(payment.getPaymentId());

        assertFalse(paymentRepository.existsById(payment.getPaymentId()));
    }

    @Test
    @Transactional
    void testDeleteCompletedPaymentThrowsException() {
         payment = Payment.builder()
                .paymentMethod("Wallet")
                .paymentStatus("COMPLETED")
                .amount(450.00)
                .paymentReferenceNumber("PAY129")
                .paymentDate(LocalDate.now())
                .build();
        payment = paymentRepository.save(payment);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            paymentService.deletePayment(payment.getPaymentId());
        });

        assertEquals("Cannot delete a completed payment associated with an order!", exception.getMessage());
    }
}
