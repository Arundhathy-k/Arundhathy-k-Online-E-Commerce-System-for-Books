package com.kovan.integrationTestCases;

import com.kovan.entities.*;
import com.kovan.repository.*;
import com.kovan.service.OrderItemService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderItemServiceIT {

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    private Order savedOrder;
    private Book savedBook;

    @BeforeEach
    void setup() {

        User user = User.builder()
                .firstName("Test User")
                .email("Test@gmail.com")
                .build();
        userRepository.save(user);
        Address address = Address.builder()
                .city("Test City")
                .state("Test State")
                .user(user)
                .build();
        addressRepository.save(address);
        savedOrder = orderRepository.save(
                Order.builder()
                        .orderDate(LocalDateTime.now())
                        .user(user)
                        .shippingAddress(address)
                        .build());

        Category testCategory = Category.builder().name("Fiction").build();
        categoryRepository.save(testCategory);

        savedBook = bookRepository.save(
                Book.builder()
                        .title("Test Book")
                        .author("Author")
                        .price(BigDecimal.valueOf(20.00))
                        .category(testCategory)
                        .build()
        );
    }

    @AfterEach
    void cleanup() {

        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        bookRepository.deleteAll();
        categoryRepository.deleteAll();
        addressRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testAddOrderItem() {

        int quantity = 2;
        BigDecimal unitPrice = BigDecimal.valueOf(20.00);

        OrderItem orderItem = orderItemService.addOrderItem(
                savedOrder.getOrderId(),
                savedBook.getBookId(),
                quantity,
                unitPrice
        );

        assertNotNull(orderItem.getOrderItemId());
        assertEquals(savedOrder.getOrderId(), orderItem.getOrder().getOrderId());
        assertEquals(savedBook.getBookId(), orderItem.getBook().getBookId());
        assertEquals(quantity, orderItem.getQuantity());
        assertEquals(unitPrice, orderItem.getUnitPrice());
        assertEquals(unitPrice.multiply(BigDecimal.valueOf(quantity)), orderItem.getTotalPrice());
    }

    @Test
    void testGetAllOrderItems() {

        OrderItem orderItem1 = OrderItem.builder()
                .order(savedOrder)
                .book(savedBook)
                .quantity(1)
                .unitPrice(BigDecimal.valueOf(20.00))
                .totalPrice(BigDecimal.valueOf(20.00))
                .build();

        OrderItem orderItem2 = OrderItem.builder()
                .order(savedOrder)
                .book(savedBook)
                .quantity(3)
                .unitPrice(BigDecimal.valueOf(15.00))
                .totalPrice(BigDecimal.valueOf(45.00))
                .build();

        orderItemRepository.saveAll(List.of(orderItem1, orderItem2));

        List<OrderItem> orderItems = orderItemService.getAllOrderItems();

        assertEquals(2, orderItems.size());
    }

    @Test
    void testGetOrderItemById() {

        OrderItem orderItem = orderItemRepository.save(
                OrderItem.builder()
                        .order(savedOrder)
                        .book(savedBook)
                        .quantity(2)
                        .unitPrice(BigDecimal.valueOf(20.00))
                        .totalPrice(BigDecimal.valueOf(40.00))
                        .build()
        );

        OrderItem fetchedOrderItem = orderItemService.getOrderItemById(orderItem.getOrderItemId());

        assertNotNull(fetchedOrderItem);
        assertEquals(orderItem.getOrderItemId(), fetchedOrderItem.getOrderItemId());
    }

    @Test
    void testDeleteOrderItem() {

        OrderItem orderItem = orderItemRepository.save(
                OrderItem.builder()
                        .order(savedOrder)
                        .book(savedBook)
                        .quantity(2)
                        .unitPrice(BigDecimal.valueOf(20.00))
                        .totalPrice(BigDecimal.valueOf(40.00))
                        .build()
        );

        orderItemService.deleteOrderItem(orderItem.getOrderItemId());

        assertFalse(orderItemRepository.existsById(orderItem.getOrderItemId()));
    }
}
