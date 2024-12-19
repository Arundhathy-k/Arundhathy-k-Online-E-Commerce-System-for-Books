package com.kovan.junitTestCases;

import com.kovan.entities.*;
import com.kovan.repository.*;
import com.kovan.service.CartItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartItemServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private CartItemService cartItemService;

    private final ShoppingCart  cart = ShoppingCart.builder()
            .cartId(1L)
            .build();
    private final Book book = Book.builder()
            .bookId(1L)
            .title("Effective Java")
            .build();
    private final CartItem cartItem = CartItem.builder()
            .cartItemId(1L)
            .cart(cart)
            .book(book)
            .quantity(2)
            .build();

    @Test
    void testAddCartItem() {
        when(shoppingCartRepository.findById(cart.getCartId())).thenReturn(of(cart));
        when(bookRepository.findById(book.getBookId())).thenReturn(of(book));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);

        CartItem result = cartItemService.addCartItem(cart.getCartId(), book.getBookId(), 2);

        assertNotNull(result);
        assertEquals(cart, result.getCart());
        assertEquals(book, result.getBook());
        assertEquals(2, result.getQuantity());
        verify(shoppingCartRepository, times(1)).findById(cart.getCartId());
        verify(bookRepository, times(1)).findById(book.getBookId());
        verify(cartItemRepository,times(1)).save(any(CartItem.class));
    }

    @Test
    void testAddCartItemThrowsExceptionWhenCartNotFound() {

        when(shoppingCartRepository.findById(cart.getCartId())).thenReturn(empty());

        assertThrows(IllegalArgumentException.class,
                () -> cartItemService.addCartItem(cart.getCartId(), book.getBookId(), 2));

        verify(shoppingCartRepository, times(1)).findById(cart.getCartId());
        verify(bookRepository, never()).findById(anyLong());
        verify(cartItemRepository,never()).save(any(CartItem.class));
    }

    @Test
    void testAddCartItemThrowsExceptionWhenBookNotFound() {

       when(shoppingCartRepository.findById(cart.getCartId())).thenReturn(of(cart));
       when(bookRepository.findById(book.getBookId())).thenReturn(empty());

       assertThrows(IllegalArgumentException.class,
                () -> cartItemService.addCartItem(cart.getCartId(), book.getBookId(), 2));

       verify(shoppingCartRepository,times(1)).findById(cart.getCartId());
       verify(bookRepository,times(1)).findById(book.getBookId());
       verify(cartItemRepository,never()).save(any(CartItem.class));
    }

    @Test
    void testUpdateCartItem() {

       when(cartItemRepository.findById(cartItem.getCartItemId())).thenReturn(of(cartItem));
       when(cartItemRepository.save(cartItem)).thenReturn(cartItem);

       cartItemService.updateCartItem(cartItem.getCartItemId(), 5);

       assertEquals(5, cartItem.getQuantity());
       verify(cartItemRepository, times(1)).findById(cartItem.getCartItemId());
       verify(cartItemRepository, times(1)).save(cartItem);
    }

    @Test
    void testUpdateCartItemThrowsExceptionWhenNotFound() {

       when(cartItemRepository.findById(cartItem.getCartItemId())).thenReturn(empty());

        assertThrows(IllegalArgumentException.class,
                () -> cartItemService.updateCartItem(cartItem.getCartItemId(), 5));

        verify(cartItemRepository, times(1)).findById(cartItem.getCartItemId());
        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    void testDeleteCartItem() {

        cartItemService.deleteCartItem(cartItem.getCartItemId());

        verify(cartItemRepository, times(1)).deleteById(cartItem.getCartItemId());
    }

    @Test
    void testGetAllCartItems() {

        List<CartItem> cartItems = List.of(cartItem);
        when(cartItemRepository.findAll()).thenReturn(cartItems);

        List<CartItem> result = cartItemService.getAllCartItems();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(cartItem, result.getFirst());
        verify(cartItemRepository, times(1)).findAll();
    }
}

