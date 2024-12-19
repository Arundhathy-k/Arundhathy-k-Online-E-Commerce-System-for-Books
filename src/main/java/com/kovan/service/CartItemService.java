package com.kovan.service;

import com.kovan.entities.Book;
import com.kovan.entities.CartItem;
import com.kovan.entities.ShoppingCart;
import com.kovan.repository.BookRepository;
import com.kovan.repository.CartItemRepository;
import com.kovan.repository.ShoppingCartRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final BookRepository bookRepository;

    public CartItemService(CartItemRepository cartItemRepository,
                           ShoppingCartRepository shoppingCartRepository,
                           BookRepository bookRepository) {
        this.cartItemRepository = cartItemRepository;
        this.shoppingCartRepository = shoppingCartRepository;
        this.bookRepository = bookRepository;
    }

    public CartItem addCartItem(Long cartId, Long bookId, int quantity) {
        ShoppingCart cart = shoppingCartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Shopping cart not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .book(book)
                .quantity(quantity).build();

        return cartItemRepository.save(cartItem);
    }

    public CartItem updateCartItem(Long cartItemId, int quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));
        cartItem.setQuantity(quantity);
        return cartItemRepository.save(cartItem);

    }

    public void deleteCartItem(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }
    public List<CartItem> getAllCartItems(){
        return cartItemRepository.findAll();
    }
}

