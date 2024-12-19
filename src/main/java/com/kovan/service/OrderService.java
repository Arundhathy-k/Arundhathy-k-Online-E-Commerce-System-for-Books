package com.kovan.service;

import com.kovan.entities.*;
import com.kovan.repository.BookRepository;
import com.kovan.repository.OrderRepository;
import com.kovan.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final AddressService addressService;
    private final PaymentRepository paymentRepository;
    private final BookRepository bookRepository;

    public OrderService(OrderRepository orderRepository, UserService userService, AddressService addressService, PaymentRepository paymentRepository, BookRepository bookRepository) {
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.addressService = addressService;
        this.paymentRepository = paymentRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public Order createOrder(Order order) {
        Long userId = order.getUser().getUserId();
        Long addressId = order.getShippingAddress().getAddressId();

        Address address = addressService.getAddressesById(addressId);
        User user = userService.getUserById(userId);

        order.setUser(user);
        order.setShippingAddress(address);
        order.setOrderStatus("PENDING");

        return orderRepository.save(order);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found!"));
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional
    public Order updateOrder(Long id, Order updatedOrder) {
        Order existingOrder = getOrderById(id);

        if ("SHIPPED".equalsIgnoreCase(existingOrder.getOrderStatus())) {
            throw new RuntimeException("Cannot update a shipped order!");
        }

        existingOrder.setOrderStatus(updatedOrder.getOrderStatus());
        return orderRepository.save(existingOrder);
    }

    @Transactional
    public void cancelOrder(Long id) {
        Order order = getOrderById(id);

        if ("SHIPPED".equalsIgnoreCase(order.getOrderStatus())) {
            throw new RuntimeException("Cannot cancel a shipped order!");
        }

        order.setOrderStatus("CANCELLED");
        refundPayment(order);
        orderRepository.save(order);
    }

    private void refundPayment(Order order) {
        Payment payment = order.getPayment();
        if (payment != null && "COMPLETED".equalsIgnoreCase(payment.getPaymentStatus())) {
            payment.setPaymentStatus("REFUNDED");
            payment.setTransactionType("RETURN");
            paymentRepository.save(payment);
            incrementStock(order,payment.getTransactionQuantity());
        }
    }

    private void incrementStock(Order order, int transactionQuantity) {
        order.getOrderItems().forEach(orderItem -> {
            Book book = bookRepository.findById(orderItem.getBook().getBookId())
                    .orElseThrow(() -> new RuntimeException("Book not found!"));
            book.setStockQuantity(book.getStockQuantity() + transactionQuantity);
            bookRepository.save(book);
        });
    }
}
