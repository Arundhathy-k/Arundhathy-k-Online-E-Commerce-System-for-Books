package com.kovan.service;

import com.kovan.entities.Address;
import com.kovan.entities.Order;
import com.kovan.entities.Payment;
import com.kovan.entities.User;
import com.kovan.repository.OrderRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final AddressService addressService;

    public OrderService(OrderRepository orderRepository, UserService userService, AddressService addressService) {
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.addressService = addressService;
    }

    public Order createOrder(Order order) {
       Long userId = order.getUser().getUserId();
       Long addressId = order.getShippingAddress().getAddressId();

       Address address = addressService.getAddressesById(addressId);
       User user = userService.getUserById(userId);
       Payment payment = order.getPayment();

       order.setPayment(payment);
       order.setUser(user);
       order.setShippingAddress(address);
        return orderRepository.save(order);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found!"));
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order updateOrder(Long id, Order updatedOrder) {
        Order order = getOrderById(id);
        order.setOrderStatus(updatedOrder.getOrderStatus());
        return orderRepository.save(order);
    }

    public void cancelOrder(Long id) {
        Order order = getOrderById(id);
        order.setOrderStatus("CANCELLED");
        orderRepository.save(order);
    }
}

