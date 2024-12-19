package com.kovan.junitTestCases;

import com.kovan.entities.Book;
import com.kovan.entities.Order;
import com.kovan.entities.OrderItem;
import com.kovan.entities.Payment;
import com.kovan.repository.BookRepository;
import com.kovan.repository.OrderRepository;
import com.kovan.repository.PaymentRepository;
import com.kovan.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private PaymentService paymentService;

    private final Payment payment = Payment.builder()
            .paymentId(1L)
            .paymentDate(LocalDate.now())
            .paymentMethod("CREDIT_CARD")
            .paymentStatus("COMPLETED")
            .amount(200.00)
            .paymentReferenceNumber("PAY12345")
            .transactionQuantity(2)
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
            .orderStatus("PENDING")
            .payment(payment)
            .orderItems(List.of(orderItem))
            .build();

    @Test
    void testProcessPayment() {
        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(bookRepository.findById(book.getBookId())).thenReturn(Optional.of(book));

        Payment result = paymentService.processPayment(order.getOrderId(), payment);

        assertNotNull(result);
        assertEquals(payment.getPaymentId(), result.getPaymentId());
        assertEquals("COMPLETED", result.getPaymentStatus());
        verify(orderRepository, times(1)).findById(order.getOrderId());
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(orderRepository, times(1)).save(order);
        verify(bookRepository, times(1)).save(book);
        assertEquals(8, book.getStockQuantity());
    }

    @Test
    void testProcessPaymentThrowsExceptionForNonPendingOrder() {
        order.setOrderStatus("SHIPPED");
        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            paymentService.processPayment(order.getOrderId(), payment);
        });

        assertEquals("Payment can only be processed for pending orders!", exception.getMessage());
        verify(orderRepository, times(1)).findById(order.getOrderId());
        verifyNoInteractions(paymentRepository);
    }

    @Test
    void testGetPaymentById() {
        when(paymentRepository.findById(payment.getPaymentId())).thenReturn(Optional.of(payment));

        Payment result = paymentService.getPaymentById(payment.getPaymentId());

        assertNotNull(result);
        assertEquals(payment.getPaymentId(), result.getPaymentId());
        verify(paymentRepository, times(1)).findById(payment.getPaymentId());
    }

    @Test
    void testGetPaymentByIdThrowsExceptionWhenNotFound() {
        when(paymentRepository.findById(payment.getPaymentId())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            paymentService.getPaymentById(payment.getPaymentId());
        });

        assertEquals("Payment not found!", exception.getMessage());
        verify(paymentRepository, times(1)).findById(payment.getPaymentId());
    }

    @Test
    void testGetAllPayments() {
        List<Payment> payments = List.of(payment);
        when(paymentRepository.findAll()).thenReturn(payments);

        List<Payment> result = paymentService.getAllPayments();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(payment, result.getFirst());
        verify(paymentRepository, times(1)).findAll();
    }

    @Test
    void testUpdatePayment() {
        Payment updatedPayment = Payment.builder()
                .paymentMethod("DEBIT_CARD")
                .paymentStatus("COMPLETED")
                .amount(250.00)
                .paymentReferenceNumber("PAY54321")
                .transactionQuantity(2)
                .build();

        when(paymentRepository.findById(payment.getPaymentId())).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(updatedPayment);
        when(orderRepository.findByPayment(any(Payment.class))).thenReturn(Optional.of(order));
        when(bookRepository.findById(book.getBookId())).thenReturn(Optional.of(book));

        Payment result = paymentService.updatePayment(payment.getPaymentId(), updatedPayment);

        assertNotNull(result);
        assertEquals("DEBIT_CARD", result.getPaymentMethod());
        assertEquals("COMPLETED", result.getPaymentStatus());
        assertEquals(250.00, result.getAmount());
        verify(paymentRepository, times(1)).findById(payment.getPaymentId());
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(orderRepository, times(1)).findByPayment(any(Payment.class));
        verify(orderRepository, times(1)).save(order);
        verify(bookRepository, times(1)).save(book);
        assertEquals(8, book.getStockQuantity());
    }

    @Test
    void testDeletePayment() {
        Payment paymentForDelete = Payment.builder()
                .paymentId(1L)
                .paymentDate(LocalDate.now())
                .paymentMethod("CREDIT_CARD")
                .paymentStatus("PENDING")
                .amount(200.00)
                .paymentReferenceNumber("PAY12345")
                .build();
        when(paymentRepository.findById(paymentForDelete.getPaymentId())).thenReturn(Optional.of(paymentForDelete));

        paymentService.deletePayment(paymentForDelete.getPaymentId());

        verify(paymentRepository, times(1)).findById(paymentForDelete.getPaymentId());
        verify(paymentRepository, times(1)).deleteById(paymentForDelete.getPaymentId());
    }

    @Test
    void testDeletePaymentThrowsExceptionForCompletedPayment() {
        payment.setPaymentStatus("COMPLETED");
        when(paymentRepository.findById(payment.getPaymentId())).thenReturn(Optional.of(payment));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            paymentService.deletePayment(payment.getPaymentId());
        });

        assertEquals("Cannot delete a completed payment associated with an order!", exception.getMessage());
        verify(paymentRepository, times(1)).findById(payment.getPaymentId());
        verify(paymentRepository, never()).deleteById(any());
    }
}
