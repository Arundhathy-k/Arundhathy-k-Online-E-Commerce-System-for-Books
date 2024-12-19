package com.kovan.junitTestCases;

import com.kovan.entities.*;
import com.kovan.repository.BookRepository;
import com.kovan.repository.OrderRepository;
import com.kovan.repository.PaymentRepository;
import com.kovan.service.AddressService;
import com.kovan.service.OrderService;
import com.kovan.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Instant;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserService userService;

    @Mock
    private AddressService addressService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private OrderService orderService;

    private final User user = User.builder()
            .userId(1L)
            .firstName("Test User")
            .build();

    private final Address address = Address.builder()
            .addressId(1L)
            .street("123 Main St")
            .city("Test City")
            .build();

    private final Book book = Book.builder()
            .bookId(1L)
            .title("Test Book")
            .stockQuantity(10)
            .build();

    private final OrderItem orderItem = OrderItem.builder()
            .orderItemId(1L)
            .book(book)
            .quantity(2)
            .unitPrice(20.00)
            .totalPrice(40.00)
            .build();

    private final Order order = Order.builder()
            .orderId(1L)
            .orderDate(Instant.now().toString())
            .orderStatus("PENDING")
            .user(user)
            .shippingAddress(address)
            .orderItems(List.of(orderItem))
            .build();

    @Test
    void testCreateOrder() {
        when(userService.getUserById(user.getUserId())).thenReturn(user);
        when(addressService.getAddressesById(address.getAddressId())).thenReturn(address);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.createOrder(order);

        assertNotNull(result);
        assertEquals(order.getOrderId(), result.getOrderId());
        assertEquals(order.getOrderStatus(), result.getOrderStatus());
        verify(userService, times(1)).getUserById(user.getUserId());
        verify(addressService, times(1)).getAddressesById(address.getAddressId());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testGetOrderById() {
        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));

        Order result = orderService.getOrderById(order.getOrderId());

        assertNotNull(result);
        assertEquals(order.getOrderId(), result.getOrderId());
        verify(orderRepository, times(1)).findById(order.getOrderId());
    }

    @Test
    void testGetOrderByIdThrowsExceptionWhenNotFound() {
        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.getOrderById(order.getOrderId()));
        verify(orderRepository, times(1)).findById(order.getOrderId());
    }

    @Test
    void testGetAllOrders() {
        List<Order> orders = List.of(order);
        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> result = orderService.getAllOrders();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(order, result.get(0));
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void testUpdateOrder() {
        Order updatedOrder = Order.builder()
                .orderStatus("COMPLETED")
                .build();

        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.updateOrder(order.getOrderId(), updatedOrder);

        assertNotNull(result);
        assertEquals("COMPLETED", result.getOrderStatus());
        verify(orderRepository, times(1)).findById(order.getOrderId());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testCancelOrder() {
        Payment payment = Payment.builder()
                .paymentId(1L)
                .paymentStatus("COMPLETED")
                .transactionQuantity(2)
                .build();

        order.setPayment(payment);

        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(bookRepository.findById(book.getBookId())).thenReturn(Optional.of(book));

        orderService.cancelOrder(order.getOrderId());

        assertEquals("CANCELLED", order.getOrderStatus());
        verify(orderRepository, times(1)).findById(order.getOrderId());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(bookRepository, times(1)).save(any(Book.class));
    }
}

