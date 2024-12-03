package com.kovan.controller;

import com.kovan.entities.OrderItem;
import com.kovan.service.OrderItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/order-items")
public class OrderItemController {

    private final OrderItemService orderItemService;

    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @PostMapping("/add")
    public ResponseEntity<OrderItem> addOrderItem(@RequestParam Long orderId,
                                                  @RequestParam Long bookId,
                                                  @RequestParam int quantity,
                                                  @RequestParam Double unitPrice) {
        OrderItem orderItem = orderItemService.addOrderItem(bookId, quantity, unitPrice);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderItem);
    }

    @GetMapping("/fetch")
    public ResponseEntity<List<OrderItem>> getAllOrderItems() {
        List<OrderItem> orderItems = orderItemService.getAllOrderItems();
        return ResponseEntity.ok(orderItems);
    }

    @GetMapping("/{orderItemId}")
    public ResponseEntity<OrderItem> getOrderItemById(@PathVariable Long orderItemId) {
        OrderItem orderItem = orderItemService.getOrderItemById(orderItemId);
        return ResponseEntity.ok(orderItem);
    }

    @DeleteMapping("/{orderItemId}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Long orderItemId) {
        orderItemService.deleteOrderItem(orderItemId);
        return ResponseEntity.noContent().build();
    }
}
