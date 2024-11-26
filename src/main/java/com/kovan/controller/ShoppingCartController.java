package com.kovan.controller;

import com.kovan.entities.CartItem;
import com.kovan.entities.ShoppingCart;
import com.kovan.service.ShoppingCartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shopping-cart")
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ShoppingCart> getOrCreateCart(@PathVariable Long userId) {
        ShoppingCart cart = shoppingCartService.getOrCreateCart(userId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/add")
    public ResponseEntity<CartItem> addToCart(@RequestParam Long userId,
                                              @RequestParam Long bookId,
                                              @RequestParam int quantity) {
        CartItem cartItem = shoppingCartService.addToCart(userId, bookId, quantity);
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItem);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeFromCart(@RequestParam Long userId,
                                               @RequestParam Long cartItemId) {
        shoppingCartService.removeFromCart(userId, cartItemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/view/{userId}")
    public ResponseEntity<ShoppingCart> viewCart(@PathVariable Long userId) {
        ShoppingCart cart = shoppingCartService.viewCart(userId);
        return ResponseEntity.ok(cart);
    }
}

