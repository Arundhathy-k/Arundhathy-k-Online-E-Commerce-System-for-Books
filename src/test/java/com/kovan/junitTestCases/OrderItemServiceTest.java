package com.kovan.junitTestCases;

import com.kovan.entities.Book;
import com.kovan.entities.Order;
import com.kovan.entities.OrderItem;
import com.kovan.repository.BookRepository;
import com.kovan.repository.OrderItemRepository;
import com.kovan.repository.OrderRepository;
import com.kovan.service.OrderItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static java.util.Optional.of;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private OrderItemService orderItemService;

    private final Order order = Order.builder()
            .orderId(1L)
            .orderStatus("PENDING")
            .build();

    private final Book book =  Book.builder()
            .bookId(1L)
            .title("Test Book")
           .price(50.00)
            .build();

    private final OrderItem orderItem = OrderItem.builder()
            .orderItemId(1L)
            .order(order)
            .book(book)
            .quantity(2)
            .unitPrice(50.00)
            .totalPrice(100.00)
            .build();

    @Test
    void testAddOrderItem() {

        when(orderRepository.findById(order.getOrderId())).thenReturn(of(order));
        when(bookRepository.findById(book.getBookId())).thenReturn(of(book));
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(orderItem);

        OrderItem result = orderItemService.addOrderItem(
                order.getOrderId(),
                book.getBookId(),
                2,
                (50.00)
        );

        assertNotNull(result);
        assertEquals(orderItem.getOrderItemId(), result.getOrderItemId());
        assertEquals(orderItem.getTotalPrice(), result.getTotalPrice());
        verify(orderRepository, times(1)).findById(order.getOrderId());
        verify(bookRepository, times(1)).findById(book.getBookId());
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
    }

    @Test
    void testGetAllOrderItems() {

        List<OrderItem> orderItems = List.of(orderItem);
        when(orderItemRepository.findAll()).thenReturn(orderItems);

        List<OrderItem> result = orderItemService.getAllOrderItems();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(orderItem, result.getFirst());
        verify(orderItemRepository, times(1)).findAll();
    }

    @Test
    void testGetOrderItemById() {

        when(orderItemRepository.findById(orderItem.getOrderItemId())).thenReturn(of(orderItem));

        OrderItem result = orderItemService.getOrderItemById(orderItem.getOrderItemId());

        assertNotNull(result);
        assertEquals(orderItem.getOrderItemId(), result.getOrderItemId());
        verify(orderItemRepository, times(1)).findById(orderItem.getOrderItemId());
    }

    @Test
    void testGetOrderItemByIdThrowsExceptionWhenNotFound() {

        when(orderItemRepository.findById(orderItem.getOrderItemId())).thenReturn(java.util.Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> orderItemService.getOrderItemById(orderItem.getOrderItemId()));
        verify(orderItemRepository, times(1)).findById(orderItem.getOrderItemId());
    }

    @Test
    void testDeleteOrderItem() {

        when(orderItemRepository.existsById(orderItem.getOrderItemId())).thenReturn(true);

        orderItemService.deleteOrderItem(orderItem.getOrderItemId());

        verify(orderItemRepository, times(1)).existsById(orderItem.getOrderItemId());
        verify(orderItemRepository, times(1)).deleteById(orderItem.getOrderItemId());
    }

    @Test
    void testDeleteOrderItemThrowsExceptionWhenNotFound() {

        when(orderItemRepository.existsById(orderItem.getOrderItemId())).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> orderItemService.deleteOrderItem(orderItem.getOrderItemId()));
        verify(orderItemRepository, times(1)).existsById(orderItem.getOrderItemId());
    }
}

