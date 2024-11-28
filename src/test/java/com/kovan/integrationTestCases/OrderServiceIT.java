package com.kovan.integrationTestCases;

import com.kovan.entities.Address;
import com.kovan.entities.Order;
import com.kovan.entities.Payment;
import com.kovan.entities.User;
import com.kovan.repository.AddressRepository;
import com.kovan.repository.OrderRepository;
import com.kovan.repository.PaymentRepository;
import com.kovan.repository.UserRepository;
import com.kovan.service.OrderService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

        private User testUser;
        private Address testAddress;

        @BeforeEach
        void setup() {
            String uniqueEmail = "john" + System.currentTimeMillis() + "@example.com";
            testUser = User.builder()
                            .firstName("Ajay")
                            .email(uniqueEmail)
                            .build();
            userRepository.save(testUser);

            testAddress = Address.builder()
                            .street("123 Test Street")
                            .city("Test City")
                            .state("Test State")
                            .user(testUser)
                            .build();
            addressRepository.save(testAddress);
        }

        @AfterEach
        void cleanup() {

            orderRepository.deleteAll();
            paymentRepository.deleteAll();
            addressRepository.deleteAll();
            userRepository.deleteAll();
        }

        @Test
        void testCreateOrder() {

            Payment payment = paymentRepository.save(
                    Payment.builder()
                            .paymentStatus("PAID")
                            .paymentMethod("Credit Card")
                            .build()
            );

            Order order = Order.builder()
                    .orderDate(LocalDateTime.now())
                    .orderStatus("CREATED")
                    .user(testUser)
                    .shippingAddress(testAddress)
                    .payment(payment)
                    .build();

            Order savedOrder = orderService.createOrder(order);

            assertNotNull(savedOrder.getOrderId());
            assertEquals("CREATED", savedOrder.getOrderStatus());
            assertEquals(testUser.getUserId(), savedOrder.getUser().getUserId());
            assertEquals(testAddress.getAddressId(), savedOrder.getShippingAddress().getAddressId());
            assertEquals(payment.getPaymentId(), savedOrder.getPayment().getPaymentId());
        }

        @Test
        void testGetOrderById() {

            Order order = orderRepository.save(
                    Order.builder()
                            .orderDate(LocalDateTime.now())
                            .orderStatus("PROCESSING")
                            .user(testUser)
                            .shippingAddress(testAddress)
                            .build()
            );

            Order fetchedOrder = orderService.getOrderById(order.getOrderId());

            assertNotNull(fetchedOrder);
            assertEquals(order.getOrderId(), fetchedOrder.getOrderId());
            assertEquals("PROCESSING", fetchedOrder.getOrderStatus());
        }

        @Test
        void testGetAllOrders() {

            orderRepository.saveAll(List.of(
                    Order.builder()
                            .orderDate(LocalDateTime.now())
                            .orderStatus("CREATED")
                            .user(testUser)
                            .shippingAddress(testAddress)
                            .build(),
                    Order.builder()
                            .orderDate(LocalDateTime.now().minusDays(1))
                            .orderStatus("SHIPPED")
                            .user(testUser)
                            .shippingAddress(testAddress)
                            .build()
            ));

            List<Order> orders = orderService.getAllOrders();

            assertEquals(2, orders.size());
        }

        @Test
        void testUpdateOrder() {

            Order order = orderRepository.save(
                    Order.builder()
                            .orderDate(LocalDateTime.now())
                            .orderStatus("CREATED")
                            .user(testUser)
                            .shippingAddress(testAddress)
                            .build()
            );

            Order updatedOrder = Order.builder()
                    .orderStatus("SHIPPED")
                    .build();

            Order resultOrder = orderService.updateOrder(order.getOrderId(), updatedOrder);

            assertEquals("SHIPPED", resultOrder.getOrderStatus());
        }

        @Test
        void testCancelOrder() {

            Order order = orderRepository.save(
                    Order.builder()
                            .orderDate(LocalDateTime.now())
                            .orderStatus("PROCESSING")
                            .user(testUser)
                            .shippingAddress(testAddress)
                            .build()
            );

            orderService.cancelOrder(order.getOrderId());

            Order canceledOrder = orderRepository.findById(order.getOrderId()).orElse(null);
            assertNotNull(canceledOrder);
            assertEquals("CANCELLED", canceledOrder.getOrderStatus());
        }
    }
