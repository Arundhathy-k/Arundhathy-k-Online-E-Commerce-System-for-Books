package com.kovan.controller;

import com.kovan.entities.CartItem;
import com.kovan.entities.ShoppingCart;
import com.kovan.service.ShoppingCartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shoppingCart")
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ShoppingCart> getOrCreateCart(@PathVariable Long userId) {
        return ResponseEntity.ok(shoppingCartService.getOrCreateCart(userId));
    }

    @PostMapping("/add")
    public ResponseEntity<CartItem> addToCart(@RequestParam Long userId,
                                              @RequestParam Long bookId,
                                              @RequestParam int quantity) {
        return ResponseEntity.status(HttpStatus.CREATED).body(shoppingCartService.addToCart(userId, bookId, quantity));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeFromCart(@RequestParam Long userId,
                                               @RequestParam Long cartItemId) {
        shoppingCartService.removeFromCart(userId, cartItemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/view/{userId}")
    public ResponseEntity<ShoppingCart> viewCart(@PathVariable Long userId) {
        return ResponseEntity.ok(shoppingCartService.viewCart(userId));
    }
}

