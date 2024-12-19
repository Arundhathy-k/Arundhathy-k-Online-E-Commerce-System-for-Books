package com.kovan.integrationTestCases;

import com.kovan.entities.*;
import com.kovan.entities.Order;
import com.kovan.repository.*;
import com.kovan.service.OrderService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.ArrayList;

@SpringBootTest
class OrderServiceIT {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    private Order order;
    private Book testBook;
    private User testUser;
    private Address testAddress;
    private OrderItem orderItem;
    private Payment payment;

    @BeforeEach
    @Transactional
    void setup() {
        testUser = userRepository.save(
                User.builder()
                        .firstName("Ajay")
                        .email("Ajay@gmail.com")
                        .build()
        );

        testAddress = addressRepository.save(
                Address.builder()
                        .street("123 Test Street")
                        .city("Test City")
                        .state("Test State")
                        .build()
        );

        payment = paymentRepository.save(
                Payment.builder()
                        .paymentStatus("PAID")
                        .paymentMethod("Credit Card")
                        .amount(200.00)
                        .paymentReferenceNumber("234678f56")
                        .paymentDate(LocalDate.now())
                        .build()
        );

        testBook = bookRepository.save(
                Book.builder()
                        .title("Test Book")
                        .author("Author")
                        .price(20.00)
                        .stockQuantity(100)
                        .build()
        );

        orderItem = orderItemRepository.save(
                OrderItem.builder()
                        .book(testBook)
                        .quantity(2)
                        .unitPrice(20.00)
                        .totalPrice(40.00)
                        .build()
        );
    }

    @AfterEach
    void cleanup() {
        paymentRepository.deleteAll();
        orderRepository.deleteAll();
        addressRepository.deleteAll();
        userRepository.deleteAll();
        bookRepository.deleteAll();
        orderItemRepository.deleteAll();
    }

    @Test
    @Transactional
    void testCreateOrder() {
        order = orderRepository.save(
                Order.builder()
                        .orderDate(Instant.now().toString())
                        .user(testUser)
                        .shippingAddress(testAddress)
                        .orderItems(new ArrayList<>(List.of(orderItem)))
                        .orderStatus("PENDING")
                        .build()
        );

        Order savedOrder = orderService.createOrder(order);

        assertNotNull(savedOrder.getOrderId());
        assertEquals("PENDING", savedOrder.getOrderStatus());
        assertEquals(order.getUser(), savedOrder.getUser());
        assertEquals(order.getShippingAddress(), savedOrder.getShippingAddress());
        assertEquals(order.getPayment(), savedOrder.getPayment());
    }

    @Test
    @Transactional
    void testGetOrderById() {
        order = orderRepository.save(
                Order.builder()
                        .orderDate(Instant.now().toString())
                        .user(testUser)
                        .shippingAddress(testAddress)
                        .orderItems(new ArrayList<>(List.of(orderItem)))
                        .orderStatus("PENDING")
                        .build()
        );

        Order fetchedOrder = orderService.getOrderById(order.getOrderId());

        assertNotNull(fetchedOrder);
        assertEquals(order.getOrderId(), fetchedOrder.getOrderId());
        assertEquals("PENDING", fetchedOrder.getOrderStatus());
    }

    @Test
    @Transactional
    void testGetAllOrders() {
        order = orderRepository.save(
                Order.builder()
                        .orderDate(Instant.now().toString())
                        .user(testUser)
                        .shippingAddress(testAddress)
                        .orderItems(new ArrayList<>(List.of(orderItem)))
                        .orderStatus("PENDING")
                        .build()
        );

        List<Order> orders = orderService.getAllOrders();

        assertNotNull(orders);
        assertEquals(1, orders.size());
    }

    @Test
    @Transactional
    void testUpdateOrder() {
        order = orderRepository.save(
                Order.builder()
                        .orderDate(Instant.now().toString())
                        .user(testUser)
                        .shippingAddress(testAddress)
                        .orderItems(new ArrayList<>(List.of(orderItem)))
                        .orderStatus("PENDING")
                        .build()
        );

        OrderItem newItem = OrderItem.builder()
                .book(testBook)
                .quantity(3)
                .unitPrice(20.00)
                .totalPrice(60.00)
                .build();

        Order updatedOrder = Order.builder()
                .orderStatus("SHIPPED")
                .orderItems(new ArrayList<>(List.of(newItem)))
                .build();

        Order resultOrder = orderService.updateOrder(order.getOrderId(), updatedOrder);

        assertEquals("SHIPPED", resultOrder.getOrderStatus());
        assertEquals(1, resultOrder.getOrderItems().size());
        assertEquals(2, resultOrder.getOrderItems().getFirst().getQuantity());
    }

    @Test
    @Transactional
    void testCancelOrder() {
        order = orderRepository.save(
                Order.builder()
                        .orderDate(Instant.now().toString())
                        .user(testUser)
                        .shippingAddress(testAddress)
                        .orderItems(new ArrayList<>(List.of(orderItem)))
                        .orderStatus("PENDING")
                        .build()
        );

        payment = paymentRepository.save(
                Payment.builder()
                        .paymentStatus("COMPLETED")
                        .transactionQuantity(2)
                        .build()
        );

        order.setPayment(payment);
        orderRepository.save(order);

        orderService.cancelOrder(order.getOrderId());

        Order canceledOrder = orderRepository.findById(order.getOrderId()).orElse(null);
        assertNotNull(canceledOrder);
        assertEquals("CANCELLED", canceledOrder.getOrderStatus());
        assertEquals("REFUNDED", canceledOrder.getPayment().getPaymentStatus());
        assertEquals(102, testBook.getStockQuantity()); // Ensuring stock increment after cancellation
    }
}

