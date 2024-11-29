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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @BeforeEach
    @Transactional
    void setup() {
        User testUser = User.builder()
                .firstName("John")
                .email("john@example.com")
                .build();
        userRepository.save(testUser);

         Address testAddress = Address.builder()
                .street("123 Test Street")
                .city("Test City")
                .state("Test State")
                .user(testUser)
                .build();
        addressRepository.save(testAddress);

        order = Order.builder()
                        .orderDate(LocalDateTime.now())
                        .orderStatus("PENDING")
                        .user(testUser)
                        .shippingAddress(testAddress)
                        .build();
        order = orderRepository.saveAndFlush(order);
    }

    @AfterEach
    void cleanup() {
        orderRepository.deleteAll();
        paymentRepository.deleteAll();
        addressRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    void testProcessPayment() {

        Payment paymentDetails = Payment.builder()
                .paymentMethod("Credit Card")
                .paymentStatus("COMPLETED")
                .amount(BigDecimal.valueOf(100.00))
                .paymentReferenceNumber("REF123456")
                .order(orderRepository.findById(order.getOrderId()).orElse(Order.builder().build()))
                .build();

        Payment savedPayment = paymentService.processPayment(order.getOrderId(), paymentDetails);

        assertNotNull(savedPayment.getPaymentId());
        assertEquals("COMPLETED", savedPayment.getPaymentStatus());
        assertEquals(order.getOrderId(), savedPayment.getOrder().getOrderId());

        Order updatedOrder = orderRepository.findById(order.getOrderId()).orElse(null);
        assertNotNull(updatedOrder);
        assertEquals("SHIPPED", updatedOrder.getOrderStatus());
    }

    @Test
    @Transactional
    void testGetPaymentById() {

        Payment payment = paymentRepository.save(
                Payment.builder()
                        .paymentDate(LocalDate.now())
                        .paymentMethod("Debit Card")
                        .paymentStatus("PENDING")
                        .amount(new BigDecimal("50.00"))
                        .paymentReferenceNumber("REF654321")
                        .order(orderRepository.findById(order.getOrderId()).orElse(Order.builder().build()))
                        .build()
        );

        Payment fetchedPayment = paymentService.getPaymentById(payment.getPaymentId());

        assertNotNull(fetchedPayment);
        assertEquals(payment.getPaymentId(), fetchedPayment.getPaymentId());
        assertEquals("PENDING", fetchedPayment.getPaymentStatus());
    }

    @Test
    @Transactional
    void testGetAllPayments() {

        paymentRepository.saveAll(List.of(
                Payment.builder()
                        .paymentDate(LocalDate.now())
                        .paymentMethod("UPI")
                        .paymentStatus("PENDING")
                        .amount(new BigDecimal("200.00"))
                        .paymentReferenceNumber("REFUPI001")
                        .order(orderRepository.findById(order.getOrderId()).orElse(Order.builder().build()))
                        .build(),
                Payment.builder()
                        .paymentDate(LocalDate.now())
                        .paymentMethod("Net Banking")
                        .paymentStatus("COMPLETED")
                        .amount(new BigDecimal("150.00"))
                        .paymentReferenceNumber("REFNET123")
                        .order(orderRepository.findById(order.getOrderId()).orElse(Order.builder().build()))
                        .build()
        ));

          List<Payment> payments = paymentService.getAllPayments();

        assertEquals(2, payments.size());
    }

    @Test
    @Transactional
    void testUpdatePayment() {

        Payment payment = paymentRepository.save(
                Payment.builder()
                        .paymentDate(LocalDate.now())
                        .paymentMethod("Cash")
                        .paymentStatus("PENDING")
                        .amount(new BigDecimal("300.00"))
                        .paymentReferenceNumber("REFCASH001")
                        .order(orderRepository.findById(order.getOrderId()).orElse(Order.builder().build()))
                        .build()
        );

        Payment updatedPaymentDetails = Payment.builder()
                .paymentMethod("Credit Card")
                .paymentStatus("COMPLETED")
                .amount(new BigDecimal("300.00"))
                .order(orderRepository.findById(order.getOrderId()).orElse(Order.builder().build()))
                .paymentReferenceNumber("REFUPDATED")
                .build();

        Payment updatedPayment = paymentService.updatePayment(payment.getPaymentId(), updatedPaymentDetails);

        assertEquals("COMPLETED", updatedPayment.getPaymentStatus());
        assertEquals("REFUPDATED", updatedPayment.getPaymentReferenceNumber());
        assertEquals("Credit Card", updatedPayment.getPaymentMethod());

        Order updatedOrder = orderRepository.findById(order.getOrderId()).orElse(null);
        assertNotNull(updatedOrder);
        assertEquals("SHIPPED", updatedOrder.getOrderStatus());
    }

    @Test
    @Transactional
    void testDeletePayment() {

        Payment payment = paymentRepository.save(
                Payment.builder()
                        .paymentDate(LocalDate.now())
                        .paymentMethod("Wallet")
                        .paymentStatus("PENDING")
                        .amount(new BigDecimal("400.00"))
                        .paymentReferenceNumber("REFWALLET001")
                        .order(orderRepository.findById(order.getOrderId()).orElse(Order.builder().build()))
                        .build()
        );

        paymentService.deletePayment(payment.getPaymentId());

        assertFalse(paymentRepository.existsById(payment.getPaymentId()));
    }

}

