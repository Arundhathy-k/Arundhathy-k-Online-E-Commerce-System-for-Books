package com.kovan.controller;

import com.kovan.entities.CartItem;
import com.kovan.service.CartItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart-items")
public class CartItemController {

    private final CartItemService cartItemService;

    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @PostMapping("/add")
    public ResponseEntity<CartItem> addCartItem(@RequestParam Long cartId,
                                                @RequestParam Long bookId,
                                                @RequestParam int quantity) {
        CartItem cartItem = cartItemService.addCartItem(cartId, bookId, quantity);
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItem);
    }

    @GetMapping("/fetch")
    public ResponseEntity<List<CartItem>> getCartItemsByCartId() {
        List<CartItem> cartItems = cartItemService.getAllCartItems();
        return ResponseEntity.ok(cartItems);
    }

    @PutMapping("/{cartItemId}")
    public ResponseEntity<Void> updateCartItem(@PathVariable Long cartItemId,
                                               @RequestParam int quantity) {
        cartItemService.updateCartItem(cartItemId, quantity);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long cartItemId) {
        cartItemService.deleteCartItem(cartItemId);
        return ResponseEntity.noContent().build();
    }
}

