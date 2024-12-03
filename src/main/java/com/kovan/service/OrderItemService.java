package com.kovan.service;

import com.kovan.entities.Book;
import com.kovan.entities.OrderItem;
import com.kovan.repository.BookRepository;
import com.kovan.repository.OrderItemRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final BookRepository bookRepository;

    public OrderItemService(OrderItemRepository orderItemRepository,
                            BookRepository bookRepository) {
        this.orderItemRepository = orderItemRepository;
        this.bookRepository = bookRepository;
    }

    public OrderItem addOrderItem(Long bookId, int quantity, Double unitPrice) {


        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

       Double totalPrice = unitPrice * quantity;

        OrderItem orderItem = OrderItem.builder()
                .book(book)
                .quantity(quantity)
                .unitPrice(unitPrice)
                .totalPrice(totalPrice).build();

        return orderItemRepository.save(orderItem);
    }

    public List<OrderItem> getAllOrderItems() {
        return orderItemRepository.findAll();
    }

    public OrderItem getOrderItemById(Long orderItemId) {
        return orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new IllegalArgumentException("Order item not found"));
    }

    public void deleteOrderItem(Long orderItemId) {
        if (!orderItemRepository.existsById(orderItemId)) {
            throw new IllegalArgumentException("Order item not found");
        }
        orderItemRepository.deleteById(orderItemId);
    }
}

