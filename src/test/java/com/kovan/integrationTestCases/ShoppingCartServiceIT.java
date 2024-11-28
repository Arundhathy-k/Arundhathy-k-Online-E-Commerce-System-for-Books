package com.kovan.integrationTestCases;

import com.kovan.entities.*;
import com.kovan.repository.*;
import com.kovan.service.ShoppingCartService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ShoppingCartServiceIT {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private User testUser;
    private Book testBook;

    @BeforeAll
    void setup() {

        testUser = userRepository.save(
                User.builder()
                        .firstName("Alice")
                        .email("alice@example.com")
                        .build()
        );
        Category testCategory = Category.builder().name("Fiction").build();
        categoryRepository.save(testCategory);

        testBook = bookRepository.save(
                Book.builder()
                        .title("Effective Java")
                        .author("Joshua Bloch")
                        .price(BigDecimal.valueOf(50.00))
                        .category(testCategory)
                        .build()
        );
    }

    @AfterEach
    void cleanup() {
        cartItemRepository.deleteAll();
        shoppingCartRepository.deleteAll();
    }

    @AfterAll
    void teardown() {
        bookRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testGetOrCreateCart_CreateNewCart() {

        ShoppingCart cart = shoppingCartService.getOrCreateCart(testUser.getUserId());

        assertNotNull(cart.getCartId());
        assertEquals(testUser.getUserId(), cart.getUser().getUserId());
        assertEquals(LocalDate.now(), cart.getCreatedDate());
        assertEquals(LocalDate.now(), cart.getLastUpdatedDate());
    }

    @Test
    void testAddToCart_AddNewItem() {

        CartItem cartItem = shoppingCartService.addToCart(testUser.getUserId(), testBook.getBookId(), 2);

        assertNotNull(cartItem.getCartItemId());
        assertEquals(2, cartItem.getQuantity());
        assertEquals(testBook.getBookId(), cartItem.getBook().getBookId());
        assertEquals(testUser.getUserId(), cartItem.getCart().getUser().getUserId());
    }

    @Test
    void testAddToCart_UpdateExistingItem() {

        ShoppingCart cart = shoppingCartService.getOrCreateCart(testUser.getUserId());
        cartItemRepository.save(
                CartItem.builder()
                        .cart(cart)
                        .book(testBook)
                        .quantity(3)
                        .build()
        );

        CartItem updatedItem = shoppingCartService.addToCart(testUser.getUserId(), testBook.getBookId(), 2);

        assertEquals(5, updatedItem.getQuantity());
    }

    @Test
    void testViewCart() {

        ShoppingCart cart = shoppingCartService.getOrCreateCart(testUser.getUserId());
        cartItemRepository.save(
                CartItem.builder()
                        .cart(cart)
                        .book(testBook)
                        .quantity(2)
                        .build()
        );

        ShoppingCart fetchedCart = shoppingCartService.viewCart(testUser.getUserId());

        assertNotNull(fetchedCart);

    }

    @Test
    void testRemoveFromCart_SuccessfulRemoval() {

        ShoppingCart cart = shoppingCartService.getOrCreateCart(testUser.getUserId());
        CartItem cartItem = cartItemRepository.save(
                CartItem.builder()
                        .cart(cart)
                        .book(testBook)
                        .quantity(1)
                        .build()
        );

        shoppingCartService.removeFromCart(testUser.getUserId(), cartItem.getCartItemId());

        assertFalse(cartItemRepository.existsById(cartItem.getCartItemId()));
    }

}

