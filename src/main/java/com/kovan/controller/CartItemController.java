package com.kovan.controller;

import com.kovan.entities.CartItem;
import com.kovan.service.CartItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cartItem")
public class CartItemController {

    private final CartItemService cartItemService;

    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @PostMapping("/add")
    public ResponseEntity<CartItem> addCartItem(@RequestParam Long cartId,
                                                @RequestParam Long bookId,
                                                @RequestParam int quantity) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemService.addCartItem(cartId, bookId, quantity));
    }

    @GetMapping("/fetch")
    public ResponseEntity<List<CartItem>> getCartItemsByCartId() {
        return ResponseEntity.ok(cartItemService.getAllCartItems());
    }

    @PutMapping("/{cartItemId}")
    public ResponseEntity<CartItem> updateCartItem(@PathVariable Long cartItemId,
                                               @RequestParam int quantity) {
        return ResponseEntity.ok(cartItemService.updateCartItem(cartItemId, quantity));
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long cartItemId) {
        cartItemService.deleteCartItem(cartItemId);
        return ResponseEntity.noContent().build();
    }
}

