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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

        private Order order;

    @BeforeEach
        @Transactional
        void setup() {
          User testUser = User.builder()
                            .firstName("Ajay")
                            .email("Ajay@gmail.com")
                            .build();
            userRepository.save(testUser);

            Address testAddress = Address.builder()
                            .street("123 Test Street")
                            .city("Test City")
                            .state("Test State")
                            .build();
            addressRepository.save(testAddress);
        Payment payment = Payment.builder()
                .paymentStatus("PAID")
                .paymentMethod("Credit Card")
                .amount(200.00)
                .paymentReferenceNumber("234678f56")
                .paymentDate(LocalDate.now())
                .build();
            paymentRepository.save(payment);
            order = Order.builder()
                    .orderDate(LocalDate.now())
                    .orderStatus("PROCESSING")
                    .user(testUser)
                    .shippingAddress(testAddress)
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
        void testCreateOrder() {

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

            Order fetchedOrder = orderService.getOrderById(order.getOrderId());

            assertNotNull(fetchedOrder);
            assertEquals(order.getOrderId(), fetchedOrder.getOrderId());
            assertEquals("PROCESSING", fetchedOrder.getOrderStatus());
        }

        @Test
        @Transactional
        void testGetAllOrders() {

            orderRepository.saveAll(List.of(order
            ));

            List<Order> orders = orderService.getAllOrders();

            assertNotNull(orders);
            assertEquals(1, orders.size());
        }

        @Test
        @Transactional
        void testUpdateOrder() {

            Order updatedOrder = Order.builder()
                    .orderStatus("SHIPPED")
                    .build();

            Order resultOrder = orderService.updateOrder(order.getOrderId(), updatedOrder);

            assertEquals("SHIPPED", resultOrder.getOrderStatus());
        }

        @Test
        @Transactional
        void testCancelOrder() {

            orderService.cancelOrder(order.getOrderId());

            Order canceledOrder = orderRepository.findById(order.getOrderId()).orElse(null);
            assertNotNull(canceledOrder);
            assertEquals("CANCELLED", canceledOrder.getOrderStatus());
        }
    }
