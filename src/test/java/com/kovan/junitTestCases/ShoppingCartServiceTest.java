package com.kovan.junitTestCases;

import com.kovan.entities.Book;
import com.kovan.entities.CartItem;
import com.kovan.entities.ShoppingCart;
import com.kovan.entities.User;
import com.kovan.repository.BookRepository;
import com.kovan.repository.CartItemRepository;
import com.kovan.repository.ShoppingCartRepository;
import com.kovan.repository.UserRepository;
import com.kovan.service.ShoppingCartService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Instant;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceTest {

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ShoppingCartService shoppingCartService;

    private final User user = User.builder()
                .userId(1L)
                .build();

    private final Book book = Book.builder()
                .bookId(1L)
                .build();

    private final ShoppingCart cart = ShoppingCart.builder()
                .cartId(1L)
                .user(user)
                .createdDate(Instant.now().toString())
                .lastUpdatedDate(Instant.now().toString())
                .build();

    private final CartItem cartItem = CartItem.builder()
                .cartItemId(1L)
                .cart(cart)
                .book(book)
                .quantity(2)
                .build();

    @Test
    void testGetOrCreateCart_NewCart() {

        when(shoppingCartRepository.findByUserUserId(user.getUserId())).thenReturn(empty());
        when(userRepository.findById(user.getUserId())).thenReturn(of(user));
        when(shoppingCartRepository.save(any(ShoppingCart.class))).thenReturn(cart);

        ShoppingCart result = shoppingCartService.getOrCreateCart(user.getUserId());

        assertNotNull(result);
        assertEquals(cart.getCartId(), result.getCartId());
        verify(shoppingCartRepository, times(1)).findByUserUserId(user.getUserId());
        verify(userRepository, times(1)).findById(user.getUserId());
        verify(shoppingCartRepository, times(1)).save(any(ShoppingCart.class));
    }

    @Test
    void testGetOrCreateCart_ExistingCart() {

        when(shoppingCartRepository.findByUserUserId(user.getUserId())).thenReturn(of(cart));

        ShoppingCart result = shoppingCartService.getOrCreateCart(user.getUserId());

        assertNotNull(result);
        assertEquals(cart.getCartId(), result.getCartId());
        verify(shoppingCartRepository, times(1)).findByUserUserId(user.getUserId());
        verifyNoInteractions(userRepository);
    }

    @Test
    void testAddToCart_NewItem() {

        when(shoppingCartRepository.findByUserUserId(user.getUserId())).thenReturn(of(cart));
        when(cartItemRepository.findByCartCartId(cart.getCartId())).thenReturn(empty());
        when(bookRepository.findById(book.getBookId())).thenReturn(of(book));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);

        CartItem result = shoppingCartService.addToCart(user.getUserId(), book.getBookId(), 2);

        assertNotNull(result);
        assertEquals(cartItem.getCartItemId(), result.getCartItemId());
        assertEquals(2, result.getQuantity());
        verify(cartItemRepository, times(1)).findByCartCartId(cart.getCartId());
        verify(bookRepository, times(1)).findById(book.getBookId());
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    void testAddToCart_UpdateExistingItem() {

        when(shoppingCartRepository.findByUserUserId(user.getUserId())).thenReturn(of(cart));
        when(cartItemRepository.findByCartCartId(cart.getCartId())).thenReturn(of(cartItem));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);

        CartItem result = shoppingCartService.addToCart(user.getUserId(), book.getBookId(), 3);

        assertNotNull(result);
        assertEquals(cartItem.getCartItemId(), result.getCartItemId());
        assertEquals(5, result.getQuantity()); // 2 (existing) + 3 (new)
        verify(cartItemRepository, times(1)).findByCartCartId(cart.getCartId());
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
        verifyNoInteractions(bookRepository);
    }

    @Test
    void testRemoveFromCart_Success() {

        when(shoppingCartRepository.findByUserUserId(user.getUserId())).thenReturn(of(cart));
        when(cartItemRepository.findById(cartItem.getCartItemId())).thenReturn(of(cartItem));

        shoppingCartService.removeFromCart(user.getUserId(), cartItem.getCartItemId());

        verify(cartItemRepository, times(1)).findById(cartItem.getCartItemId());
        verify(cartItemRepository, times(1)).delete(cartItem);
    }

    @Test
    void testRemoveFromCart_ItemNotInCart() {

        when(shoppingCartRepository.findByUserUserId(user.getUserId())).thenReturn(of(cart));
        when(cartItemRepository.findById(cartItem.getCartItemId())).thenReturn(of(cartItem));

        ShoppingCart anotherCart = ShoppingCart.builder().cartId(2L).build();
        cartItem.setCart(anotherCart);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            shoppingCartService.removeFromCart(user.getUserId(), cartItem.getCartItemId());
        });

        assertEquals("Item does not belong to this cart!", exception.getMessage());
        verify(cartItemRepository, times(1)).findById(cartItem.getCartItemId());
        verify(cartItemRepository, never()).delete(any());
    }

    @Test
    void testViewCart() {

        when(shoppingCartRepository.findByUserUserId(user.getUserId())).thenReturn(of(cart));

        ShoppingCart result = shoppingCartService.viewCart(user.getUserId());

        assertNotNull(result);
        assertEquals(cart.getCartId(), result.getCartId());
        verify(shoppingCartRepository, times(1)).findByUserUserId(user.getUserId());
    }
}

