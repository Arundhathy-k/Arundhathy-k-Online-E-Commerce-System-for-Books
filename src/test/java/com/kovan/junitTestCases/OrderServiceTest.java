package com.kovan.junitTestCases;

import com.kovan.entities.Address;
import com.kovan.entities.Order;
import com.kovan.entities.User;
import com.kovan.repository.OrderRepository;
import com.kovan.service.AddressService;
import com.kovan.service.OrderService;
import com.kovan.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static java.util.Optional.of;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserService userService;

    @Mock
    private AddressService addressService;

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

    private final Order order = Order.builder()
            .orderId(1L)
            .orderDate(LocalDateTime.now())
            .orderStatus("PENDING")
            .user(user)
            .shippingAddress(address)
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
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void testGetOrderById() {

        when(orderRepository.findById(order.getOrderId())).thenReturn(of(order));

        Order result = orderService.getOrderById(order.getOrderId());

        assertNotNull(result);
        assertEquals(order.getOrderId(), result.getOrderId());
        verify(orderRepository, times(1)).findById(order.getOrderId());
    }

    @Test
    void testGetOrderByIdThrowsExceptionWhenNotFound() {

        when(orderRepository.findById(order.getOrderId())).thenReturn(java.util.Optional.empty());

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
        assertEquals(order, result.getFirst());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void testUpdateOrder() {

        Order updatedOrder = Order.builder()
                .orderStatus("COMPLETED")
                .build();

        when(orderRepository.findById(order.getOrderId())).thenReturn(of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.updateOrder(order.getOrderId(), updatedOrder);

        assertNotNull(result);
        assertEquals("COMPLETED", result.getOrderStatus());
        verify(orderRepository, times(1)).findById(order.getOrderId());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void testCancelOrder() {

        when(orderRepository.findById(order.getOrderId())).thenReturn(of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderService.cancelOrder(order.getOrderId());

        assertEquals("CANCELLED", order.getOrderStatus());
        verify(orderRepository, times(1)).findById(order.getOrderId());
        verify(orderRepository, times(1)).save(order);
    }
}

