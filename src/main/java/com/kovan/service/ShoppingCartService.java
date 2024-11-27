package com.kovan.service;

import com.kovan.entities.Book;
import com.kovan.entities.CartItem;
import com.kovan.entities.ShoppingCart;
import com.kovan.entities.User;
import com.kovan.repository.BookRepository;
import com.kovan.repository.CartItemRepository;
import com.kovan.repository.ShoppingCartRepository;
import com.kovan.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;

    private final CartItemRepository cartItemRepository;

    private final BookRepository bookRepository;

    private final UserRepository userRepository;

    public ShoppingCartService(ShoppingCartRepository shoppingCartRepository, CartItemRepository cartItemRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public ShoppingCart getOrCreateCart(Long userId) {
        return shoppingCartRepository.findByUserUserId(userId)
                .orElseGet(() -> {
                    ShoppingCart cart = ShoppingCart.builder().build();
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
                    cart.setUser(user);
                    cart.setCreatedDate(LocalDate.now());
                    cart.setLastUpdatedDate(LocalDate.now());
                    return shoppingCartRepository.save(cart);
                });
    }

    public CartItem addToCart(Long userId, Long bookId, int quantity) {
        ShoppingCart cart = getOrCreateCart(userId);
        Optional<CartItem> existingItem = cartItemRepository.findByCartCartId(cart.getCartId());

        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            return cartItemRepository.save(cartItem);
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found!"));

        CartItem newItem = CartItem.builder()
                .cart(cart)
                .book(book)
                .quantity(quantity).build();
        return cartItemRepository.save(newItem);
    }

    public void removeFromCart(Long userId, Long cartItemId) {
        ShoppingCart cart = getOrCreateCart(userId);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found!"));

        if (!cart.getCartId().equals(cartItem.getCart().getCartId())) {
            throw new RuntimeException("Item does not belong to this cart!");
        }
        cartItemRepository.delete(cartItem);
    }

    public ShoppingCart viewCart(Long userId) {
        return getOrCreateCart(userId);
    }
}

