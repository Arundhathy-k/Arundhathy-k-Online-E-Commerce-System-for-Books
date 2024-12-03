package com.kovan.junitTestCases;

import com.kovan.entities.Order;
import com.kovan.entities.Payment;
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
import static java.util.Optional.of;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PaymentService paymentService;

    private final Payment payment= Payment.builder()
                .paymentId(1L)
                .paymentDate(LocalDate.now())
                .paymentMethod("CREDIT_CARD")
                .paymentStatus("COMPLETED")
                .amount(200.00)
                .paymentReferenceNumber("PAY12345")
                .build();

    private final Order order = Order.builder()
            .orderId(1L)
            .orderStatus("PENDING")
            .payment(payment)
            .build();

    @Test
    void testProcessPayment() {

        when(orderRepository.findById(order.getOrderId())).thenReturn(of(order));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        Payment result = paymentService.processPayment(order.getOrderId(), payment);

        assertNotNull(result);
        assertEquals(payment.getPaymentId(), result.getPaymentId());
        assertEquals("COMPLETED", result.getPaymentStatus());
        verify(orderRepository, times(1)).findById(order.getOrderId());
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void testProcessPaymentThrowsExceptionForNonPendingOrder() {

        order.setOrderStatus("SHIPPED");
        when(orderRepository.findById(order.getOrderId())).thenReturn(of(order));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            paymentService.processPayment(order.getOrderId(), payment);
        });
        assertEquals("Payment can only be processed for pending orders!", exception.getMessage());
        verify(orderRepository, times(1)).findById(order.getOrderId());
        verifyNoInteractions(paymentRepository);
    }

    @Test
    void testGetPaymentById() {

        when(paymentRepository.findById(payment.getPaymentId())).thenReturn(of(payment));

        Payment result = paymentService.getPaymentById(payment.getPaymentId());

        assertNotNull(result);
        assertEquals(payment.getPaymentId(), result.getPaymentId());
        verify(paymentRepository, times(1)).findById(payment.getPaymentId());
    }

    @Test
    void testGetPaymentByIdThrowsExceptionWhenNotFound() {

        when(paymentRepository.findById(payment.getPaymentId())).thenReturn(java.util.Optional.empty());

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
                .build();

        when(paymentRepository.findById(payment.getPaymentId())).thenReturn(of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(updatedPayment);
        when(orderRepository.findByPayment(any(Payment.class))).thenReturn(of(order));

        Payment result = paymentService.updatePayment(payment.getPaymentId(), updatedPayment);

        assertNotNull(result);
        assertEquals("DEBIT_CARD", result.getPaymentMethod());
        assertEquals("COMPLETED", result.getPaymentStatus());
        assertEquals(250.00, result.getAmount());
        verify(paymentRepository, times(1)).findById(payment.getPaymentId());
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(orderRepository, times(1)).findByPayment(any(Payment.class));
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void testDeletePayment() {

        Payment paymentForDelete= Payment.builder()
                .paymentId(1L)
                .paymentDate(LocalDate.now())
                .paymentMethod("CREDIT_CARD")
                .paymentStatus("PENDING")
                .amount(200.00)
                .paymentReferenceNumber("PAY12345")
                .build();
        when(paymentRepository.findById(paymentForDelete.getPaymentId())).thenReturn(of(paymentForDelete));

        paymentService.deletePayment(paymentForDelete.getPaymentId());

        verify(paymentRepository, times(1)).findById(paymentForDelete.getPaymentId());
        verify(paymentRepository, times(1)).deleteById(paymentForDelete.getPaymentId());
    }

    @Test
    void testDeletePaymentThrowsExceptionForCompletedPayment() {

        payment.setPaymentStatus("COMPLETED");
        when(paymentRepository.findById(payment.getPaymentId())).thenReturn(of(payment));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            paymentService.deletePayment(payment.getPaymentId());
        });
        assertEquals("Cannot delete a completed payment associated with an order!", exception.getMessage());
        verify(paymentRepository, times(1)).findById(payment.getPaymentId());
        verify(paymentRepository, times(0)).deleteById(any());
    }
}

